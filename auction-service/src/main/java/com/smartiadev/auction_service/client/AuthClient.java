package com.smartiadev.auction_service.client;

import com.smartiadev.auction_service.dto.AuctionStrikeResponse;
import com.smartiadev.auction_service.dto.UserBidEligibilityResponse;
import com.smartiadev.auction_service.dto.UserProfileInternalDto;
import org.hibernate.sql.results.graph.FetchList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/api/users/internal/{id}/can-bid")
    UserBidEligibilityResponse canBid(@PathVariable UUID id);

    @PostMapping("/api/users/internal/{id}/auction-strike")
    AuctionStrikeResponse addStrike(@PathVariable UUID id);

    @PostMapping("/api/users/internal/user/batch")
    List<UserProfileInternalDto> getUsersBatch(
            @RequestBody List<UUID> ids
    );
}