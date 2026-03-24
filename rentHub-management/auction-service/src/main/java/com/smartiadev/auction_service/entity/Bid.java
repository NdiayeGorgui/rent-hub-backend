package com.smartiadev.auction_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bids", schema = "auction_schema")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long auctionId;
    private UUID bidderId;

    private Double amount;
    private LocalDateTime createdAt;
}
