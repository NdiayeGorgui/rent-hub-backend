package com.smartiadev.auth_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class UserResponseDto {

    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String city;
    private String profilePicture;
    private boolean enabled;
    private LocalDateTime createdAt;
    private Set<String> roles;
    private String subscription;
}