package com.smartiadev.dispute_service.client;

import com.smartiadev.dispute_service.dto.ItemInternalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "item-service")
public interface ItemClient {

    @PutMapping("/api/admin/items/{id}/deactivate")
    void deactivate(@PathVariable Long id);

    /*@GetMapping("/api/items/internal/{id}")
    ItemInternalDTO getItem(@PathVariable Long id );*/

    @PostMapping("/api/items/internal/batch")
    List<ItemInternalDTO> getItemsBatch(@RequestBody List<Long> ids );
}

