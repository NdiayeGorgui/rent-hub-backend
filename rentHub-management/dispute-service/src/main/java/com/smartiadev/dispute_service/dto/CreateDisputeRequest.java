package com.smartiadev.dispute_service.dto;

public record CreateDisputeRequest(
        Long rentalId,
        String reason,
        String description
) {}

