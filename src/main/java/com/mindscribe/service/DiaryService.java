package com.mindscribe.service;

import com.mindscribe.dto.CalendarDayDto;
import com.mindscribe.dto.DiaryEntryUpdateDto;
import com.mindscribe.model.DiaryEntry;
import com.mindscribe.model.User;
import com.mindscribe.repository.DiaryEntryRepository;
import com.mindscribe.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Map;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        String username = auth.getName();

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

    public DiaryEntry updateEntry(Long id, @Valid DiaryEntryUpdateDto dto) {
        DiaryEntry entry = diaryEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        if (!entry.getUserId().equals(getCurrentUserId())) {
            throw new RuntimeException("Cannot edit other user's entry");
        }

        entry.setContent(dto.content());
        entry.setMood(dto.mood());
        return diaryEntryRepository.save(entry);
    }

    public DiaryEntry getEntry(Long id) {
        DiaryEntry entry = diaryEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        if (!entry.getUserId().equals(getCurrentUserId())) {
            throw new RuntimeException("Cannot view other user's entry");
        }
        return entry;
    }

    public void deleteEntry(Long id) {
        DiaryEntry entry = diaryEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        if (!entry.getUserId().equals(getCurrentUserId())) {
            throw new RuntimeException("Cannot delete other user's entry");
        }
        diaryEntryRepository.delete(entry);
    }
public List<CalendarDayDto> getCalendarData(String month) {
    YearMonth ym = YearMonth.parse(month); // "2026-02"
    LocalDate startDate = ym.atDay(1);
    LocalDate endDate = ym.atEndOfMonth();

    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endDate.plusDays(1).atStartOfDay();

    Long userId = getCurrentUserId();
    List<DiaryEntry> entries =
            diaryEntryRepository.findByUserIdAndCreatedAtBetween(userId, start, end);

    Map<LocalDate, List<DiaryEntry>> byDate = entries.stream()
            .collect(Collectors.groupingBy(e -> e.getCreatedAt().toLocalDate()));

    List<CalendarDayDto> result = new ArrayList<>();

    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
        List<DiaryEntry> dayEntries = byDate.getOrDefault(date, List.of());
        boolean hasEntry = !dayEntries.isEmpty();
        long count = dayEntries.size();
        String dominantMood = hasEntry
                ? dayEntries.get(0).getMood()   // simple: first mood; you can improve later
                : null;

        result.add(new CalendarDayDto(date, dominantMood, hasEntry, count));
    }

    return result;
}


}



