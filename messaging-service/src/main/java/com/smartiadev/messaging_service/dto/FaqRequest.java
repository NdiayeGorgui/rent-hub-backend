package com.smartiadev.messaging_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FaqRequest(

        @NotBlank(message = "Theme is required")
        String theme,

        @NotBlank(message = "Question is required")
        String question,

        @NotBlank(message = "Answer is required")
        @Size(max = 2000, message = "Answer must not exceed 2000 characters")
        String answer

) {}