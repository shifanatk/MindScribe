package com.mindscribe.ui;

/**
 * Simple session management for JavaFX UI
 * Stores the currently logged-in user information
 */
public class SessionManager {
    
    private static String currentUser;
    private static String userId;
    
    public static void setCurrentUser(String username, String id) {
        currentUser = username;
        userId = id;
        System.out.println("Session set - User: " + username + ", ID: " + id);
    }
    
    public static String getCurrentUser() {
        return currentUser;
    }
    
    public static String getCurrentUserId() {
        return userId;
    }
    
    public static void clearSession() {
        currentUser = null;
        userId = null;
        System.out.println("Session cleared");
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
