package com.smartiadev.payments_service.dto;

import java.util.UUID;

public record CancelRequest(
        Long auctionId,
        Long itemId,
        UUID userId,
        Double amount
) {}
