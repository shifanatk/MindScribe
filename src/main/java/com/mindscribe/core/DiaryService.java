package com.mindscribe.core;

import com.mindscribe.model.JournalEntry;
import com.mindscribe.model.JournalEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaryService {

    private final JournalEntryRepository repository;

    public DiaryService(JournalEntryRepository repository) {
        this.repository = repository;
    }

    public JournalEntry createEntry(String title, String content) {
        JournalEntry entry = new JournalEntry(title, content);
        return repository.save(entry);
    }

    public List<JournalEntry> getAllEntries() {
        return repository.findAll();
    }
}

