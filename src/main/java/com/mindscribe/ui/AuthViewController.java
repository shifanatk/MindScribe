package com.mindscribe.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.mindscribe.util.ViewSwitcher;
import com.mindscribe.ui.DatabaseUserService;
import com.mindscribe.ui.SessionManager;
import com.mindscribe.model.mysql.User;

/**
 * JavaFX Controller for the authentication view (Login/Register)
 */
public class AuthViewController {
    
    @FXML
    private TextField loginUsername;
    
    @FXML
    private PasswordField loginPassword;
    
    @FXML
    private Button btnLogin;
    
    @FXML
    private TextField registerUsername;
    
    @FXML
    private PasswordField registerPassword;
    
    @FXML
    private PasswordField registerConfirmPassword;
    
    @FXML
    private Button btnRegister;
    
    @FXML
    private Label lblMessage;
    
    private DatabaseUserService userService;
    
    public AuthViewController() {
        this.userService = new DatabaseUserService();
    }
    
    @FXML
    public void initialize() {
        // Clear any existing messages
        lblMessage.setText("");
    }
    
    @FXML
    public void handleLogin() {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText();
        
        System.out.println("Login attempt - Username: " + username + ", Password: " + password);
        
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter both username and password", "error");
            return;
        }
        
        try {
            User user = userService.authenticate(username, password);
            if (user != null) {
                System.out.println("Login successful for user: " + user.getUsername());
                
                // Set session
                SessionManager.setCurrentUser(user.getUsername(), user.getId());
                
                showMessage("Login successful!", "success");
                // Navigate to Dashboard
                ViewSwitcher.switchToView("/fxml/DashboardView.fxml", "MindScribe - Dashboard");
            } else {
                System.out.println("Login failed for user: " + username);
                showMessage("Invalid username or password", "error");
            }
        } catch (Exception e) {
            System.out.println("Login exception: " + e.getMessage());
            showMessage("Login failed: " + e.getMessage(), "error");
        }
    }
    
    @FXML
    public void handleRegister() {
        String username = registerUsername.getText().trim();
        String password = registerPassword.getText();
        String confirmPassword = registerConfirmPassword.getText();
        
        System.out.println("Registration attempt - Username: " + username + ", Password: " + password);
        
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Please fill in all registration fields", "error");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match", "error");
            return;
        }
        
        if (password.length() < 6) {
            showMessage("Password must be at least 6 characters long", "error");
            return;
        }
        
        try {
            User existingUser = userService.findByUsername(username);
            if (existingUser != null) {
                System.out.println("Registration failed - Username already exists: " + username);
                showMessage("Username already exists", "error");
                return;
            }
            
            User newUser = userService.registerUser(username, password);
            if (newUser != null) {
                System.out.println("Registration successful for user: " + newUser.getUsername());
                showMessage("Registration successful! Please login.", "success");
                // Clear registration fields
                registerUsername.clear();
                registerPassword.clear();
                registerConfirmPassword.clear();
                // Focus on login fields
                loginUsername.requestFocus();
            } else {
                System.out.println("Registration failed for user: " + username);
                showMessage("Registration failed", "error");
            }
        } catch (Exception e) {
            System.out.println("Registration exception: " + e.getMessage());
            showMessage("Registration failed: " + e.getMessage(), "error");
        }
    }
    
    private void showMessage(String message, String type) {
        lblMessage.setText(message);
        lblMessage.getStyleClass().removeAll("success-message", "error-message");
        lblMessage.getStyleClass().add(type + "-message");
    }
}
