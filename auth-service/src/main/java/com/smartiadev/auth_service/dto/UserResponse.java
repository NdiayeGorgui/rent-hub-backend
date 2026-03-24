package com.smartiadev.auth_service.dto;



import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        String username,
        Set<String> roles
) {}
