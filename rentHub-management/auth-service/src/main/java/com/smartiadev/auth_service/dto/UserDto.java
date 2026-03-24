package com.smartiadev.auth_service.dto;


import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String email
) {}
