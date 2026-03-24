package com.smartiadev.messaging_service.dto;

import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String email
) {}