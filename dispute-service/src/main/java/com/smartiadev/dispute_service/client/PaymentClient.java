package com.smartiadev.dispute_service.client;

import com.smartiadev.dispute_service.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "payments-service")
public interface PaymentClient {

    @GetMapping("/api/payments/admin/auction-fee")
    PaymentResponse getAuctionFeeByItemId(@RequestParam Long itemId);

    @PostMapping("/api/payments/admin/refund-auction-fee")
    void refundAuctionFee(
            @RequestParam String paymentIntentId,
            @RequestParam UUID winnerId,
            @RequestParam Long auctionId
    );
}
