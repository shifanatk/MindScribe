package com.mindscribe.service;

public class AnalysisResult {
    private String emotion;
    private float confidence;
    private String originalText;
    
    public AnalysisResult(String emotion, float confidence, String originalText) {
        this.emotion = emotion;
        this.confidence = confidence;
        this.originalText = originalText;
    }
    
    public String getEmotion() {
        return emotion;
    }
    
    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }
    
    public float getConfidence() {
        return confidence;
    }
    
    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }
    
    public String getOriginalText() {
        return originalText;
    }
    
    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }
    
    @Override
    public String toString() {
        return String.format("Emotion: %s (Confidence: %.2f%%)", emotion, confidence * 100);
    }
}
