package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.AuctionBidPlacedEvent;
import com.smartiadev.base_domain_service.dto.AuctionClosedEvent;
import com.smartiadev.base_domain_service.dto.AuctionFeeRefundedEvent;
import com.smartiadev.notif_service.client.UserClient;
import com.smartiadev.notif_service.client.WatcherClient;
import com.smartiadev.notif_service.dto.UserResponse;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuctionNotificationConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;
    private final WatcherClient watcherClient;
    private final UserClient userClient;

    /* =========================================
       NEW BID NOTIFICATION
       ========================================= */
    @KafkaListener(
            topics = "auction.bid.placed",
            groupId = "notification-group"
    )
    public void onNewBid(AuctionBidPlacedEvent event) {

        List<UUID> watchers = watcherClient.getWatchers(event.auctionId());

        for (UUID watcherId : watchers) {

            // ne pas notifier celui qui a fait l'enchère
            if (watcherId.equals(event.bidderId())) {
                continue;
            }

            Notification notification = repository.save(
                    new Notification(
                            null,
                            watcherId,
                            "🔥 Nouvelle enchère : " + event.amount() + " $",
                            "NEW_BID",
                            false,
                            LocalDateTime.now()
                    )
            );

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + watcherId,
                    notification
            );
        }
    }

    /* =========================================
       AUCTION CLOSED NOTIFICATION
       ========================================= */
    @KafkaListener(
            topics = "auction.closed",
            groupId = "notification-group"
    )
    public void onAuctionClosed(AuctionClosedEvent event) {


        Notification notification;

// 🏆 CAS GAGNANT
        if (event.winnerId() != null) {

            // 👉 récupérer les users
            UserResponse winner = userClient.getUserById(event.winnerId());
            UserResponse owner = userClient.getUserById(event.ownerId());

            // 👉 Notification pour le WINNER
            Notification winnerNotification = repository.save(
                    new Notification(
                            null,
                            event.winnerId(),
                            "🎉 Félicitations " + winner.username() + " ! Vous avez remporté l’enchère avec "
                                    + event.winningAmount() + " $",
                            "AUCTION_WON",
                            false,
                            LocalDateTime.now()
                    )
            );

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + event.winnerId(),
                    winnerNotification
            );

            // 👉 Notification pour le OWNER
            Notification ownerNotification = repository.save(
                    new Notification(
                            null,
                            event.ownerId(),
                            "📢 Votre enchère a été remportée par "
                                    + winner.username() + " pour "
                                    + event.winningAmount() + " $",
                            "AUCTION_FINISHED",
                            false,
                            LocalDateTime.now()
                    )
            );

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + event.ownerId(),
                    ownerNotification
            );
        }

        // ❌ CAS RESERVE NON ATTEINT
        else if (!event.reserveMet()) {

            notification = repository.save(
                    new Notification(
                            null,
                            event.ownerId(),
                            "📭 L’enchère est terminée : le prix de réserve n’a pas été atteint.",
                            "AUCTION_RESERVE_NOT_MET",
                            false,
                            LocalDateTime.now()
                    )
            );

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + event.ownerId(),
                    notification
            );

        }
        // ❌ CAS AUCUNE ENCHÈRE
        else {

            notification = repository.save(
                    new Notification(
                            null,
                            event.ownerId(),
                            "📭 L’enchère est terminée sans aucune mise.",
                            "AUCTION_NO_BID",
                            false,
                            LocalDateTime.now()
                    )
            );

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + event.ownerId(),
                    notification
            );
        }


    }


}