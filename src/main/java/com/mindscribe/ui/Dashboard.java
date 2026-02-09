package com.mindscribe.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * JavaFX controller for the main MindScribe dashboard.
 *
 * For now this is UI-only and uses mock data; later we can
 * plug it into the Spring Boot backend and AI services.
 */
public class Dashboard {

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
        // Seed with some mock entries so the UI feels alive
        entriesList.getItems().addAll(
                "Morning reflection",
                "Afternoon check-in",
                "Gratitude list"
        );

        entriesList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> loadSelectedEntry(newVal)
        );
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

        if (!entriesList.getItems().contains(title)) {
            entriesList.getItems().add(0, title);
        }

        // TODO: Persist to backend when storage is ready
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
        if (title == null) {
            return;
        }
        // For now just reflect the title; later we can fetch full content
        titleField.setText(title);
        contentArea.setText("This is where the full diary content for \"" + title + "\" will appear.\n\n"
                + "Once storage is wired up, this will load from your encrypted database.");
    }
}
