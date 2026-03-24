package com.smartiadev.dispute_service.dto;

import com.smartiadev.dispute_service.entity.DisputeStatus;

public record ResolveDisputeRequest(
        String decision,
        String adminDecision,
        String action ,// NONE, DEACTIVATE_ITEM, SUSPEND_USER
        DisputeStatus disputeStatus
) {}

