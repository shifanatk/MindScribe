package com.mindscribe.controller;

import com.mindscribe.core.DiaryService;
import com.mindscribe.model.h2.JournalEntry;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diary")
@CrossOrigin(origins = "*")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    public record NewEntryRequest(String title, String content) {}

    @GetMapping("/health")
    public String health() {
        return "{\"status\":\"MindScribe Diary API OK!\"}";
    }

    @PostMapping("/entry")
    public JournalEntry createEntry(@RequestParam String username, @RequestBody NewEntryRequest request) {
        return diaryService.createEntry(request.title(), request.content(), username);
    }

    @GetMapping("/entries")
    public List<JournalEntry> getEntries(@RequestParam String username) {
        if (username != null && !username.trim().isEmpty()) {
            return diaryService.getAllEntries(username);
        } else {
            return diaryService.getAllEntries(null);
        }
    }

    /**
     * Aggregated sentiment data for the Mood Calendar / Emotion Dashboard.
     * Returns a date -> {sentiment -> count} structure.
     */
    @GetMapping("/mood-calendar")
    public Map<LocalDate, Map<String, Long>> getMoodCalendar() {
        return diaryService.getMoodCalendar();
    }
}
