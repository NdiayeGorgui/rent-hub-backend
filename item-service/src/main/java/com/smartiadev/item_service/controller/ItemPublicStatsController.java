package com.smartiadev.item_service.controller;

import com.smartiadev.item_service.service.ItemStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items/stats/public")
@RequiredArgsConstructor
public class ItemPublicStatsController {

    private final ItemStatsService itemStatsService;

    @GetMapping("/count/published")
    public Long countPublishedItems() {
        return itemStatsService.countPublishedItems();
    }
}
