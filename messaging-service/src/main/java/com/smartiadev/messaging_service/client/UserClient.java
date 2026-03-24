package com.smartiadev.messaging_service.client;

import com.smartiadev.messaging_service.dto.UserDto;
import com.smartiadev.messaging_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "auth-service")
public interface UserClient {

    @GetMapping("/api/users/internal/{id}")
    UserResponse getUserById(@PathVariable UUID id);

    @GetMapping("/api/users/internal/admins")
    List<UserResponse> getAdmins();

}