package com.smartiadev.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "auction-service", contextId = "auctionPublicClient")
public interface AuctionPublicClient {

    @GetMapping("/api/auctions/stats/public/open")
    long countOpenAuctions();
}
