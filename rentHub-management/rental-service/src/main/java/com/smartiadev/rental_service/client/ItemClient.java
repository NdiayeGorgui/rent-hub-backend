package com.smartiadev.rental_service.client;

import com.smartiadev.rental_service.dto.ItemInternalDTO;
import com.smartiadev.rental_service.dto.RentalResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "item-service")
public interface ItemClient {

    @GetMapping("/api/items/internal/{id}")
    ItemInternalDTO getItem(@PathVariable("id") Long itemId);

    @PutMapping("/api/items/internal/{id}/set-active")
    void setItemActive(@PathVariable("id") Long id, @RequestParam Boolean active);
}

