package com.mindscribe.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindscribe.service.AIService; // Ensure this matches your AI service package
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class Dashboard {

    @Autowired
    private AIService aiService;

    @Autowired
    private ConfigurableApplicationContext springContext;

    private static final String API_BASE = "http://localhost:8080/api/diary";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private record EntryDto(Long id, String title, String content, String createdAt) {}

    @FXML private Label moodLabel;
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private ListView<String> entriesList;

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
        String title = titleField.getText().isBlank() ? "Untitled entry" : titleField.getText().trim();
        String content = contentArea.getText();
        saveEntryToBackend(title, content);
    }

    @FXML
    private void onAnalyzeMood() {
        String text = contentArea.getText();
        if (text == null || text.isBlank()) {
            moodLabel.setText("Mood: (write something first)");
            return;
        }

        moodLabel.setText("Mood: Analyzing...");

        // Run AI in a background thread so the UI doesn't freeze
        new Thread(() -> {
            try {
                String emotion = aiService.predictEmotion(text);
                Platform.runLater(() -> moodLabel.setText("Mood: " + emotion));
            } catch (Exception e) {
                Platform.runLater(() -> moodLabel.setText("Mood: Analysis Error"));
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void onLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(springContext::getBean);

            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println("Logout failed: " + e.getMessage());
        }
    }

    private void loadSelectedEntry(String title) {
        if (title == null) return;

        List<EntryDto> entries = (List<EntryDto>) entriesList.getUserData();
        if (entries == null) return;

        entries.stream()
                .filter(e -> e.title().equals(title))
                .findFirst()
                .ifPresent(e -> {
                    titleField.setText(e.title());
                    contentArea.setText(e.content());
                    if (e.createdAt() != null) {
                        moodLabel.setText("Mood: – | " + e.createdAt());
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
                        loadEntriesFromBackend(); // Refresh list
                    }
                } catch (Exception e) {
                    System.err.println("Failed to save: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private record NewEntryPayload(String title, String content) {}
}