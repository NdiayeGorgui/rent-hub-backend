package com.smartiadev.item_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemSummaryDto {

    private Long id;
    private String title;
    private Double pricePerDay;
    private Double averageRating;
    private LocalDateTime createdAt; // pour items publiés
    private LocalDate startDate;     // pour rentals
    private LocalDate endDate;
}
