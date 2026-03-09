package com.mindscribe.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.mindscribe.util.ViewSwitcher;
import com.mindscribe.ui.SessionManager;
import com.mindscribe.ui.BackendDiaryService;
import com.mindscribe.ui.BackendDiaryService.DiaryEntryUI;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

/**
 * JavaFX Controller for Past Entries view
 */
public class PastEntriesViewController {
    
    @FXML
    private TableView<DiaryEntryUI> entriesTable;
    
    @FXML
    private TableColumn<DiaryEntryUI, String> dateColumn;
    
    @FXML
    private TableColumn<DiaryEntryUI, String> moodColumn;
    
    @FXML
    private TableColumn<DiaryEntryUI, String> previewColumn;
    
    @FXML
    private TableColumn<DiaryEntryUI, String> analysisColumn;
    
    @FXML
    private Button btnBack;
    
    @FXML
    private Button btnDeleteSelected;
    
    @FXML
    private Button btnExportAll;
    
    private BackendDiaryService diaryService;
    private ObservableList<DiaryEntryUI> entriesData;
    
    public PastEntriesViewController() {
        this.diaryService = BackendDiaryService.getInstance();
        this.entriesData = FXCollections.observableArrayList();
    }
    
    @FXML
    public void initialize() {
        // Setup table columns
        setupTableColumns();
        
        // Load entries data
        loadEntriesData();
        
        // Set table data
        entriesTable.setItems(entriesData);
    }
    
    @FXML
    public void handleBackToDashboard() {
        ViewSwitcher.switchToView("/fxml/DashboardView.fxml", "MindScribe - Dashboard");
    }
    
    @FXML
    public void handleDeleteSelected() {
        DiaryEntryUI selectedEntry = entriesTable.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Delete");
            confirmDialog.setHeaderText("Delete Entry");
            confirmDialog.setContentText("Are you sure you want to delete this entry from " + 
                selectedEntry.getTimestamp().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")) + "?");
            
            if (confirmDialog.showAndWait().orElse(javafx.scene.control.ButtonType.OK) == javafx.scene.control.ButtonType.OK) {
                // Remove from data
                String currentUser = SessionManager.getCurrentUser();
                diaryService.deleteEntry(currentUser, selectedEntry);
                
                // Reload data
                loadEntriesData();
                
                showAlert("Entry deleted successfully", "success");
            }
        }
    }
    
    @FXML
    public void handleExportAll() {
        try {
            String currentUser = SessionManager.getCurrentUser();
            java.util.List<DiaryEntryUI> entries = diaryService.getAllEntries(currentUser);
            
            if (entries.isEmpty()) {
                showAlert("No entries to export", "info");
                return;
            }
            
            // Create export content
            StringBuilder exportContent = new StringBuilder();
            exportContent.append("MindScribe Journal Entries Export\n");
            exportContent.append("Generated: ");
            exportContent.append(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            exportContent.append("\n\n");
            
            for (DiaryEntryUI entry : entries) {
                exportContent.append("Date: ");
                exportContent.append(entry.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                exportContent.append("\n");
                exportContent.append("Mood: ");
                exportContent.append(entry.getMood());
                exportContent.append("\n");
                exportContent.append("Analysis: ");
                if (entry.getAnalysis() != null) {
                    exportContent.append(entry.getAnalysis());
                } else {
                    exportContent.append("No analysis available");
                }
                exportContent.append("\n");
                exportContent.append("Content: ");
                exportContent.append(entry.getContent());
                exportContent.append("\n\n");
            }
            
            // Save to file
            java.io.PrintWriter writer = new java.io.PrintWriter("mindscribe_export_" + currentUser + "_" + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");
            writer.write(exportContent.toString());
            writer.close();
            
            showAlert("Entries exported successfully", "success");
            
        } catch (Exception e) {
            showAlert("Export failed: " + e.getMessage(), "error");
        }
    }
    
    private void setupTableColumns() {
        // Simple setup using property value factories
        dateColumn.setCellValueFactory(param -> {
            if (param.getValue() != null) {
                return new SimpleStringProperty(param.getValue().getTimestamp().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            }
            return new SimpleStringProperty("");
        });
        
        moodColumn.setCellValueFactory(param -> {
            if (param.getValue() != null) {
                String mood = param.getValue().getMood();
                String emoji = getMoodEmoji(mood);
                return new SimpleStringProperty(emoji + " " + mood);
            }
            return new SimpleStringProperty("");
        });
        
        previewColumn.setCellValueFactory(param -> {
            if (param.getValue() != null) {
                String content = param.getValue().getContent();
                if (content.length() > 100) {
                    content = content.substring(0, 97) + "...";
                }
                return new SimpleStringProperty(content);
            }
            return new SimpleStringProperty("");
        });
        
        analysisColumn.setCellValueFactory(param -> {
            if (param.getValue() != null) {
                String analysis = param.getValue().getAnalysis();
                if (analysis != null) {
                    if (analysis.length() > 100) {
                        analysis = analysis.substring(0, 97) + "...";
                    }
                } else {
                    analysis = "No analysis";
                }
                return new SimpleStringProperty(analysis);
            }
            return new SimpleStringProperty("");
        });
    }
    
    private void loadEntriesData() {
        try {
            String currentUser = SessionManager.getCurrentUser();
            java.util.List<DiaryEntryUI> entries = diaryService.getAllEntries(currentUser);
            
            entriesData.clear();
            entriesData.addAll(entries);
            
            // Sort by date (most recent first)
            entriesData.sort(Comparator.comparing(DiaryEntryUI::getTimestamp).reversed());
            
        } catch (Exception e) {
            showAlert("Error loading entries: " + e.getMessage(), "error");
        }
    }
    
    private String getMoodEmoji(String mood) {
        switch (mood.toLowerCase()) {
            case "happy": return "😊";
            case "sad": return "😢";
            case "anxious": return "😰";
            case "neutral": return "😐";
            default: return "📝";
        }
    }
    
    private void showAlert(String message, String type) {
        Alert alert = new Alert(type.equals("success") ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setTitle("MindScribe");
        alert.setHeaderText(type.equals("success") ? "Success" : "Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
