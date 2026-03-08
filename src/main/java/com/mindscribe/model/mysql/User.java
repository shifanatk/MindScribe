package com.mindscribe.model.mysql;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB Atlas-backed user document for authentication and profiles.
 *
 * Note: we keep the existing package name to minimise ripple changes.
 */
@Document(collection = "users")
public class User {

    @Id
    private String id; // Maps to MongoDB ObjectId (e.g. "69ad5597d635dd507b5c4ce5")

    private String username;

    /**
     * BCrypt-hashed password (e.g. $2a$10$...).
     */
    private String password;

    /**
     * Spring Security role, e.g. "ROLE_USER".
     */
    private String role;

    public User() {
    }

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
