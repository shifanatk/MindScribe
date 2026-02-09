package com.mindscribe.controller;

import com.mindscribe.model.DiaryEntry;
import com.mindscribe.service.DiaryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diary")
@CrossOrigin(origins = "*")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @GetMapping("/health")
    public String health() {
        return "{\"status\":\"MindScribe Diary API OK!\"}";
    }

    // For now, we accept raw text and use userId = 1 and mood = "positive"
    @PostMapping("/entry")
    public DiaryEntry createEntry(@RequestBody String content) {
        return diaryService.createEntry(1L, content, "positive");
    }

    @GetMapping("/entries")
    public List<DiaryEntry> getEntries() {
        return diaryService.getEntriesForUser(1L);
    }
}


