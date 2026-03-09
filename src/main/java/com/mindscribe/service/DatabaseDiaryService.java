package com.mindscribe.service;

import com.mindscribe.model.h2.DiaryEntry;
import com.mindscribe.repository.h2.DiaryEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

/**
 * Database-backed diary service that uses H2 database for persistence
 */
@Service
@Transactional
public class DatabaseDiaryService {
    
    @Autowired
    private DiaryEntryRepository diaryEntryRepository;
    
    @PostConstruct
    public void init() {
        System.out.println("DatabaseDiaryService initialized with H2 backend");
    }
    
    /**
     * Save a new diary entry to H2 database
     */
    public DiaryEntry saveEntry(String userId, String content, String mood) {
        DiaryEntry entry = new DiaryEntry(userId, content, mood);
        return diaryEntryRepository.save(entry);
    }
    
    /**
     * Delete a diary entry by ID
     */
    public void deleteEntry(Long entryId) {
        diaryEntryRepository.deleteById(entryId);
    }
    
    /**
     * Get all entries for a specific user
     */
    public List<DiaryEntry> getAllEntries(String userId) {
        return diaryEntryRepository.findByUserId(userId);
    }
    
    /**
     * Get the most recent entry for a user
     */
    public Optional<DiaryEntry> getMostRecentEntry(String userId) {
        List<DiaryEntry> entries = diaryEntryRepository.findByUserId(userId);
        if (entries.isEmpty()) {
            return Optional.empty();
        }
        
        return entries.stream()
                .max((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
    }
    
    /**
     * Get recent entries for a user with limit
     */
    public List<DiaryEntry> getRecentEntries(String userId, int limit) {
        List<DiaryEntry> entries = diaryEntryRepository.findByUserId(userId);
        // Sort by creation date descending and limit
        return entries.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .toList();
    }
    
    /**
     * Get entry count for a user
     */
    public long getEntryCount(String userId) {
        return diaryEntryRepository.findByUserId(userId).size();
    }
    
    /**
     * Update an existing entry
     */
    public Optional<DiaryEntry> updateEntry(Long entryId, String content, String mood) {
        Optional<DiaryEntry> existingEntry = diaryEntryRepository.findById(entryId);
        if (existingEntry.isPresent()) {
            DiaryEntry entry = existingEntry.get();
            entry.setContent(content);
            entry.setMood(mood);
            return Optional.of(diaryEntryRepository.save(entry));
        }
        return Optional.empty();
    }
}
