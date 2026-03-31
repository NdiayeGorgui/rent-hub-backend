package com.smartiadev.dispute_service.client;

import com.smartiadev.dispute_service.dto.AuctionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auction-service")
public interface AuctionClient {

    @GetMapping("/api/auctions/{id}")
    AuctionResponse getAuction(@PathVariable Long id);
}
