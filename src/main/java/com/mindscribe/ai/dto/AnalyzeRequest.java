package com.mindscribe.ai.dto;

public class AnalyzeRequest {
    private Long userId;
    private Long entryId;
    private String text;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}

