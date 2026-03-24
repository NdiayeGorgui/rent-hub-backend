package com.smartiadev.notif_service.controller;

import com.smartiadev.base_domain_service.dto.AuctionWinnerNotification;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /* =====================
       GET MY NOTIFICATIONS
       ===================== */
    @GetMapping("/me")
    public List<Notification> myNotifications(
            @AuthenticationPrincipal Jwt jwt
    ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        return notificationService.getUserNotifications(userId);
    }

    /* =====================
       MARK ONE AS READ
       ===================== */
    @PutMapping("/{id}/read")
    public void markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        notificationService.markAsRead(id, userId);
    }

    /* =====================
       MARK ALL AS READ
       ===================== */
    @PutMapping("/me/read-all")
    public void markAllAsRead(
            @AuthenticationPrincipal Jwt jwt
    ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        notificationService.markAllAsRead(userId);
    }

    /* =====================
       INTERNAL EVENT
       ===================== */
    @PostMapping("/auction-winner")
    public void notifyWinner(
            @RequestBody AuctionWinnerNotification dto
    ) {
        notificationService.createAuctionWinnerNotification(dto);
    }
}