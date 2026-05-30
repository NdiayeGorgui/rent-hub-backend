package com.smartiadev.auth_service.controller;


import com.smartiadev.auth_service.dto.AuctionStrikeResponse;
import com.smartiadev.auth_service.dto.UserBidEligibilityResponse;
import com.smartiadev.auth_service.dto.UserProfileInternalDto;
import com.smartiadev.auth_service.dto.UserResponse;

import com.smartiadev.auth_service.entity.User;
import com.smartiadev.auth_service.repository.UserRepository;
import com.smartiadev.auth_service.service.AuthService;
import com.smartiadev.auth_service.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserInternalController {

    private final AuthService userService;
    private final ProfileService profileService;
    private final UserRepository userRepository;

    @GetMapping("/internal/{id}")
    public UserResponse getUser(@PathVariable UUID id) {
        return userService.getUser(id);
    }

    @PostMapping("/internal/batch")
    public List<UserResponse> getUsersBatch(
            @RequestBody List<UUID> ids
    ) {
        return userRepository.findAllById(ids)
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getUsername(),
                        user.getRoles()
                ))
                .toList();
    }

    @PostMapping("/api/users/internal/user/batch")
    public List<UserProfileInternalDto> getUserBatch(
            @RequestBody List<UUID> ids
    ) {
        return profileService.getUserBatch(ids);
    }


    @GetMapping("/internal/{id}/can-bid")
    public UserBidEligibilityResponse canUserBid(@PathVariable UUID id) {
        return userService.checkBidEligibility(id);
    }

    @PostMapping("/internal/{id}/auction-strike")
    public AuctionStrikeResponse addAuctionStrike(@PathVariable UUID id) {
        return userService.addAuctionStrike(id);
    }

    @GetMapping("/internal/admins")
    public List<UserResponse> getAdmins() {
        return userService.getAdmins();
    }
}
