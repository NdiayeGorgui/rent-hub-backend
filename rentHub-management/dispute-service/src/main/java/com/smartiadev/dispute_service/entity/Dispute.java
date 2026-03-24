package com.smartiadev.dispute_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "disputes", schema = "dispute_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rentalId;
    private Long itemId;

    private UUID openedBy;
    private UUID reportedUserId;

    private String reason;
    private String description;

    @Enumerated(EnumType.STRING)
    private DisputeStatus status;

    private String adminDecision;

    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}

