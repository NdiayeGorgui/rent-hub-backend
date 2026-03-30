package com.smartiadev.item_service.service;

import com.smartiadev.item_service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ItemService {

    ItemResponseDTO create(ItemRequestDTO dto, UUID ownerId);

    List<ItemResponseDTO> findAllActive();

    ItemResponseDTO findById(Long id) ;

    List<ItemResponseDTO> findByOwner(UUID ownerId);

    void deactivate(Long itemId, UUID ownerId);
    void deactivateFromRental(Long itemId, UUID ownerId);

    List<ItemResponseDTO> findAllIncludingInactive();
    void adminDeactivate(Long id);
    void adminActivate(Long id);
    void activate(Long itemId, UUID ownerId);
    Page<ItemResponseDTO> myPublishedItems(UUID ownerId, int page, int size);
    void activateBySystem(Long itemId);
    void updateItem(Long itemId, UUID userId, UpdateItemRequest dto);
    Page<ItemSearchResponseDto> searchItems(
            String keyword,
            String city,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            LocalDate startDate,
            LocalDate endDate,
            Double minRating,
            String  type,
            Pageable pageable
    );

    List<ItemSummaryDto> getPublishedItemsByUser(UUID userId);
    ItemDetailsDto getItemDetails(Long itemId);

    ItemResponseDTO createWithImages(
            ItemRequestDTO dto,
            List<MultipartFile> images,
            UUID ownerId
    );
    List<AdminItemDto> findAllAdminItems();

    void updateItemWithImages(Long id, UUID userId, UpdateItemRequest dto, List<MultipartFile> files,  String existingImages);

    List<ItemSummaryWithDistanceDto> getNearbyItems(double lat, double lng, double radiusKm);
}
