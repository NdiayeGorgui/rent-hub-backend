package com.smartiadev.item_service.dto;

import com.smartiadev.item_service.entity.ItemStatus;
import com.smartiadev.item_service.entity.ItemType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ItemResponseDTO {

    private Long id;
    private UUID ownerId;
    private String title;
    private String description;
    private Long categoryId;
    private ItemType type;
    private Double pricePerDay;
    private String city;
    private String address;
    private List<String> imageUrls;
    private Boolean active;
    private ItemStatus status;
    private LocalDateTime createdAt;
}
