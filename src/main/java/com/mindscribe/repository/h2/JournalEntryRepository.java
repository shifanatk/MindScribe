package com.mindscribe.repository.h2;

import com.mindscribe.model.h2.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    
    List<JournalEntry> findByUsername(String username);
    
    Optional<JournalEntry> findByIdAndUsername(Long id, String username);
}
