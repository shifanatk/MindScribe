package com.mindscribe.controller;

import com.mindscribe.core.DiaryService;
import com.mindscribe.model.JournalEntry;
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

    public record NewEntryRequest(String title, String content) {}

    @PostMapping("/entry")
    public JournalEntry createEntry(@RequestBody NewEntryRequest request) {
        return diaryService.createEntry(request.title(), request.content());
    }

    @GetMapping("/entries")
    public List<JournalEntry> getEntries() {
        return diaryService.getAllEntries();
    }
}