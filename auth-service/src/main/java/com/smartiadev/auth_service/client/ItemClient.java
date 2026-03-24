package com.smartiadev.auth_service.client;

import com.smartiadev.auth_service.dto.ItemSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "item-service")
public interface ItemClient {

    @GetMapping("/api/items/user/{userId}/published")
    List<ItemSummaryDto> getItemsPublishedByUser(
            @PathVariable UUID userId
    );

    @GetMapping("/api/admin/items/stats/count")
    Long countAllItems();

    @GetMapping("/api/admin/items/stats/count/published")
    Long countPublishedItems();
}
