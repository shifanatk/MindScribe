package com.mindscribe.controller;

import com.mindscribe.dto.CalendarDayDto;
import com.mindscribe.dto.DiaryEntryUpdateDto;
import com.mindscribe.model.DiaryEntry;
import com.mindscribe.service.DiaryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import com.mindscribe.core.DiaryService;
import com.mindscribe.model.JournalEntry;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diary")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    // CREATE entry (you probably already have something like this)
    @PostMapping("/entries")
    public ResponseEntity<DiaryEntry> createEntry(@RequestBody DiaryEntry entryRequest) {
        DiaryEntry saved = diaryService.createEntry(entryRequest.getContent(), entryRequest.getMood());
        return ResponseEntity.ok(saved);
    }

    // LIST entries for current user
    @GetMapping("/entries")
    public ResponseEntity<List<DiaryEntry>> getEntriesForCurrentUser() {
        List<DiaryEntry> entries = diaryService.getEntriesForCurrentUser();
        return ResponseEntity.ok(entries);
    }

    // NEW: UPDATE entry
    @PutMapping("/entries/{id}")
    public ResponseEntity<DiaryEntry> updateEntry(
            @PathVariable Long id,
            @RequestBody @Valid DiaryEntryUpdateDto dto) {

        DiaryEntry updated = diaryService.updateEntry(id, dto);
        return ResponseEntity.ok(updated);
    }

    // NEW: GET single entry
    @GetMapping("/entries/{id}")
    public ResponseEntity<DiaryEntry> getEntry(@PathVariable Long id) {
        DiaryEntry entry = diaryService.getEntry(id);
        return ResponseEntity.ok(entry);
    }

    // NEW: DELETE entry
    @DeleteMapping("/entries/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        diaryService.deleteEntry(id);
        return ResponseEntity.noContent().build();
    }

    // NEW: CALENDAR data for a month (e.g. month=2026-02)
    @GetMapping("/calendar")
    public ResponseEntity<List<CalendarDayDto>> getCalendar(@RequestParam String month) {
        List<CalendarDayDto> calendar = diaryService.getCalendarData(month);
        return ResponseEntity.ok(calendar);
    }

    // Health check (you already had something similar)
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok("{\"status\":\"MindScribe Diary API OK!\"}");
    }
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
