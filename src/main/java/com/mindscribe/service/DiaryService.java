package com.mindscribe.service;

import com.mindscribe.model.DiaryEntry;
import com.mindscribe.repository.DiaryEntryRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaryService {

    private final DiaryEntryRepository diaryEntryRepository;

    public DiaryService(DiaryEntryRepository diaryEntryRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
    }

    public DiaryEntry createEntry(String content, String mood) {
        // TEMP: fake user id until you wire real users
        Long userId = 1L;

        DiaryEntry entry = new DiaryEntry(userId, content, mood);
        // createdAt is set in the constructor; updatedAt via @UpdateTimestamp
        return diaryEntryRepository.save(entry);
    }

    public List<DiaryEntry> getEntriesForCurrentUser() {
        Long userId = 1L; // TEMP: same fake user
        return diaryEntryRepository.findByUserId(userId);
    }
}

