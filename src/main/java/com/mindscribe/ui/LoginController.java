package com.mindscribe.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * JavaFX login controller.
 *
 * This now delegates authentication to the Spring Boot backend
 * (/api/auth/login), which validates credentials against MongoDB Atlas.
 */
public class LoginController {

    private static final String AUTH_BASE = "http://localhost:8080/api/auth";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private Stage primaryStage;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void onLogin(ActionEvent event) {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            errorLabel.setText("Username and password are required");
            errorLabel.setVisible(true);
            return;
        }

        errorLabel.setVisible(false);

        // Call Spring Boot backend (/api/auth/login) asynchronously
        new Thread(() -> {
            try {
                String uri = AUTH_BASE
                        + "/login?username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
                        + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(uri))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                boolean ok = response.statusCode() == 200
                        && response.body() != null
                        && response.body().contains("\"status\":\"logged_in\"");

                if (ok) {
                    Platform.runLater(() -> {
                        errorLabel.setVisible(false);
                        openDashboardScene();
                    });
                } else {
                    Platform.runLater(() -> {
                        errorLabel.setText("Invalid username or password");
                        errorLabel.setVisible(true);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    errorLabel.setText("Cannot reach MindScribe server at " + AUTH_BASE);
                    errorLabel.setVisible(true);
                });
            }
        }).start();
    }

    private void openDashboardScene() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/dashboard.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 1100, 700);
            scene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm()
            );

            if (primaryStage == null) {
                // Fallback: derive stage from any control
                primaryStage = (Stage) usernameField.getScene().getWindow();
            }

            primaryStage.setTitle("MindScribe – AI Diary");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Failed to open diary screen");
            errorLabel.setVisible(true);
        }
    }
}

