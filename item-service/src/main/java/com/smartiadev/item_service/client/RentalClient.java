package com.smartiadev.item_service.client;

import com.smartiadev.item_service.dto.RentalStatsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@FeignClient(
        name = "rental-service"
)
public interface RentalClient {

    @GetMapping("/api/rentals/internal/unavailable")
    List<Long> getUnavailableItems(
            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    );

    @PostMapping("/api/rentals/stats/items")
    Map<Long, RentalStatsResponse> getStatsByItems(
            @RequestBody List<Long> itemIds
    );
}
