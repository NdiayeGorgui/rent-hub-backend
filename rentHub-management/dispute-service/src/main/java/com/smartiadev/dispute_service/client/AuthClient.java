package com.smartiadev.dispute_service.client;

import com.smartiadev.dispute_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @PutMapping("/api/admin/users/{id}/suspend")
    void suspend(@PathVariable UUID id);

    @GetMapping("/api/users/internal/admins")
    List<UserResponse> getAdmins();
}

