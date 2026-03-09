package com.mindscribe.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple diary service for storing and retrieving journal entries
 * In a real app, this would connect to a database
 */
public class DiaryService {
    
    // In-memory storage for demo purposes
    private static final Map<String, List<DiaryEntry>> userEntries = new HashMap<>();
    
    public DiaryService() {
        // Initialize with some sample data
        initializeSampleData();
    }
    
    /**
     * Save a new diary entry
     */
    public void saveEntry(String username, String content, String mood, String analysis) {
        DiaryEntry entry = new DiaryEntry(
            LocalDateTime.now(),
            content,
            mood,
            analysis
        );
        
        userEntries.computeIfAbsent(username, k -> new ArrayList<>()).add(entry);
        System.out.println("Entry saved for user: " + username);
    }
    
    /**
     * Delete a diary entry
     */
    public void deleteEntry(String username, DiaryEntry entry) {
        List<DiaryEntry> entries = userEntries.get(username);
        if (entries != null) {
            entries.remove(entry);
            System.out.println("Entry deleted for user: " + username);
        }
    }
    
    /**
     * Get recent entries for a user
     */
    public List<DiaryEntry> getRecentEntries(String username, int limit) {
        List<DiaryEntry> entries = userEntries.get(username);
        if (entries == null) {
            return new ArrayList<>();
        }
        
        // Return most recent entries (reverse order)
        List<DiaryEntry> recent = new ArrayList<>(entries);
        recent.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        
        return recent.stream().limit(limit).toList();
    }
    
    /**
     * Get the most recent entry for a user
     */
    public DiaryEntry getMostRecentEntry(String username) {
        List<DiaryEntry> entries = userEntries.get(username);
        if (entries == null || entries.isEmpty()) {
            return null;
        }
        
        return entries.stream()
                .max((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                .orElse(null);
    }
    
    /**
     * Get all entries for analytics
     */
    public List<DiaryEntry> getAllEntries(String username) {
        return userEntries.getOrDefault(username, new ArrayList<>());
    }
    
    private void initializeSampleData() {
        // Add sample entries for demo
        List<DiaryEntry> testUserEntries = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        testUserEntries.add(new DiaryEntry(
            now.minusDays(1),
            "Today was a productive day. I managed to complete my project milestone and had a great conversation with my team about future goals. Feeling accomplished and excited for what's next!",
            "happy",
            "Primary emotion: Joy (85%). Sentiment: Positive. Key themes: Achievement, Team, Future."
        ));
        
        testUserEntries.add(new DiaryEntry(
            now.minusDays(3),
            "Feeling a bit anxious about the upcoming presentation. I've been preparing but still feel like I need more practice. The imposter syndrome is creeping in.",
            "anxious",
            "Primary emotion: Anxiety (72%). Sentiment: Negative. Key themes: Work, Stress, Self-doubt."
        ));
        
        testUserEntries.add(new DiaryEntry(
            now.minusDays(7),
            "Had a wonderful weekend with family. We went hiking and enjoyed nature. Sometimes I need to disconnect from work and just appreciate the simple moments.",
            "happy",
            "Primary emotion: Joy (92%). Sentiment: Very Positive. Key themes: Family, Nature, Relaxation."
        ));
        
        userEntries.put("testuser", testUserEntries);
        userEntries.put("shifa", testUserEntries);
        userEntries.put("admin", testUserEntries);
    }
    
    /**
     * Diary entry model
     */
    public static class DiaryEntry {
        private final LocalDateTime timestamp;
        private final String content;
        private final String mood;
        private final String analysis;
        
        public DiaryEntry(LocalDateTime timestamp, String content, String mood, String analysis) {
            this.timestamp = timestamp;
            this.content = content;
            this.mood = mood;
            this.analysis = analysis;
        }
        
        public LocalDateTime getTimestamp() {
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
