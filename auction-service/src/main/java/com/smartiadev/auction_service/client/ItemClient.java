package com.smartiadev.auction_service.client;

import com.smartiadev.auction_service.dto.ItemInternalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "item-service")
public interface ItemClient {

    @GetMapping("/api/items/internal/{id}")
    ItemInternalDTO getItem(@PathVariable Long id);

    @PutMapping("/api/items/internal/{id}/activate")
    void activateItem(
            @PathVariable Long id,
            @RequestHeader("X-Internal-Call") String internal
    );

    @PostMapping("/api/items/internal/items/batch")
    List<ItemInternalDTO> getItemsBatch(@RequestBody List<Long> ids);
}
