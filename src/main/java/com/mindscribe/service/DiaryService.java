package com.mindscribe.service;

import com.mindscribe.model.DiaryEntry;
import com.mindscribe.model.User;
import com.mindscribe.repository.DiaryEntryRepository;
import com.mindscribe.repository.UserRepository;
import org.springframework.security.core.Authentication;
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

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // e.g. "rasheeda"

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return user.getId();
    }

    public DiaryEntry createEntry(String content, String mood) {
        Long userId = getCurrentUserId();

        DiaryEntry entry = new DiaryEntry(userId, content, mood);
        return diaryEntryRepository.save(entry);
    }

    public List<DiaryEntry> getEntriesForCurrentUser() {
        Long userId = getCurrentUserId();
        return diaryEntryRepository.findByUserId(userId);
    }
}


