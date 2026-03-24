package com.smartiadev.rental_service.dto;

import java.time.LocalDate;

public record RentedItemDTO(
        Long rentalId,
        Long itemId,
        LocalDate startDate,
        LocalDate endDate,
        String status
) {}
