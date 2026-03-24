package com.smartiadev.rental_service.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RentalRequestDTO(
        @NotNull Long itemId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {}
