package com.mindscribe.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.mindscribe.util.ViewSwitcher;
import com.mindscribe.ui.SessionManager;
import com.mindscribe.ui.BackendDiaryService;
import com.mindscribe.ui.BackendDiaryService.DiaryEntryUI;

/**
 * JavaFX Controller for the Dashboard view
 */
public class DashboardViewController {
    
    @FXML
    private Label lblGreeting;
    
    @FXML
    private Label lblRecentEntry;
    
    @FXML
    private Button btnWriteDiary;
    
    @FXML
    private Button btnViewAnalytics;
    
    @FXML
    private Button btnPastEntries;
    
    @FXML
    private Button btnEmergencyContacts;
    
    @FXML
    private Button btnLogout;
    
    private BackendDiaryService diaryService;
    
    public DashboardViewController() {
        this.diaryService = BackendDiaryService.getInstance();
    }
    
    @FXML
    public void initialize() {
        // Set personalized greeting from session
        String currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            lblGreeting.setText("Hello, " + currentUser + "!");
        } else {
            lblGreeting.setText("Hello, Guest!");
        }
        
        // Load recent entry preview
        loadRecentEntry();
    }
    
    @FXML
    public void handleWriteDiary() {
        ViewSwitcher.switchToView("/fxml/EditorView.fxml", "MindScribe - Write Entry");
    }
    
    @FXML
    public void handleViewAnalytics() {
        ViewSwitcher.switchToView("/fxml/AnalyticsView.fxml", "MindScribe - Mood Analytics");
    }
    
    @FXML
    public void handlePastEntries() {
        // Navigate to the new Past Entries view
        ViewSwitcher.switchToView("/fxml/PastEntriesView.fxml", "MindScribe - Past Entries");
    }
    
    @FXML
    public void handleEmergencyContacts() {
        // Navigate to the Emergency Contacts view
        ViewSwitcher.switchToView("/fxml/EmergencyContactView.fxml", "MindScribe - Emergency Contacts");
    }
    
    @FXML
    public void handleLogout() {
        // Clear session
        SessionManager.clearSession();
        
        // Clear cache and navigate back to auth
        ViewSwitcher.clearCache();
        ViewSwitcher.switchToView("/fxml/AuthView.fxml", "MindScribe - Login");
    }
    
    private void loadRecentEntry() {
        try {
            String currentUser = SessionManager.getCurrentUser();
            DiaryEntryUI recentEntry = diaryService.getMostRecentEntry(currentUser);
            
            if (recentEntry != null) {
                // Show preview of most recent entry
                String preview = recentEntry.getContent();
                if (preview.length() > 200) {
                    preview = preview.substring(0, 197) + "...";
                }
                lblRecentEntry.setText(preview);
            } else {
                lblRecentEntry.setText("No recent entries found. Start writing today!");
            }
        } catch (Exception e) {
            System.err.println("Error loading recent entry: " + e.getMessage());
            lblRecentEntry.setText("Error loading recent entries");
        }
    }
}
