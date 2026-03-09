package com.mindscribe.ui;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * UI-friendly diary service that works in standalone mode
 * Stores entries in memory for now (can be extended to use file storage)
 */
public class UIDiaryService {
    
    private static UIDiaryService instance;
    private final ConcurrentHashMap<String, List<DiaryEntryUI>> userEntries;
    private final AtomicLong idGenerator;
    
    private UIDiaryService() {
        this.userEntries = new ConcurrentHashMap<>();
        this.idGenerator = new AtomicLong(1);
        System.out.println("UIDiaryService initialized in standalone mode");
    }
    
    public static synchronized UIDiaryService getInstance() {
        if (instance == null) {
            instance = new UIDiaryService();
        }
        return instance;
    }
    
    /**
     * Save a new diary entry
     */
    public void saveEntry(String username, String content, String mood, String analysis) {
        if (username == null || username.trim().isEmpty()) {
            username = "default_user";
        }
        
        DiaryEntryUI entry = new DiaryEntryUI(
            idGenerator.getAndIncrement(),
            java.time.LocalDateTime.now(),
            content,
            mood,
            analysis
        );
        
        userEntries.computeIfAbsent(username, k -> new ArrayList<>()).add(entry);
        System.out.println("Entry saved locally for user: " + username + ", total entries: " + userEntries.get(username).size());
    }
    
    /**
     * Delete a diary entry
     */
    public void deleteEntry(String username, DiaryEntryUI entry) {
        if (username == null || username.trim().isEmpty()) {
            username = "default_user";
        }
        
        List<DiaryEntryUI> entries = userEntries.get(username);
        if (entries != null) {
            entries.removeIf(e -> e.getDbId().equals(entry.getDbId()));
            System.out.println("Entry deleted for user: " + username);
        }
    }
    
    /**
     * Get recent entries for a user
     */
    public List<DiaryEntryUI> getRecentEntries(String username, int limit) {
        if (username == null || username.trim().isEmpty()) {
            username = "default_user";
        }
        
        List<DiaryEntryUI> entries = userEntries.getOrDefault(username, new ArrayList<>());
        entries.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        
        if (entries.size() > limit) {
            return entries.subList(0, limit);
        }
        return new ArrayList<>(entries);
    }
    
    /**
     * Get most recent entry for a user
     */
    public DiaryEntryUI getMostRecentEntry(String username) {
        if (username == null || username.trim().isEmpty()) {
            username = "default_user";
        }
        
        List<DiaryEntryUI> entries = userEntries.getOrDefault(username, new ArrayList<>());
        if (entries.isEmpty()) {
            return null;
        }
        
        return entries.stream()
            .max((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
            .orElse(null);
    }
    
    /**
     * Get all entries for analytics
     */
    public List<DiaryEntryUI> getAllEntries(String username) {
        if (username == null || username.trim().isEmpty()) {
            username = "default_user";
        }
        
        List<DiaryEntryUI> entries = userEntries.getOrDefault(username, new ArrayList<>());
        entries.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return new ArrayList<>(entries);
    }
    
    /**
     * UI-compatible diary entry class
     */
    public static class DiaryEntryUI {
        private final Long dbId;
        private final java.time.LocalDateTime timestamp;
        private final String content;
        private final String mood;
        private final String analysis;
        
        public DiaryEntryUI(Long dbId, java.time.LocalDateTime timestamp, String content, String mood, String analysis) {
            this.dbId = dbId;
            this.timestamp = timestamp;
            this.content = content;
            this.mood = mood;
            this.analysis = analysis;
        }
        
        public Long getDbId() {
            return dbId;
        }
        
        public java.time.LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public String getContent() {
            return content;
        }
        
        public String getMood() {
            return mood;
        }
        
        public String getAnalysis() {
            return analysis;
        }
        
        public String getFormattedDate() {
            return timestamp.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        }
    }
}
