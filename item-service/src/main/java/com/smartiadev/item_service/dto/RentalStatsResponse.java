package com.smartiadev.item_service.dto;


import lombok.Builder;

@Builder
public record RentalStatsResponse(
        Long rentalsCount,
        Double totalRevenue,
        Long totalDaysRented
) {}
