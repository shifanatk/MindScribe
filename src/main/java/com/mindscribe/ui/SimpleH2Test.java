package com.mindscribe.ui;

public class SimpleH2Test {
    public static void main(String[] args) {
        System.out.println("Testing H2 Database Service...");
        
        try {
            H2DiaryService service = H2DiaryService.getInstance();
            
            // Test saving an entry
            H2DiaryService.JournalEntry entry = service.saveEntry(
                "Test Entry", 
                "This is a test to verify H2 is working", 
                "Positive"
            );
            
            if (entry != null) {
                System.out.println("✓ Entry saved with ID: " + entry.getId());
                
                // Test loading entries
                var entries = service.getAllEntries();
                System.out.println("✓ Total entries in database: " + entries.size());
                
                for (H2DiaryService.JournalEntry e : entries) {
                    System.out.println("  - " + e.getTitle() + " (ID: " + e.getId() + ")");
                }
                
                System.out.println("✓ H2 Database is working correctly!");
            } else {
                System.out.println("✗ Failed to save entry");
            }
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
