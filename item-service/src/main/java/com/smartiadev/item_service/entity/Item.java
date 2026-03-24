package com.smartiadev.item_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "items", schema = "item_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Propriétaire de l'article (User ID venant de auth-service)
     */
    @Column(nullable = false)
    private UUID ownerId;


    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * Catégorie (base-domain-service)
     */
    @Column(nullable = false)
    private Long categoryId;

    /**
     * Prix par jour
     */
    @Column(nullable = true)
    private Double pricePerDay;

    /**
     * Localisation
     */
    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 255)
    private String address;

    /**
     * Images (URLs S3)
     */
    @ElementCollection
    @CollectionTable(
            name = "item_images",
            joinColumns = @JoinColumn(name = "item_id")
    )
    @Column(name = "image_url", nullable = false)
    private List<String> imageUrls;

    /**
     * Visibilité
     */
    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    /**
     * Audit
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 🔴 CHANGEMENT IMPORTANT ICI
    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type = ItemType.RENTAL;

    /* =====================
       Lifecycle callbacks
       ===================== */

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
