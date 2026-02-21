package com.mindscribe.ai.dto;

public class EmotionShare {
    private String label;
    private double percentage;

    public EmotionShare() {}

    public EmotionShare(String label, double percentage) {
        this.label = label;
        this.percentage = percentage;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
}

