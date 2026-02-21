package com.mindscribe.ai;

import com.mindscribe.ai.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiService {

    public OverviewResponse buildPastOverview(Long userId, int days) {
        OverviewResponse res = new OverviewResponse();
        res.setMessage(
            "Hey there, over the last " + days +
            " days your entries show mostly calm and joyful moods, " +
            "with a few stressed days around deadlines."
        );
        res.setTopEmotions(List.of(
            new EmotionShare("joy", 0.55),
            new EmotionShare("calm", 0.25),
            new EmotionShare("anxiety", 0.20)
        ));
        return res;
    }

    public DetailedAnalysisResponse analyzeEntry(AnalyzeRequest req) {
        DetailedAnalysisResponse res = new DetailedAnalysisResponse();
        res.setSummary(
            "You seem a bit overwhelmed but also motivated to improve."
        );
        res.setPrimaryEmotion("anxiety");
        res.setEmotionScores(List.of(
            new EmotionScore("anxiety", 0.76),
            new EmotionScore("joy", 0.32),
            new EmotionScore("sadness", 0.18)
        ));
        res.setAdvice(
            "Take 5 minutes to breathe, then note one win from today."
        );
        return res;
    }
}

