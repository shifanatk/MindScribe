package com.mindscribe.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for switching between different views in the JavaFX application.
 * Provides centralized view management and scene transitions.
 */
public class ViewSwitcher {
    
    private static Stage primaryStage;
    private static Scene currentScene;
    private static final Map<String, Parent> viewCache = new HashMap<>();
    
    /**
     * Initialize the ViewSwitcher with the primary stage
     */
    public static void initialize(Stage stage) {
        primaryStage = stage;
    }
    
    /**
     * Switch to a specific view by FXML file name
     * @param fxmlPath The path to the FXML file (e.g., "/fxml/AuthView.fxml")
     * @param title The window title for this view
     * @param controllerClass The expected controller class (for type safety)
     * @return The controller instance for the loaded view
     */
    @SuppressWarnings("unchecked")
    public static <T> T switchToView(String fxmlPath, String title, Class<T> controllerClass) {
        try {
            // Check cache first
            Parent root = viewCache.get(fxmlPath);
            FXMLLoader loader;
            
            if (root == null) {
                // Load new FXML
                loader = new FXMLLoader(ViewSwitcher.class.getResource(fxmlPath));
                root = loader.load();
                viewCache.put(fxmlPath, root);
            } else {
                // Use cached root - create new loader to get fresh controller
                loader = new FXMLLoader(ViewSwitcher.class.getResource(fxmlPath));
                // Don't set root when using cached FXML to avoid "Root value already specified" error
                Parent cachedRoot = root;
                root = cachedRoot;
            }
            
            // Create or update scene
            if (currentScene == null) {
                currentScene = new Scene(root);
                currentScene.getStylesheets().add(
                    ViewSwitcher.class.getResource("/css/main.css").toExternalForm()
                );
                primaryStage.setScene(currentScene);
            } else {
                currentScene.setRoot(root);
            }
            
            primaryStage.setTitle(title);
            primaryStage.setResizable(true);
            primaryStage.show();
            
            return (T) loader.getController();
            
        } catch (IOException e) {
            System.err.println("Failed to load view: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Switch to a view without expecting a specific controller type
     */
    public static void switchToView(String fxmlPath, String title) {
        switchToView(fxmlPath, title, Object.class);
    }
    
    /**
     * Clear the view cache (useful for logout or refresh scenarios)
     */
    public static void clearCache() {
        viewCache.clear();
    }
    
    /**
     * Get the current scene
     */
    public static Scene getCurrentScene() {
        return currentScene;
    }
    
    /**
     * Get the primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
