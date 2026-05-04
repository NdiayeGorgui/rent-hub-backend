package com.smartiadev.auth_service.controller;

import com.smartiadev.auth_service.dto.PublicStatsDto;
import com.smartiadev.auth_service.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatController {

    private final AdminStatsService statService;

    @GetMapping("/public")
    public ResponseEntity<PublicStatsDto> getPublicStats() {
        return ResponseEntity.ok(statService.getPublicStats());
    }
}