package com.smartiadev.base_domain_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalEndedEvent {

    private Long rentalId;
    private Long itemId;
    private UUID ownerId;
    private UUID renterId;
}
