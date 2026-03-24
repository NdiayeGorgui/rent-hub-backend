package com.smartiadev.auth_service.dto;


public record DisputeStats(
        Long totalDisputes,
        Long openDisputes,
        Long inReviewDisputes,
        Long resolvedDisputes,
        Long rejectedDisputes,
        Long disputesLast30Days,
        Double avgResolutionTimeMinutes,
        long withAdminDecision
) {}
