package com.mindscribe.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * JavaFX controller for the main MindScribe dashboard.
 *
 * For now this is UI-only and uses mock data; later we can
 * plug it into the Spring Boot backend and AI services.
 */
public class Dashboard {

    private static final String API_BASE = "http://localhost:8080/api/diary";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private record EntryDto(Long id, String title, String content, String createdAt) {}

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
        saveEntryToBackend(title, content);
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
        // We already have full entries cached in the list view user data
        @SuppressWarnings("unchecked")
        List<EntryDto> entries = (List<EntryDto>) entriesList.getUserData();
        if (entries == null) {
            return;
        }
        entries.stream()
                .filter(e -> e.title().equals(title))
                .findFirst()
                .ifPresent(e -> {
                    titleField.setText(e.title());
                    contentArea.setText(e.content());

                    if (e.createdAt() != null) {
                        moodLabel.setText("Mood: –  |  " + e.createdAt());
                    }
                });
    }

    private void loadEntriesFromBackend() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + "/entries"))
                .GET()
                .build();

        new Thread(() -> {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    List<EntryDto> entries = objectMapper.readValue(
                            response.body(),
                            new TypeReference<List<EntryDto>>() {}
                    );

                    Platform.runLater(() -> {
                        entriesList.getItems().clear();
                        for (EntryDto e : entries) {
                            entriesList.getItems().add(e.title());
                        }
                        entriesList.setUserData(entries);
                    });
                }
            } catch (Exception e) {
                // In this first version we silently ignore errors (e.g., backend not running)
                System.err.println("Failed to load entries: " + e.getMessage());
            }
        }).start();
    }

    private void saveEntryToBackend(String title, String content) {
        try {
            String json = objectMapper.writeValueAsString(new NewEntryPayload(title, content));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/entry"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            new Thread(() -> {
                try {
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        EntryDto saved = objectMapper.readValue(response.body(), EntryDto.class);
                        Platform.runLater(() -> {
                            if (!entriesList.getItems().contains(saved.title())) {
                                entriesList.getItems().add(0, saved.title());
                            }
                            // Refresh cache
                            loadEntriesFromBackend();
                        });
                    }
                } catch (Exception e) {
                    System.err.println("Failed to save entry: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            System.err.println("Failed to prepare save request: " + e.getMessage());
        }
    }

    private record NewEntryPayload(String title, String content) {}
}
