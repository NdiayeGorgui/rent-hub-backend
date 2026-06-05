package com.smartiadev.auth_service.dto;

public record RentalStatsDto(
        Long totalRentals,
        Long activeRentals,
        Double totalRevenue,
        Long pendingRentals,  // CREATED + APPROVED
        Long completedRentals // ENDED
) {}