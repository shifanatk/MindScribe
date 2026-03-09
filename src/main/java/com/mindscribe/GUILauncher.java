package com.mindscribe;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.mindscribe.util.ViewSwitcher;

/**
 * Pure JavaFX Frontend Launcher - No Spring Boot dependencies
 * Use this to run only the GUI frontend while backend runs separately
 */
public class GUILauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize the ViewSwitcher with the primary stage
        ViewSwitcher.initialize(primaryStage);
        
        // Start with the authentication view
        ViewSwitcher.switchToView("/fxml/AuthView.fxml", "MindScribe - Login");
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
