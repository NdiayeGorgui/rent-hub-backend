package com.smartiadev.base_domain_service.dto;

import java.util.UUID;

public record AuctionFeeRefundedEvent(
        Long itemId,
        UUID ownerId,
        Double amount
) {}