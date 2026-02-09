package com.mindscribe.service;

import com.mindscribe.model.DiaryEntry;
import com.mindscribe.repository.DiaryEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaryService {

    private final DiaryEntryRepository diaryEntryRepository;

    public DiaryService(DiaryEntryRepository diaryEntryRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
    }

    public DiaryEntry createEntry(Long userId, String content, String mood) {
        DiaryEntry entry = new DiaryEntry(userId, content, mood);
        return diaryEntryRepository.save(entry);
    }

    public List<DiaryEntry> getEntriesForUser(Long userId) {
        return diaryEntryRepository.findByUserId(userId);
    }
}


