package com.smartiadev.rental_service.dto;

public record RentalStatsDto(
        Long totalRentals,
        Long activeRentals,
        Double totalRevenue
) {}