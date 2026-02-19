package com.mindscribe.service;

import com.mindscribe.model.DiaryEntry;
import com.mindscribe.model.User;
import com.mindscribe.repository.DiaryEntryRepository;
import com.mindscribe.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaryService {

    private final DiaryEntryRepository diaryEntryRepository;
    private final UserRepository userRepository;

    public DiaryService(DiaryEntryRepository diaryEntryRepository,
                        UserRepository userRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
        this.userRepository = userRepository;
    }

    public DiaryEntry createEntry(String content, String mood) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        DiaryEntry entry = new DiaryEntry(user.getId(), content, mood);
        return diaryEntryRepository.save(entry);
    }

    public List<DiaryEntry> getEntriesForCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return diaryEntryRepository.findByUserId(user.getId());
    }
}
