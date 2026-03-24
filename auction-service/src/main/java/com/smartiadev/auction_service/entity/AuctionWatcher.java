package com.smartiadev.auction_service.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "auction_watchers",schema = "auction_schema",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"auction_id", "user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionWatcher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long auctionId;

    private UUID userId;
}
