package com.smartiadev.messaging_service.dto;

import lombok.Builder;

@Builder
public record FaqResponse(
        Long id,
        String theme,
        String question,
        String answer
) {}