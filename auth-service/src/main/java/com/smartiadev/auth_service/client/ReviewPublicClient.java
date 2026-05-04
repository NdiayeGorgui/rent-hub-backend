package com.smartiadev.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "review-service", contextId = "reviewPublicClient")
public interface ReviewPublicClient {

    @GetMapping("/api/reviews/stats/public/average")
    Double getPlatformAverageRating();
}
