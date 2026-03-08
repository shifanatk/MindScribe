package com.mindscribe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.mindscribe.ui.LoginController;

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
                getClass().getResource("/fxml/login.fxml")
        );
        Parent root = loader.load();

        Scene scene = new Scene(root, 420, 260);
        scene.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm()
        );

        primaryStage.setTitle("MindScribe – Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        // Pass the stage to the login controller so it can open the dashboard on success
        LoginController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
