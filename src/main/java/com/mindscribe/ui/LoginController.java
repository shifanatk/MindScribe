package com.mindscribe.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Simple single-user login controller.
 *
 * The valid username/password are hard-coded so you can easily
 * change them in code while focusing on one user.
 */
public class LoginController {

    // TODO: change these to whatever you like
    private static final String VALID_USERNAME = "user";
    private static final String VALID_PASSWORD = "secret123";

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

        if (username.equals(VALID_USERNAME) && password.equals(VALID_PASSWORD)) {
            errorLabel.setVisible(false);
            openDashboardScene();
        } else {
            errorLabel.setText("Invalid username or password");
            errorLabel.setVisible(true);
        }
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

