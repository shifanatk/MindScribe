package com.mindscribe.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diary")
@CrossOrigin(origins = "*")
public class DiaryController {

    @GetMapping("/health")
    public String health() {
        return "{\"status\":\"MindScribe Diary API OK!\"}";
    }

    @PostMapping("/entry")
    public String createEntry(@RequestBody String content) {
        return "{\"saved\":\"" + content + "\", \"mood\":\"positive\"}";
    }

    @GetMapping("/entries")
    public String getEntries() {
        return "[{\"id\":1, \"content\":\"Sample diary\", \"date\":\"2026-01-25\"}]";
    }
}