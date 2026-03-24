package com.smartiadev.item_service.client;

import com.smartiadev.item_service.dto.AuctionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auction-service")
public interface AuctionClient {

    @GetMapping("/api/auctions/by-item/{itemId}")
    AuctionDto getAuctionByItemId(@PathVariable Long itemId);
}