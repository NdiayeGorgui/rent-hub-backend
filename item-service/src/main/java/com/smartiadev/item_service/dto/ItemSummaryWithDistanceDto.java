package com.smartiadev.item_service.dto;

import com.smartiadev.item_service.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemSummaryWithDistanceDto {
    private Long id;
    private String title;
    private String description;
    private Double pricePerDay;
    private String type;
    private String city;
    private List<String> imageUrls;
    private Double distanceKm;
    private String distanceLabel; // "environ 1 km", "moins de 500 m"

    public ItemSummaryWithDistanceDto(Item item, double distanceKm) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.pricePerDay = item.getPricePerDay();
        this.type = item.getType().name();
        this.city = item.getCity();
        this.imageUrls = item.getImageUrls();
        this.distanceKm = distanceKm;
        this.distanceLabel = formatDistance(distanceKm);
    }

    private String formatDistance(double km) {
        if (km < 0.5) return "moins de 500 m";
        if (km < 1) return "environ 1 km";
        if (km < 2) return "environ 1,5 km";
        return "environ " + (int) Math.round(km) + " km";
    }
}
