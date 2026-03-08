package com.mindscribe.ai;

import org.springframework.stereotype.Service;

/**
 * Analyzes journal text and returns a sentiment label.
 * Uses keyword-based logic; can be replaced with ONNX TinyBERT when model is available.
 * Returns: Positive, Neutral, Reflective, or Crisis (triggers alert when Crisis).
 */
@Service
public class TinyBertService {

    private static final String CRISIS = "Crisis";
    private static final String POSITIVE = "Positive";
    private static final String REFLECTIVE = "Reflective";
    private static final String NEUTRAL = "Neutral";

    /**
     * Analyzes the given text and returns a sentiment label.
     * Crisis: self-harm / suicide / severe distress keywords.
     */
    public String analyzeSentiment(String text) {
        if (text == null || text.isBlank()) {
            return NEUTRAL;
        }
        String lower = text.toLowerCase().trim();

        // Crisis indicators (trigger alert)
        if (lower.matches(".*\\b(suicide|kill myself|end (it|my life)|self[- ]?harm|want to die|can't go on|no way out)\\b.*")
                || lower.contains("hurt myself")
                || lower.contains("don't want to live")) {
            return CRISIS;
        }

        // Positive
        if (lower.matches(".*\\b(grateful|happy|excited|joy|relieved|hopeful|love|great|amazing)\\b.*")) {
            return POSITIVE;
        }

        // Reflective / negative but not crisis
        if (lower.matches(".*\\b(tired|sad|anxious|stressed|worried|overwhelmed|lonely|angry)\\b.*")) {
            return REFLECTIVE;
        }

        return NEUTRAL;
    }
}
