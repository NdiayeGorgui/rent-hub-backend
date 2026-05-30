package com.smartiadev.rental_service.client;

import com.smartiadev.rental_service.dto.UserProfileInternalDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "auth-service",
        path = "/api/users"
)
public interface AuthClient {

    @PostMapping("/internal/user/batch")
    List<UserProfileInternalDto> getUsersBatch(
            @RequestBody List<UUID> ids
    );
}
