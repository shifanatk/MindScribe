package com.mindscribe.repository.h2;

import com.mindscribe.model.h2.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
}
