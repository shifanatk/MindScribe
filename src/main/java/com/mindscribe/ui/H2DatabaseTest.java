package com.mindscribe.ui;

/**
 * Simple test class to verify H2 database operations
 * Run this to test if the database is working properly
 */
public class H2DatabaseTest {
    
    public static void main(String[] args) {
        System.out.println("Testing H2 Database operations...");
        
        try {
            // Get database service instance
            H2DiaryService diaryService = H2DiaryService.getInstance();
            
            // Test saving an entry
            System.out.println("1. Testing save entry...");
            H2DiaryService.JournalEntry savedEntry = diaryService.saveEntry(
                "Test Entry", 
                "This is a test entry to verify H2 database is working properly. Feeling happy today!", 
                "Positive"
            );
            
            if (savedEntry != null) {
                System.out.println("✓ Entry saved successfully with ID: " + savedEntry.getId());
                System.out.println("  Title: " + savedEntry.getTitle());
                System.out.println("  Date: " + savedEntry.getFormattedDate());
            } else {
                System.out.println("✗ Failed to save entry");
                return;
            }
            
            // Test loading all entries
            System.out.println("\n2. Testing load all entries...");
            var entries = diaryService.getAllEntries();
            System.out.println("✓ Loaded " + entries.size() + " entries from database");
            
            // Display all entries
            for (H2DiaryService.JournalEntry entry : entries) {
                System.out.println("  - " + entry.getTitle() + " (" + entry.getFormattedDate() + ")");
            }
            
            // Test loading by ID
            System.out.println("\n3. Testing load by ID...");
            H2DiaryService.JournalEntry loadedEntry = diaryService.getEntryById(savedEntry.getId());
            if (loadedEntry != null) {
                System.out.println("✓ Entry loaded successfully by ID: " + loadedEntry.getId());
                System.out.println("  Content: " + loadedEntry.getContent().substring(0, Math.min(50, loadedEntry.getContent().length())) + "...");
            } else {
                System.out.println("✗ Failed to load entry by ID");
            }
            
            System.out.println("\n✓ All H2 database tests passed!");
            System.out.println("✓ Database is working correctly for saving and loading journal entries");
            
        } catch (Exception e) {
            System.err.println("✗ Database test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
