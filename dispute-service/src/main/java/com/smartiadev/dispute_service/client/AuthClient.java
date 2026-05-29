package com.smartiadev.dispute_service.client;

import com.smartiadev.dispute_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @PutMapping("/api/admin/users/{id}/suspend")
    void suspend(@PathVariable UUID id);

    @GetMapping("/api/users/internal/admins")
    List<UserResponse> getAdmins();

   /* @GetMapping("/api/users/internal/{id}")
    UserResponse getUser( @PathVariable UUID id );*/

    @PostMapping("/api/users/internal/batch")
    List<UserResponse> getUsersBatch( @RequestBody List<UUID> ids );
}

