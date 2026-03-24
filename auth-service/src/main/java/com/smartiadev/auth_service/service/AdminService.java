package com.smartiadev.auth_service.service;

import com.smartiadev.auth_service.client.SubscriptionClient;
import com.smartiadev.auth_service.dto.PremiumStatusResponse;
import com.smartiadev.auth_service.dto.response.UserResponseDto;
import com.smartiadev.auth_service.entity.User;
import com.smartiadev.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final SubscriptionClient subscriptionClient;


    public void suspendUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void activateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private UserResponseDto mapToDto(User user) {

        boolean premium = false;

        try {
            PremiumStatusResponse response =
                    subscriptionClient.getPremiumStatus(user.getId());

            premium = response.premium();
        } catch (Exception ignored) {}

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .city(user.getCity())
                .profilePicture(user.getProfilePicture())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles())
                .subscription(premium ? "PREMIUM" : "STANDARD")
                .build();
    }
}

