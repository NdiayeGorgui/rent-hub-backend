package com.smartiadev.payments_service.client;

import com.smartiadev.payments_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "auth-service")
public interface UserClient {

    @GetMapping("/api/users/internal/{id}")
    UserResponse getUser(@PathVariable UUID id);

    @GetMapping("/api/users/internal/admins")
    List<UserResponse> getAdmins();

    @PostMapping("/api/users/internal/user/batch")
    List<UserResponse> getUsersBatch(
            @RequestBody List<UUID> ids
    );

}