package com.mindscribe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class Main extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        // This starts the Spring Boot backend and loads your AI Service
        springContext = new SpringApplicationBuilder(MindscribeBackendApplication.class).run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Component A: Load LOGIN first
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));

        // This allows @Autowired to work in your Controllers
        loader.setControllerFactory(springContext::getBean);

        Parent root = loader.load();
        Scene scene = new Scene(root, 1100, 700);

        // Keep your custom styling
        if (getClass().getResource("/css/styles.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        }

        primaryStage.setTitle("MindScribe – Login");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Component E: Graceful Shutdown of AI resources
        if (springContext != null) {
            springContext.close();
        }
        Platform.exit();
    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
