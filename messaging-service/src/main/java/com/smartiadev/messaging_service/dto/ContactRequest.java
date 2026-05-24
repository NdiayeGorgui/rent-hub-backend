package com.smartiadev.messaging_service.dto;

public record ContactRequest(
        String firstName,
        String lastName,
        String email,
        String subject,
        String message
) {}
