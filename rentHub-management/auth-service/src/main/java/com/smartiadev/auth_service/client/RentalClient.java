package com.smartiadev.auth_service.client;

import com.smartiadev.auth_service.dto.ItemSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "rental-service")
public interface RentalClient {

    @GetMapping("/api/rentals/user/{userId}/history")
    List<ItemSummaryDto> getRentalHistory(
            @PathVariable UUID userId
    );

    @GetMapping("/api/admin/rentals/stats/count")
    Long countAllRentals();

    @GetMapping("/api/admin/rentals/stats/count/active")
    Long countActiveRentals();

    @GetMapping("/api/admin/rentals/stats/revenue")
    Double getTotalRevenue();
}
