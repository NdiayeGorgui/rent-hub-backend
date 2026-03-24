package com.smartiadev.item_service.dto;

import java.util.List;

public record UpdateItemRequest(
        String title,
        Long categoryId,
        String description,
        String city,
        String address,
        Double pricePerDay,
        String type,
        List<String> imageUrls

) {}