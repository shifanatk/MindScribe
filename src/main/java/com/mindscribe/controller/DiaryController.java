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

    // Create a diary entry: body is plain text
    @PostMapping("/entry")
    public DiaryEntry createEntry(@RequestBody String content) {
        return diaryService.createEntry(content, "neutral");
    }

    // Get all entries for the current user
    @GetMapping("/entries")
    public List<DiaryEntry> getEntriesForCurrentUser() {
        return diaryService.getEntriesForCurrentUser();
    }
}


