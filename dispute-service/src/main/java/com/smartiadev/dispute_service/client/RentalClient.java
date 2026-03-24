package com.smartiadev.dispute_service.client;

import com.smartiadev.dispute_service.config.FeignClientConfig;
import com.smartiadev.dispute_service.dto.RentalInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "rental-service",
        url = "http://localhost:8080"   // via API Gateway
)
public interface RentalClient {

    @GetMapping("/api/rentals/internal/{id}")
    RentalInfoDTO getRental(@PathVariable("id") Long id);
}


