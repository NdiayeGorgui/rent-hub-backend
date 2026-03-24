package com.smartiadev.dispute_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "item-service")
public interface ItemClient {

    @PutMapping("/api/admin/items/{id}/deactivate")
    void deactivate(@PathVariable Long id);
}

