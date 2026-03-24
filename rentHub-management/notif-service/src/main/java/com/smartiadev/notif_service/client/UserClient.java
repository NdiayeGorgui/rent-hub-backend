package com.smartiadev.notif_service.client;

import com.smartiadev.notif_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "auth-service")
public interface UserClient {

    @GetMapping("/api/users/internal/{id}")
    UserResponse getUserById(@PathVariable UUID id);

}