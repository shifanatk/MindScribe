package com.mindscribe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX entry point for the MindScribe desktop GUI.
 *
 * This runs the local UI. The Spring Boot backend can be started separately
 * (via {@link MindscribeBackendApplication}) if/when you want API + DB.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/dashboard.fxml")
        );
        Parent root = loader.load();

        Scene scene = new Scene(root, 1100, 700);
        scene.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm()
        );

        primaryStage.setTitle("MindScribe – AI Diary");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
