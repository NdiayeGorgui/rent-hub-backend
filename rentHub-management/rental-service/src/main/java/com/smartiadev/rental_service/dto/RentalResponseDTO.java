package com.smartiadev.rental_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record RentalResponseDTO(
        Long id,
        Long itemId,
        UUID ownerId,
        UUID renterId,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        Double totalPrice,
        LocalDateTime createdAt
) {}
