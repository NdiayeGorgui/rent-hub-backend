package com.smartiadev.auction_service.client;

import com.smartiadev.base_domain_service.dto.AuctionWinnerNotification;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/api/notifications/auction-winner")
    void notifyWinner(AuctionWinnerNotification notification);
}
