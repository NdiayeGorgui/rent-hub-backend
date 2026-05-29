package com.smartiadev.review_service.client;

import com.smartiadev.review_service.dto.UserProfileInternalDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/api/profile/{userId}")
    UserProfileInternalDto getUserById(@PathVariable UUID userId);

    @PostMapping("/api/profile/internal/batch")
    List<UserProfileInternalDto> getUsersBatch(
            @RequestBody List<UUID> ids
    );
}
