package com.mindscribe.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Emergency Contact Service for crisis detection and notification
 */
public class EmergencyContactService {
    
    private static final String CONFIG_FILE = "emergency_contacts.properties";
    private static final double THREAT_THRESHOLD = 0.75; // 75% negative sentiment threshold
    
    private Properties properties;
    private List<EmergencyContact> contacts;
    
    public EmergencyContactService() {
        this.contacts = new ArrayList<>();
        loadContacts();
    }
    
    /**
     * Emergency contact model
     */
    public static class EmergencyContact {
        private String name;
        private String email;
        private String phone;
        private String relationship;
        private boolean isPrimary;
        
        public EmergencyContact(String name, String email, String phone, String relationship, boolean isPrimary) {
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.relationship = relationship;
            this.isPrimary = isPrimary;
        }
        
        // Getters
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getRelationship() { return relationship; }
        public boolean isPrimary() { return isPrimary; }
    }
    
    /**
     * Check if entry indicates crisis based on sentiment analysis
     */
    public boolean isCrisisDetected(String analysis) {
        if (analysis == null || analysis.isEmpty()) {
            return false;
        }
        
        // Look for negative sentiment indicators
        String lowerAnalysis = analysis.toLowerCase();
        
        // Count negative indicators
        int negativeCount = 0;
        int totalIndicators = 0;
        
        String[] negativeIndicators = {
            "sad", "anxious", "depressed", "worried", "scared", 
            "hopeless", "helpless", "overwhelmed", "desperate", "suicidal",
            "panic", "alone", "empty", "lost", "broken", "pain", "hurt",
            "angry", "frustrated", "stressed", "afraid", "nervous", "tense"
        };
        
        for (String indicator : negativeIndicators) {
            totalIndicators++;
            if (lowerAnalysis.contains(indicator)) {
                negativeCount++;
            }
        }
        
        // Calculate negative sentiment ratio
        if (totalIndicators == 0) return false;
        
        double negativeRatio = (double) negativeCount / totalIndicators;
        return negativeRatio >= THREAT_THRESHOLD;
    }
    
    /**
     * Send emergency notifications to all contacts
     */
    public void sendEmergencyNotifications(String username, String entryContent, String analysis) {
        if (!isCrisisDetected(analysis)) {
            return; // No crisis detected
        }
        
        System.out.println("CRISIS DETECTED - Sending emergency notifications for user: " + username);
        System.out.println("Entry content: " + entryContent);
        System.out.println("Analysis: " + analysis);
        
        // Send notifications (in real app, would use actual email/SMS service)
        for (EmergencyContact contact : contacts) {
            if (contact.isPrimary()) {
                sendEmailNotification(contact, username, entryContent, analysis);
                // In real app, also send SMS
                System.out.println("Emergency notification sent to primary contact: " + contact.getName());
            }
        }
    }
    
    /**
     * Send email notification (mock implementation)
     */
    private void sendEmailNotification(EmergencyContact contact, String username, String entryContent, String analysis) {
        try {
            // Mock email sending - in real app, configure proper SMTP
            String subject = "🚨 MINDSCRIBE EMERGENCY ALERT - " + username.toUpperCase();
            
            String message = String.format(
                "Dear %s,\n\n" +
                "This is an automated emergency alert from MindScribe.\n\n" +
                "User: %s\n" +
                "Time: %s\n\n" +
                "Concerning Entry:\n%s\n\n" +
                "AI Analysis:\n%s\n\n" +
                "This entry has been flagged for potential crisis based on sentiment analysis.\n" +
                "Please check on the user immediately.\n\n" +
                "MindScribe Emergency System\n" +
                "This is an automated message. Please contact the user directly.",
                contact.getRelationship(),
                username,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                entryContent.length() > 200 ? entryContent.substring(0, 197) + "..." : entryContent,
                analysis
            );
            
            System.out.println("EMAIL SENT TO: " + contact.getEmail());
            System.out.println("SUBJECT: " + subject);
            System.out.println("MESSAGE:\n" + message);
            
            // In real implementation, would use JavaMail API
            // Properties props = new Properties();
            // props.put("mail.smtp.host", "smtp.gmail.com");
            // ... configure and send email
            
        } catch (Exception e) {
            System.err.println("Failed to send emergency email: " + e.getMessage());
        }
    }
    
    /**
     * Load emergency contacts from properties file
     */
    private void loadContacts() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(CONFIG_FILE));
            
            // Load contacts from properties
            String contact1Name = properties.getProperty("contact1.name", "Primary Contact");
            String contact1Email = properties.getProperty("contact1.email", "");
            String contact1Phone = properties.getProperty("contact1.phone", "");
            
            String contact2Name = properties.getProperty("contact2.name", "Secondary Contact");
            String contact2Email = properties.getProperty("contact2.email", "");
            String contact2Phone = properties.getProperty("contact2.phone", "");
            
            if (!contact1Email.isEmpty()) {
                contacts.add(new EmergencyContact(contact1Name, contact1Email, contact1Phone, "Primary", true));
            }
            
            if (!contact2Email.isEmpty()) {
                contacts.add(new EmergencyContact(contact2Name, contact2Email, contact2Phone, "Secondary", false));
            }
            
        } catch (IOException e) {
            System.err.println("Could not load emergency contacts: " + e.getMessage());
            // Add default contacts if file doesn't exist
            contacts.add(new EmergencyContact("Emergency Contact", "emergency@example.com", "+1234567890", "Primary", true));
        }
    }
    
    /**
     * Save emergency contacts to properties file
     */
    public void saveContacts(List<EmergencyContact> newContacts) {
        this.contacts = newContacts;
        try {
            for (int i = 0; i < contacts.size(); i++) {
                EmergencyContact contact = contacts.get(i);
                properties.setProperty("contact" + (i + 1) + ".name", contact.getName());
                properties.setProperty("contact" + (i + 1) + ".email", contact.getEmail());
                properties.setProperty("contact" + (i + 1) + ".phone", contact.getPhone());
            }
            
            properties.store(new FileOutputStream(CONFIG_FILE), "MindScribe Emergency Contacts");
            System.out.println("Emergency contacts saved successfully");
            
        } catch (IOException e) {
            System.err.println("Failed to save emergency contacts: " + e.getMessage());
        }
    }
    
    /**
     * Get all emergency contacts
     */
    public List<EmergencyContact> getContacts() {
        return new ArrayList<>(contacts);
    }
    
    /**
     * Add new emergency contact
     */
    public void addContact(String name, String email, String phone, boolean isPrimary) {
        EmergencyContact contact = new EmergencyContact(name, email, phone, "Custom", isPrimary);
        contacts.add(contact);
        saveContacts(contacts);
    }
    
    /**
     * Remove emergency contact
     */
    public void removeContact(int index) {
        if (index >= 0 && index < contacts.size()) {
            contacts.remove(index);
            saveContacts(contacts);
        }
    }
}
