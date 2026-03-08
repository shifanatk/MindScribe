package com.mindscribe.ai;

import com.mindscribe.ai.dto.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    // 1) Past emotion overview (before user types today’s entry)
    @GetMapping("/overview/{userId}")
    public OverviewResponse getOverview(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "30") int days) {

        return aiService.buildPastOverview(userId, days);
    }

    // 2) Detailed analysis for the new entry
    @PostMapping("/analyze")
    public DetailedAnalysisResponse analyze(@RequestBody AnalyzeRequest req) {
        return aiService.analyzeEntry(req);
    }
}

