package com.mindscribe.ai.dto;

import java.util.List;

public class OverviewResponse {
    private String message;
    private List<EmotionShare> topEmotions;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<EmotionShare> getTopEmotions() { return topEmotions; }
    public void setTopEmotions(List<EmotionShare> topEmotions) {
        this.topEmotions = topEmotions;
    }
}
