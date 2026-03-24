package com.smartiadev.auth_service.dto;

import java.util.Set;
import java.util.UUID;

public record AdminUserResponse(
        UUID id,
        String username,
        String email,
        String fullName,
        String phone,
        String city,
        boolean enabled,
        Set<String> roles,
        String subscription
) {}