package com.mindscribe.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class LoginController {

    @Autowired
    private ConfigurableApplicationContext springContext; // ADD THIS

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML
    public void handleLogin(ActionEvent event) {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if ("admin".equals(user) && "password".equals(pass)) {
            statusLabel.setText("Login Successful! Redirecting...");
            statusLabel.setStyle("-fx-text-fill: green;");
            switchToDashboard(event);
        } else {
            statusLabel.setText("Invalid username or password!");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void switchToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));

            // CRITICAL: Tell JavaFX to use Spring for the Dashboard controller
            loader.setControllerFactory(springContext::getBean);

            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Re-using the same dimensions you had in Main.java
            stage.setScene(new Scene(root, 1100, 700));
            stage.show();

        } catch (IOException e) {
            statusLabel.setText("Error loading dashboard!");
            e.printStackTrace();
        }
    }
}