package com.mindscribe.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.concurrent.Task;
import javafx.application.Platform;
import com.mindscribe.ui.BackendDiaryService;
import com.mindscribe.util.ViewSwitcher;
import com.mindscribe.service.AIService;
import com.mindscribe.service.AnalysisResult;
import com.mindscribe.ui.SessionManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * JavaFX Controller for the Editor view (Journal writing)
 */
public class EditorViewController {
    
    @FXML
    private Button btnBack;
    
    @FXML
    private Label lblDate;
    
    @FXML
    private Button btnHappy;
    
    @FXML
    private Button btnSad;
    
    @FXML
    private Button btnAnxious;
    
    @FXML
    private Button btnNeutral;
    
    @FXML
    private TextArea txtJournalEntry;
    
    @FXML
    private Label lblStatus;
    
    @FXML
    private Button btnAnalyze;
    
    @FXML
    private Button btnSave;
    
    @FXML
    private VBox analysisContainer;
    
    @FXML
    private Label lblAnalysisResults;
    
    private AIService aiService;
    private String selectedMood;
    
    public EditorViewController() {
        this.aiService = new AIService();
        this.selectedMood = "neutral"; // In real app, get from authenticated session
    }
    
    @FXML
    public void initialize() {
        // Set current date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        lblDate.setText(today.format(formatter));
        
        // Set default mood
        selectMood("neutral");
    }
    
    @FXML
    public void handleBackToDashboard() {
        ViewSwitcher.switchToView("/fxml/DashboardView.fxml", "MindScribe - Dashboard");
    }
    
    @FXML
    public void handleMoodHappy() {
        selectMood("happy");
    }
    
    @FXML
    public void handleMoodSad() {
        selectMood("sad");
    }
    
    @FXML
    public void handleMoodAnxious() {
        selectMood("anxious");
    }
    
    @FXML
    public void handleMoodNeutral() {
        selectMood("neutral");
    }
    
    @FXML
    public void handleAnalyze() {
        String text = txtJournalEntry.getText().trim();
        if (text.isEmpty()) {
            showStatus("Please write something before analyzing", "error");
            return;
        }
        
        // Disable analyze button during processing
        btnAnalyze.setDisable(true);
        showStatus("Analyzing your entry...", "info");
        
        // Create background task for AI analysis
        Task<String> analysisTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                // Use real AI service and get actual result
                AnalysisResult result = aiService.analyzeEmotion(text);
                return "Emotional Analysis Complete:\n" +
                       "Primary emotion detected: " + result.getEmotion() + "\n" +
                       "Confidence: " + String.format("%.2f%%", result.getConfidence() * 100) + "\n" +
                       "Key themes: " + getKeyThemes(text) + "\n" +
                       "Overall mood: " + selectedMood;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    String result = getValue();
                    lblAnalysisResults.setText(result);
                    analysisContainer.setVisible(true);
                    analysisContainer.setManaged(true);
                    btnAnalyze.setDisable(false);
                    showStatus("Analysis complete!", "success");
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    btnAnalyze.setDisable(false);
                    showStatus("Analysis failed. Please try again.", "error");
                });
            }
        };
        
        // Run the task in a background thread
        Thread analysisThread = new Thread(analysisTask);
        analysisThread.setDaemon(true);
        analysisThread.start();
    }
    
    @FXML
    public void handleSave() {
        String text = txtJournalEntry.getText().trim();
        if (text.isEmpty()) {
            showStatus("Please write something before saving", "error");
            return;
        }
        
        try {
            showStatus("Saving entry...", "info");
            
            String currentUser = SessionManager.getCurrentUser();
            String analysis = lblAnalysisResults.getText();
            
            // Save entry using backend diary service
            BackendDiaryService diaryService = BackendDiaryService.getInstance();
            diaryService.saveEntry(currentUser, text, selectedMood, analysis);
            
            System.out.println("Entry saved for user: " + currentUser);
            showStatus("Entry saved successfully!", "success");
            
            // Notify other views to refresh their data
            ViewSwitcher.clearCache(); // Clear cached views so they reload with fresh data
            
            // Check for crisis and send emergency notifications (DISABLED for now)
            // Crisis detection functionality temporarily disabled
            /*
            if (emergencyService.isCrisisDetected(analysis)) {
                emergencyService.sendEmergencyNotifications(currentUser, text, analysis);
                showStatus("Emergency contacts notified due to concerning content", "warning");
            }
            */
            
            // Clear form after successful save
            txtJournalEntry.clear();
            analysisContainer.setVisible(false);
            analysisContainer.setManaged(false);
            lblAnalysisResults.setText("");
            
        } catch (Exception e) {
            showStatus("Failed to save entry: " + e.getMessage(), "error");
        }
    }
    
    private void selectMood(String mood) {
        selectedMood = mood;
        
        // Reset all mood buttons
        btnHappy.getStyleClass().remove("mood-button-selected");
        btnSad.getStyleClass().remove("mood-button-selected");
        btnAnxious.getStyleClass().remove("mood-button-selected");
        btnNeutral.getStyleClass().remove("mood-button-selected");
        
        // Highlight selected mood button
        switch (mood) {
            case "happy":
                btnHappy.getStyleClass().add("mood-button-selected");
                break;
            case "sad":
                btnSad.getStyleClass().add("mood-button-selected");
                break;
            case "anxious":
                btnAnxious.getStyleClass().add("mood-button-selected");
                break;
            case "neutral":
                btnNeutral.getStyleClass().add("mood-button-selected");
                break;
        }
    }
    
    private void showStatus(String message, String type) {
        lblStatus.setText(message);
        lblStatus.getStyleClass().removeAll("status-info", "status-success", "status-error");
        lblStatus.getStyleClass().add("status-" + type);
    }
    
    private String getKeyThemes(String text) {
        // Mock theme extraction
        if (text.toLowerCase().contains("work") || text.toLowerCase().contains("job")) {
            return "Work, Career";
        } else if (text.toLowerCase().contains("family") || text.toLowerCase().contains("home")) {
            return "Family, Home";
        } else if (text.toLowerCase().contains("friend") || text.toLowerCase().contains("social")) {
            return "Social, Relationships";
        }
        return "Personal Growth";
    }
}
