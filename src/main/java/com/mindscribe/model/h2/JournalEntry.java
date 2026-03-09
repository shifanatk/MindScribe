package com.mindscribe.model.h2;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * AI-derived sentiment result (e.g. Positive, Neutral, Reflective, Crisis).
     * Stored in H2 only and used for the Emotion Dashboard / Mood Calendar.
     */
    @Column(name = "sentiment_result")
    private String sentimentResult;

    /**
     * Username of the entry author
     */
    @Column(name = "username", nullable = false)
    private String username;

    private LocalDateTime createdAt;

    public JournalEntry() {
    }

    public JournalEntry(String title, String content) {
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public JournalEntry(String title, String content, String username) {
        this.title = title;
        this.content = content;
        this.username = username;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSentimentResult() {
        return sentimentResult;
    }

    public void setSentimentResult(String sentimentResult) {
        this.sentimentResult = sentimentResult;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
