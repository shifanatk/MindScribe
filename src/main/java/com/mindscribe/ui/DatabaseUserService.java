package com.mindscribe.ui;

import com.mindscribe.model.mysql.User;
import com.mindscribe.repository.mysql.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Database-backed UserService for JavaFX UI authentication
 * This service actually saves data to the database
 */
public class DatabaseUserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // In-memory cache for demo purposes when DB is not available
    private static final Map<String, User> userCache = new HashMap<>();
    
    static {
        // Pre-populate with test users
        userCache.put("admin", new User("admin", "$2a$10$N9qo8uLOickgx2ZMRZoMy", "ROLE_USER"));
        userCache.put("shifa", new User("shifa", "$2a$10$N9qo8uLOickgx2ZMRZoMy", "ROLE_USER"));
        userCache.put("testuser", new User("testuser", "$2a$10$N9qo8uLOickgx2ZMRZoMy", "ROLE_USER"));
    }
    
    public DatabaseUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // Fallback constructor for demo
    public DatabaseUserService() {
        this.userRepository = null;
        this.passwordEncoder = null;
    }
    
    /**
     * Authenticate user with username and password
     */
    public User authenticate(String username, String password) {
        try {
            // Try database first
            if (userRepository != null) {
                Optional<User> userOpt = userRepository.findByUsername(username);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        return user;
                    }
                }
            } else {
                // Fallback to cache
                User cachedUser = userCache.get(username);
                if (cachedUser != null) {
                    // For demo, accept plain text passwords for pre-populated users
                    if (("admin".equals(username) && "password".equals(password)) ||
                        ("shifa".equals(username) && "mindscribe".equals(password)) ||
                        ("testuser".equals(username) && "testpass".equals(password))) {
                        return cachedUser;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Find user by username
     */
    public User findByUsername(String username) {
        try {
            // Try database first
            if (userRepository != null) {
                Optional<User> userOpt = userRepository.findByUsername(username);
                return userOpt.orElse(null);
            } else {
                // Fallback to cache
                return userCache.get(username);
            }
        } catch (Exception e) {
            System.err.println("Find user error: " + e.getMessage());
            return userCache.get(username);
        }
    }
    
    /**
     * Register a new user
     */
    public User registerUser(String username, String password) {
        try {
            // Check if user already exists
            if (findByUsername(username) != null) {
                return null; // User already exists
            }
            
            String hashedPassword;
            
            // Hash the password
            if (passwordEncoder != null) {
                hashedPassword = passwordEncoder.encode(password);
            } else {
                // Fallback for demo - simple hash
                hashedPassword = "hashed_" + password.hashCode();
            }
            
            User newUser = new User(username, hashedPassword, "ROLE_USER");
            
            // Save to database
            if (userRepository != null) {
                User savedUser = userRepository.save(newUser);
                System.out.println("User saved to database: " + savedUser.getUsername());
                return savedUser;
            } else {
                // Fallback to cache
                userCache.put(username, newUser);
                System.out.println("User saved to cache: " + newUser.getUsername());
                return newUser;
            }
            
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get all users (for debugging)
     */
    public Map<String, User> getAllUsers() {
        return new HashMap<>(userCache);
    }
}
