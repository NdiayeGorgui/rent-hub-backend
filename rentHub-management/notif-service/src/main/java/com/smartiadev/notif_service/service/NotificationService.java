package com.smartiadev.notif_service.service;

import com.smartiadev.base_domain_service.dto.AuctionWinnerNotification;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    /* =====================
       GET USER NOTIFICATIONS
       ===================== */
    public List<Notification> getUserNotifications(UUID userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /* =====================
       MARK ONE AS READ
       ===================== */
    public void markAsRead(Long id, UUID userId) {

        Notification notification = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Notification not found"));

        if (!notification.getUserId().equals(userId)) {
            throw new IllegalStateException("Forbidden");
        }

        notification.setRead(true);

        repository.save(notification);
    }

    /* =====================
       MARK ALL AS READ
       ===================== */
    public void markAllAsRead(UUID userId) {
        repository.markAllAsRead(userId);
    }

    /* =====================
       CREATE WINNER NOTIFICATION
       ===================== */
    public void createAuctionWinnerNotification(AuctionWinnerNotification dto) {

        Notification notification = new Notification();

        notification.setUserId(dto.winnerId());
        notification.setMessage(
                "🎉 Félicitations ! Vous avez remporté l’enchère avec "
                        + dto.winningAmount() + " $"
        );
        notification.setType("AUCTION_WON");
        notification.setRead(false);

        repository.save(notification);
    }
}