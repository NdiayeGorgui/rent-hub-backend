package com.smartiadev.item_service.dto;

import com.smartiadev.item_service.entity.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class ItemRequestDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private Long categoryId;
    @NotNull
    private ItemType type;

    @Positive
    private Double pricePerDay;

    @NotBlank
    private String city;

    private String address;

   // @NotNull
   // private List<String> imageUrls;
}
