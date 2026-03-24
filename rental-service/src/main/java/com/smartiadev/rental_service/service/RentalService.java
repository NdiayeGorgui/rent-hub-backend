package com.smartiadev.rental_service.service;

import com.smartiadev.rental_service.dto.*;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RentalService {

    RentalResponseDTO create(RentalRequestDTO dto, UUID renterId);

    List<RentalResponseDTO> myRentals(UUID renterId);

    List<RentalResponseDTO> rentalsForMyItems(UUID ownerId);

    void cancel(Long rentalId, UUID userId);
    void approve(Long rentalId, UUID ownerId);
    RentalResponseDTO getRentalById(Long id);
    Page<RentedItemDTO> myRentedItems(UUID renterId, int page, int size);
    List<Long> findUnavailableItemIds(LocalDate startDate, LocalDate endDate);
    List<ItemSummaryDto> getRentedItems(UUID userId);
    RentalStatsResponse getRentalStatsByItem(Long itemId);
}
