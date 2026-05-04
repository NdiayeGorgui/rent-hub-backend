package com.smartiadev.auction_service.controller;

import com.smartiadev.auction_service.service.AuctionStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auctions/stats/public")
@RequiredArgsConstructor
public class AuctionPublicStatsController {

    private final AuctionStatsService statsService;

    @GetMapping("/open")
    public long countOpenAuctions() {
        return statsService.getStats().openAuctions();
    }
}
