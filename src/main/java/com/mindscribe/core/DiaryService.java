package com.mindscribe.core;

import com.mindscribe.ai.TinyBertService;
import com.mindscribe.model.h2.JournalEntry;
import com.mindscribe.repository.h2.JournalEntryRepository;
import com.mindscribe.service.CrisisAlertService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DiaryService {

    private static final String SENTIMENT_CRISIS = "Crisis";

    private final JournalEntryRepository repository;
    private final TinyBertService tinyBertService;
    private final CrisisAlertService crisisAlertService;

    public DiaryService(JournalEntryRepository repository,
                        TinyBertService tinyBertService,
                        CrisisAlertService crisisAlertService) {
        this.repository = repository;
        this.tinyBertService = tinyBertService;
        this.crisisAlertService = crisisAlertService;
    }

    /**
     * Saves a journal entry. AI (TinyBertService) analyzes the text;
     * sentiment is stored in H2. If sentiment is "Crisis", triggers Java Mail alert.
     */
    @Transactional(transactionManager = "h2TransactionManager")
    public JournalEntry createEntry(String title, String content, String username) {
        String sentiment = tinyBertService.analyzeSentiment(content != null ? content : "");
        JournalEntry entry = new JournalEntry(title, content, username);
        entry.setSentimentResult(sentiment);
        JournalEntry saved = repository.save(entry);
        if (SENTIMENT_CRISIS.equals(sentiment)) {
            crisisAlertService.sendCrisisAlert(content);
        }
        return saved;
    }

    @Transactional(transactionManager = "h2TransactionManager", readOnly = true)
    public List<JournalEntry> getAllEntries(String username) {
        if (username != null && !username.trim().isEmpty()) {
            return repository.findByUsername(username);
        } else {
            return repository.findAll();
        }
    }

    /**
     * Returns sentiment counts per day for use by the Mood Calendar / Emotion Dashboard.
     */
    @Transactional(transactionManager = "h2TransactionManager", readOnly = true)
    public Map<LocalDate, Map<String, Long>> getMoodCalendar() {
        return repository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCreatedAt() != null ? e.getCreatedAt().toLocalDate() : LocalDate.now(),
                        Collectors.groupingBy(
                                e -> {
                                    String s = e.getSentimentResult();
                                    return (s == null || s.isBlank()) ? "Neutral" : s;
                                },
                                Collectors.counting()
                        )
                ));
    }
}

