package com.smartiadev.review_service.dto;

public record CreateReviewRequest(
        Long rentalId,
        Integer rating,
        String comment
) {}

