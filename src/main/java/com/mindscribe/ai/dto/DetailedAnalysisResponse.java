package com.mindscribe.ai.dto;

import java.util.List;

public class DetailedAnalysisResponse {
    private String summary;
    private String primaryEmotion;
    private List<EmotionScore> emotionScores;
    private String advice;

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getPrimaryEmotion() { return primaryEmotion; }
    public void setPrimaryEmotion(String primaryEmotion) { this.primaryEmotion = primaryEmotion; }

    public List<EmotionScore> getEmotionScores() { return emotionScores; }
    public void setEmotionScores(List<EmotionScore> emotionScores) { this.emotionScores = emotionScores; }

    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }
}
