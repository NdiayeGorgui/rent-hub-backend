package com.smartiadev.auction_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/payments/refund/auction")
    void refundAuctionFee(
            @RequestParam Long auctionId,
            @RequestParam UUID ownerId
    );
}
