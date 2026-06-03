package com.smartiadev.auth_service.client;

import com.smartiadev.auth_service.config.FeignAuthConfig;
import com.smartiadev.auth_service.dto.AuctionStats;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "auction-service", contextId = "auctionAdminClient", configuration = FeignAuthConfig.class)
public interface AuctionClient {

    @GetMapping("/api/auctions/internal/stats")
    AuctionStats getAuctionStats();
}
