package com.smartiadev.rental_service.dto;


import lombok.Builder;

@Builder
public record RentalStatsResponse(
        Long rentalsCount,
        Double totalRevenue,
        Long totalDaysRented
) {}
