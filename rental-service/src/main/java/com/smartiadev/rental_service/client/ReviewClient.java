package com.smartiadev.rental_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "review-service")
public interface ReviewClient {

    @GetMapping("/api/reviews/item/{itemId}/average")
    Double getAverageRatingForItem(@PathVariable("itemId") Long itemId);
}

