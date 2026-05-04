package com.smartiadev.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "item-service",contextId = "itemPublicClient")
public interface ItemPublicClient {

    @GetMapping("/api/items/stats/public/count/published")
    Long countPublishedItems();

}
