
// src/main/java/com/mindscribe/dto/DiaryEntryUpdateDto.java
package com.mindscribe.dto;

import jakarta.validation.constraints.NotBlank;

public record DiaryEntryUpdateDto(
        @NotBlank String content,
        String mood
) {}


