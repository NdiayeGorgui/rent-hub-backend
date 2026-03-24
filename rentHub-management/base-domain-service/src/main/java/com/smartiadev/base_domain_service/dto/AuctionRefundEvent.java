package com.smartiadev.base_domain_service.dto;

import java.util.UUID;

public record AuctionRefundEvent(

        UUID ownerId,
        double amount,
        Long auctionId

) {}
