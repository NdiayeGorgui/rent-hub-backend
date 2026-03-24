package com.smartiadev.review_service.client;

import com.smartiadev.review_service.dto.RentalInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "rental-service")
public interface RentalClient {

    @GetMapping("/api/rentals/internal/{id}")
    RentalInfoDTO getRental(@PathVariable("id") Long id);
}


