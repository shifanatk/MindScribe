package com.mindscribe.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unified diary service that communicates with backend for permanent H2 storage
 * Replaces UIDiaryService for all GUI components
 */
public class BackendDiaryService {
    
    private static final String API_BASE = "http://localhost:8080/api/diary";
    private static BackendDiaryService instance;
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private BackendDiaryService() {
        System.out.println("BackendDiaryService initialized - using permanent H2 storage via backend API");
    }
    
    public static synchronized BackendDiaryService getInstance() {
        if (instance == null) {
            instance = new BackendDiaryService();
        }
        return instance;
    }
    
    /**
     * Save a new diary entry to permanent H2 storage via backend
     */
    public void saveEntry(String username, String content, String mood, String analysis) {
        try {
            String title = "Entry from " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
            String json = objectMapper.writeValueAsString(new NewEntryPayload(title, content));
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/entry?username=" + username))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Entry saved to permanent H2 storage for user: " + username + " - Status: " + response.statusCode());
            
        } catch (Exception e) {
            System.err.println("Failed to save entry to backend: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get all entries from permanent H2 storage via backend
     */
    public List<DiaryEntryUI> getAllEntries(String username) {
        try {
            String url;
            if (username != null && !username.trim().isEmpty()) {
                url = API_BASE + "/entries?username=" + username;
            } else {
                url = API_BASE + "/entries";
            }
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Loading entries from: " + url + " - Status: " + response.statusCode());
            System.out.println("Response body: " + response.body());
            
            if (response.statusCode() == 200) {
                List<BackendEntryDto> backendEntries = objectMapper.readValue(
                        response.body(), 
                        new TypeReference<List<BackendEntryDto>>() {}
                );
                
                System.out.println("Successfully loaded " + backendEntries.size() + " entries from backend");
                return convertToUIEntries(backendEntries);
            } else {
                System.err.println("Failed to load entries - HTTP status: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Failed to load entries from backend: " + e.getMessage());
            e.printStackTrace();
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Get recent entries from permanent H2 storage
     */
    public List<DiaryEntryUI> getRecentEntries(String username, int limit) {
        List<DiaryEntryUI> allEntries = getAllEntries(username);
        return allEntries.stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(limit)
                .toList();
    }
    
    /**
     * Get most recent entry from permanent H2 storage
     */
    public DiaryEntryUI getMostRecentEntry(String username) {
        List<DiaryEntryUI> entries = getAllEntries(username);
        if (entries.isEmpty()) {
            return null;
        }
        
        return entries.stream()
                .max((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                .orElse(null);
    }
    
    /**
     * Delete entry from permanent H2 storage
     */
    public void deleteEntry(String username, DiaryEntryUI entry) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/entry/" + entry.getDbId()))
                    .DELETE()
                    .build();
            
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Entry deleted from permanent H2 storage for user: " + username);
            
        } catch (Exception e) {
            System.err.println("Failed to delete entry from backend: " + e.getMessage());
        }
    }
    
    private List<DiaryEntryUI> convertToUIEntries(List<BackendEntryDto> backendEntries) {
        List<DiaryEntryUI> uiEntries = new ArrayList<>();
        
        for (BackendEntryDto dto : backendEntries) {
            DiaryEntryUI uiEntry = new DiaryEntryUI(
                dto.id(),
                dto.createdAt() != null ? 
                    LocalDateTime.parse(dto.createdAt(), DateTimeFormatter.ISO_LOCAL_DATE_TIME) :
                    LocalDateTime.now(),
                dto.content(),
                dto.sentimentResult() != null ? dto.sentimentResult() : "Neutral",
                "AI Analysis: " + (dto.sentimentResult() != null ? dto.sentimentResult() : "No analysis")
            );
            uiEntries.add(uiEntry);
        }
        
        return uiEntries;
    }
    
    // Records for backend communication
    private record NewEntryPayload(String title, String content) {}
    private record BackendEntryDto(Long id, String title, String content, String createdAt, String sentimentResult, String username) {}
    
    /**
     * UI-compatible diary entry class that matches the old interface
     */
    public static class DiaryEntryUI {
        private final Long dbId;
        private final LocalDateTime timestamp;
        private final String content;
        private final String mood;
        private final String analysis;
        
        public DiaryEntryUI(Long dbId, LocalDateTime timestamp, String content, String mood, String analysis) {
            this.dbId = dbId;
            this.timestamp = timestamp;
            this.content = content;
            this.mood = mood;
            this.analysis = analysis;
        }
        
        public Long getDbId() { return dbId; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getContent() { return content; }
        public String getMood() { return mood; }
        public String getAnalysis() { return analysis; }
        
        public String getFormattedDate() {
            return timestamp.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        }
    }
}
