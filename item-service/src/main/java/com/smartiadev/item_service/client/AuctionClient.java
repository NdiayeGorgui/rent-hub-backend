package com.smartiadev.item_service.client;

import com.smartiadev.item_service.dto.AuctionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "auction-service")
public interface AuctionClient {

    @GetMapping("/api/auctions/by-item/{itemId}")
    AuctionDto getAuctionByItemId(@PathVariable Long itemId);

    @PostMapping("/api/auctions/internal/by-items")
    List<AuctionDto> getAuctionsByItemIds(
            @RequestBody List<Long> itemIds
    );
}