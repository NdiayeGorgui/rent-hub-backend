package com.smartiadev.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        String username,
        @Email
        @NotBlank
        String email,
        @NotBlank
        @Size(min = 6)
        String password,
        String fullName,
        String phone,
        String city
) {}

