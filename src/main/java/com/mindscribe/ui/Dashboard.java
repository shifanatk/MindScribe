package com.mindscribe.ui;

import com.mindscribe.ui.BackendDiaryService;
import com.mindscribe.ui.BackendDiaryService.DiaryEntryUI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * JavaFX controller for the main MindScribe dashboard.
 *
 * Uses BackendDiaryService for permanent H2 storage via backend API.
 */
public class Dashboard {

    private final BackendDiaryService diaryService = BackendDiaryService.getInstance();
    private List<DiaryEntryUI> cachedEntries;

    @FXML
    private Label moodLabel;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea contentArea;

    @FXML
    private ListView<String> entriesList;

    @FXML
    private Button newEntryButton;

    @FXML
    private Button saveEntryButton;

    @FXML
    private Button analyzeMoodButton;

    @FXML
    private void initialize() {
        entriesList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> loadSelectedEntry(newVal)
        );

        loadEntriesFromBackend();
    }

    @FXML
    private void onNewEntry() {
        titleField.clear();
        contentArea.clear();
        moodLabel.setText("Mood: –");
        titleField.requestFocus();
    }

    @FXML
    private void onSaveEntry() {
        String title = titleField.getText().isBlank()
                ? "Untitled entry"
                : titleField.getText().trim();

        String content = contentArea.getText();
        String mood = extractMoodFromContent(content);
        saveEntryToBackend(title, content, mood);
    }

    @FXML
    private void onAnalyzeMood() {
        String text = contentArea.getText();

        if (text == null || text.isBlank()) {
            moodLabel.setText("Mood: (write something to analyze)");
            return;
        }

        // Temporary heuristic; will be replaced with TinyBERT
        String lower = text.toLowerCase();
        String mood;
        if (lower.contains("grateful") || lower.contains("happy") || lower.contains("excited")) {
            mood = "Positive";
        } else if (lower.contains("tired") || lower.contains("sad") || lower.contains("anxious")) {
            mood = "Reflective";
        } else {
            mood = "Neutral";
        }

        moodLabel.setText("Mood: " + mood);
    }

    private void loadSelectedEntry(String title) {
        if (title == null || cachedEntries == null) {
            return;
        }
        
        cachedEntries.stream()
                .filter(e -> e.getContent().contains(title.substring(0, Math.min(20, title.length()))) || title.equals("Untitled entry"))
                .findFirst()
                .ifPresent(e -> {
                    titleField.setText(title);
                    contentArea.setText(e.getContent());
                    String moodText = (e.getMood() != null && !e.getMood().isBlank())
                            ? "Mood: " + e.getMood()
                            : "Mood: –";
                    moodLabel.setText(moodText + "  |  " + e.getFormattedDate());
                });
    }

    private void loadEntriesFromBackend() {
        new Thread(() -> {
            try {
                List<DiaryEntryUI> entries = diaryService.getAllEntries("testuser");
                
                Platform.runLater(() -> {
                    entriesList.getItems().clear();
                    cachedEntries = entries;
                    for (DiaryEntryUI e : entries) {
                        String title = "Entry from " + e.getFormattedDate();
                        entriesList.getItems().add(title);
                    }
                    System.out.println("Loaded " + entries.size() + " entries from permanent H2 storage");
                });
            } catch (Exception e) {
                System.err.println("Failed to load entries from backend: " + e.getMessage());
                Platform.runLater(() -> {
                    entriesList.getItems().clear();
                    entriesList.getItems().add("(No backend connection)");
                });
            }
        }).start();
    }

    private void saveEntryToBackend(String title, String content, String mood) {
        new Thread(() -> {
            try {
                diaryService.saveEntry("testuser", content, mood, "AI Analysis: " + mood);
                
                Platform.runLater(() -> {
                    // Refresh the entries list
                    loadEntriesFromBackend();
                    System.out.println("Entry saved to permanent H2 storage: " + title);
                });
            } catch (Exception e) {
                System.err.println("Failed to save entry to backend: " + e.getMessage());
            }
        }).start();
    }
    
    private String extractMoodFromContent(String content) {
        if (content == null || content.isBlank()) {
            return "Neutral";
        }
        
        String lower = content.toLowerCase();
        if (lower.contains("grateful") || lower.contains("happy") || lower.contains("excited")) {
            return "Positive";
        } else if (lower.contains("tired") || lower.contains("sad") || lower.contains("anxious")) {
            return "Reflective";
        } else {
            return "Neutral";
        }
    }
}
