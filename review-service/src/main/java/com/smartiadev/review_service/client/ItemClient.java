package com.smartiadev.review_service.client;

import com.smartiadev.review_service.dto.ItemInternalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "item-service")
public interface ItemClient {

    @GetMapping("/api/items/internal/{itemId}")
    ItemInternalDTO getItemById(@PathVariable Long itemId);
}
