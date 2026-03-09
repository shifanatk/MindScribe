package com.mindscribe.ui;

import com.mindscribe.model.mysql.User;
import com.mindscribe.repository.mysql.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Simple UserService for JavaFX UI authentication
 */
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // For now, we'll create a simple mock implementation
    // In a real app, this would be injected via Spring
    public UserService() {
        // Mock implementation for demo purposes
        this.userRepository = null;
        this.passwordEncoder = null;
    }
    
    /**
     * Authenticate user with username and password
     */
    public User authenticate(String username, String password) {
        // Mock authentication - in real app, use proper password verification
        if ("admin".equals(username) && "password".equals(password)) {
            return new User(username, "hashed", "ROLE_USER");
        }
        if ("shifa".equals(username) && "mindscribe".equals(password)) {
            return new User(username, "hashed", "ROLE_USER");
        }
        if ("testuser".equals(username) && "testpass".equals(password)) {
            return new User(username, "hashed", "ROLE_USER");
        }
        return null;
    }
    
    /**
     * Find user by username
     */
    public User findByUsername(String username) {
        // Mock implementation
        if ("admin".equals(username) || "shifa".equals(username) || "testuser".equals(username)) {
            return new User(username, "hashed", "ROLE_USER");
        }
        return null;
    }
    
    /**
     * Register a new user
     */
    public User registerUser(String username, String password) {
        // Mock implementation - in real app, hash password and save to database
        return new User(username, "hashed_password", "ROLE_USER");
    }
}
