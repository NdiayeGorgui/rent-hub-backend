package com.smartiadev.review_service.dto;


public record UserReviewStatsDto(

        Double averageRating,
        Long reviewsCount
) {}
