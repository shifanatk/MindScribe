package com.mindscribe.dto;

import java.time.LocalDate;

public record CalendarDayDto(
        LocalDate date,
        String dominantMood,
        boolean hasEntry,
        long entryCount
) {}
