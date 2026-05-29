package com.smartiadev.review_service.client;

import com.smartiadev.review_service.dto.ItemInternalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "item-service")
public interface ItemClient {

    @GetMapping("/api/items/internal/{itemId}")
    ItemInternalDTO getItemById(@PathVariable Long itemId);

    @PostMapping("/api/items/internal/items/batch")
    List<ItemInternalDTO> getItemsBatch(
            @RequestBody List<Long> ids
    );
}
