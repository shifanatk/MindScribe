package com.mindscribe.ui;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple H2 database service for GUI that works without Spring Boot
 * Directly connects to H2 database file for persistence
 */
public class H2DiaryService {
    
    private static final String DB_URL = "jdbc:h2:file:./data/mindscribe;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    private static H2DiaryService instance;
    
    private H2DiaryService() {
        initializeDatabase();
    }
    
    public static synchronized H2DiaryService getInstance() {
        if (instance == null) {
            instance = new H2DiaryService();
        }
        return instance;
    }
    
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Create journal entries table if it doesn't exist
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS journal_entry (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(255),
                    content TEXT,
                    sentiment_result VARCHAR(100),
                    created_at TIMESTAMP
                )
                """;
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
                System.out.println("H2 database initialized successfully");
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize H2 database: " + e.getMessage());
            // Don't print stack trace for normal operation, only for debugging
            if (e.getMessage() != null && !e.getMessage().contains("Database may be already in use")) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Save a new journal entry to H2 database
     */
    public JournalEntry saveEntry(String title, String content, String sentimentResult) {
        String sql = "INSERT INTO journal_entry (title, content, sentiment_result, created_at) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setString(3, sentimentResult);
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long id = generatedKeys.getLong(1);
                        System.out.println("Entry saved to H2 with ID: " + id);
                        return getEntryById(id);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to save entry: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all journal entries from H2 database
     */
    public List<JournalEntry> getAllEntries() {
        List<JournalEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM journal_entry ORDER BY created_at DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                JournalEntry entry = new JournalEntry(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("sentiment_result"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                entries.add(entry);
            }
            
            System.out.println("Loaded " + entries.size() + " entries from H2 database");
            
        } catch (SQLException e) {
            System.err.println("Failed to load entries: " + e.getMessage());
            e.printStackTrace();
        }
        
        return entries;
    }
    
    /**
     * Get a specific entry by ID
     */
    public JournalEntry getEntryById(long id) {
        String sql = "SELECT * FROM journal_entry WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new JournalEntry(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("sentiment_result"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to get entry by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Delete an entry by ID
     */
    public boolean deleteEntry(long id) {
        String sql = "DELETE FROM journal_entry WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("Entry deleted from H2 with ID: " + id);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Failed to delete entry: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Journal entry model that matches the database structure
     */
    public static class JournalEntry {
        private final Long id;
        private final String title;
        private final String content;
        private final String sentimentResult;
        private final LocalDateTime createdAt;
        
        public JournalEntry(Long id, String title, String content, String sentimentResult, LocalDateTime createdAt) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.sentimentResult = sentimentResult;
            this.createdAt = createdAt;
        }
        
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public String getSentimentResult() { return sentimentResult; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        
        public String getFormattedDate() {
            return createdAt.format(DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a"));
        }
    }
}
