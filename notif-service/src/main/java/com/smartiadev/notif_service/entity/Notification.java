package com.smartiadev.notif_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications", schema = "notification_schema")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;          // locataire ou propriétaire
    private String message;       // texte lisible
    private String type;          // APPROVED / CANCELLED / STARTED
    private boolean read = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}

