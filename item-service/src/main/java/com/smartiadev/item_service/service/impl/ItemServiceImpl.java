package com.smartiadev.item_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartiadev.item_service.client.*;
import com.smartiadev.item_service.dto.*;
import com.smartiadev.item_service.entity.Item;
import com.smartiadev.item_service.entity.ItemStatus;
import com.smartiadev.item_service.entity.ItemType;
import com.smartiadev.item_service.repository.ItemRepository;
import com.smartiadev.item_service.repository.specification.ItemSpecifications;
import com.smartiadev.item_service.service.GeocodingService;
import com.smartiadev.item_service.service.ImageStorageService;
import com.smartiadev.item_service.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final ReviewClient reviewClient;
    private final RentalClient rentalClient;
    private final AuthClient authClient;
    private final SubscriptionClient subscriptionClient;
    private final AuctionClient  auctionClient;
    private final ImageStorageService imageStorageService;
    private static final String ITEM_SERVICE_BASE_URL = "http://localhost:8080"; // gateway
    private final ObjectMapper objectMapper;
    private final GeocodingService geocodingService;
    /* =====================
       CREATE ITEM
       ===================== */
    @Override
    public ItemResponseDTO create(ItemRequestDTO dto, UUID ownerId) {

    /* =========================
       🔥 VALIDATION TYPE / PRIX
       ========================= */
        validateItem(dto);

        // Vérifie le statut premium pour les auctions
        PremiumStatusResponse status = subscriptionClient.getPremiumStatus(ownerId);

        if (dto.getType() == ItemType.AUCTION && !status.premium()) {
            throw new IllegalStateException("Premium required for auction");
        }

    /* =========================
       CONSTRUCTION ENTITY
       ========================= */
        boolean active;
        ItemStatus itemStatus;

        // Définir le statut et l'activation en fonction du type
        if (dto.getType() == ItemType.AUCTION) {
            itemStatus = ItemStatus.PENDING_AUCTION_FEE; // attente paiement
            active = false;
        } else {
            itemStatus = ItemStatus.ACTIVE; // actif immédiatement
            active = true;
        }

        Item item = Item.builder()
                .ownerId(ownerId)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .categoryId(dto.getCategoryId())
                .type(dto.getType())
                .pricePerDay(dto.getPricePerDay())
                .city(dto.getCity())
                .address(dto.getAddress())
                .status(itemStatus)
                .active(active)
                .build();

    /* =========================
       ENREGISTREMENT
       ========================= */
        Item saved = repository.save(item);

        return map(saved);
    }

    /* =====================
       FIND ALL ACTIVE
       ===================== */
    @Override
    public List<ItemResponseDTO> findAllActive() {
        return repository.findByStatus(ItemStatus.ACTIVE)
                .stream()
                .map(this::map)
                .toList();
    }

    /* =====================
       FIND BY ID
       ===================== */
    @Override
    public ItemResponseDTO findById(Long id) {
        Item item = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item not found with id " + id)
                );

        return map(item);
    }

    /* =====================
       FIND BY OWNER
       ===================== */
    @Override
    public List<ItemResponseDTO> findByOwner(UUID ownerId) {

        return repository
                .findByOwnerIdAndStatusNot(ownerId, ItemStatus.BLOCKED)
                .stream()
                .map(this::map)
                .toList();
    }

    /* =====================
       DEACTIVATE ITEM
       ===================== */
    @Override
    public void deactivate(Long itemId, UUID ownerId) {

        Item item = repository.findById(itemId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item not found")
                );

        if (!item.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Forbidden: not item owner");
        }

        item.setActive(false);
        item.setStatus(ItemStatus.INACTIVE);
        repository.save(item);
    }

    @Override
    public void deactivateFromRental(Long itemId, UUID ownerId) {

        Item item = repository.findById(itemId)
                .orElseThrow();

        if (!item.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Forbidden");
        }

        item.setActive(false);
        item.setStatus(ItemStatus.INACTIVE);
        repository.save(item);
    }


    /* =====================
       MAPPING
       ===================== */
    private ItemResponseDTO map(Item item) {
        return new ItemResponseDTO(
                item.getId(),
                item.getOwnerId(),
                item.getTitle(),
                item.getDescription(),
                item.getCategoryId(),
                item.getType(),
                item.getPricePerDay(),
                item.getCity(),
                item.getLatitude(),
                item.getLongitude(),
                item.getAddress(),
                item.getImageUrls(),
                item.getActive(),
                item.getStatus(),
                item.getCreatedAt()
        );
    }
   /* public ItemDetailsDto getItemDetails(Long itemId) {

        Double avgRating = reviewClient.getAverageRatingForItem(itemId);

        return ItemDetailsDto.builder()
                .itemId(itemId)
                .averageRating(avgRating != null ? avgRating : 0.0)
                .build();
    }*/

    @Override
    public List<ItemResponseDTO> findAllIncludingInactive() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public void adminDeactivate(Long id) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setStatus(ItemStatus.BLOCKED);
        item.setActive(false);
        repository.save(item);
    }
    @Override
    public void activate(Long itemId, UUID ownerId) {

        Item item = repository.findById(itemId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item not found")
                );

        if (!item.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Forbidden: not item owner");
        }

        // 🔴 bloqué par admin
        if (item.getStatus() == ItemStatus.BLOCKED) {
            throw new RuntimeException("Item blocked by admin");
        }

        // 🔴 paiement non fait
        if (item.getStatus() == ItemStatus.PENDING_AUCTION_FEE) {
            throw new RuntimeException(
                    "Auction fee must be paid before activating this item"
            );
        }

        // 🔴 🔥 IMPORTANT : uniquement depuis DRAFT
        if (item.getStatus() != ItemStatus.DRAFT) {
            throw new RuntimeException(
                    "Item must be in DRAFT status to be activated"
            );
        }

        item.setStatus(ItemStatus.ACTIVE);
        item.setActive(true);

        repository.save(item);
    }

    @Override
    public void adminActivate(Long id) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setStatus(ItemStatus.ACTIVE);
        item.setActive(true);
        repository.save(item);
    }
    @Override
    public void activateBySystem(Long itemId) {

        Item item = repository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (item.getStatus() == ItemStatus.BLOCKED) {
            throw new RuntimeException("Item blocked by admin");
        }

        // 🔥 ici on ne check PAS owner ni JWT

        item.setStatus(ItemStatus.ACTIVE);
        item.setActive(true);

        repository.save(item);
    }
    @Override
    public void updateItem(Long itemId, UUID userId, UpdateItemRequest dto) {

        Item item = repository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getOwnerId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }

        if (item.getStatus() == ItemStatus.BLOCKED) {
            throw new RuntimeException("Blocked item cannot be edited");
        }

        // 🔹 SAFE UPDATE : ne mettre à jour QUE si non null
        if (dto.title() != null) item.setTitle(dto.title());
        if (dto.description() != null) item.setDescription(dto.description());
        if (dto.categoryId() != null) item.setCategoryId(dto.categoryId());
        if (dto.city() != null) item.setCity(dto.city());
        if (dto.address() != null) item.setAddress(dto.address());
        if (dto.pricePerDay() != null) item.setPricePerDay(dto.pricePerDay());

        // 🔹 TYPE SAFE : uniquement si fourni et premium si AUCTION
        if (dto.type() != null) {
            if (dto.type().equalsIgnoreCase("AUCTION") && !subscriptionClient.getPremiumStatus(userId).premium()) {
                throw new IllegalStateException("Premium required for auction");
            }
            item.setType(ItemType.valueOf(dto.type()));
        }

        // 🔹 IMAGES SAFE : ne pas écraser si null ou vide
        if (dto.imageUrls() != null && !dto.imageUrls().isEmpty()) {
            item.setImageUrls(dto.imageUrls());
        }

        repository.save(item);
    }

    @Override
    public Page<ItemResponseDTO> myPublishedItems(UUID ownerId, int page, int size) {

        Page<Item> items = repository.findByOwnerId(
                ownerId,
                PageRequest.of(
                        page,
                        size,
                        Sort.by("createdAt").descending()
                )
        );

        return items.map(this::map);
    }

    @Override
    public Page<ItemSearchResponseDto> searchItems(
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
    ) {

    /* =========================
       1️⃣ ITEMS INDISPONIBLES
       ========================= */
        List<Long> unavailableItemIds = null;

        if (startDate != null && endDate != null) {
            unavailableItemIds =
                    rentalClient.getUnavailableItems(startDate, endDate);
        }

    /* =========================
       2️⃣ FILTRE NOTE MINIMALE
       ========================= */
        List<Long> ratedItemIds = null;

        if (minRating != null) {

            ratedItemIds =
                    reviewClient.getItemIdsWithMinRating(minRating);

            // aucun item correspond
            if (ratedItemIds == null || ratedItemIds.isEmpty()) {
                return Page.empty(pageable);
            }
        }

    /* =========================
       3️⃣ CONSTRUCTION SPECIFICATION
       ========================= */
        Specification<Item> spec =
                Specification.where(
                                ItemSpecifications.hasStatus(ItemStatus.ACTIVE)
                        )
                        .and(ItemSpecifications.searchKeyword(keyword))
                        .and(ItemSpecifications.hasCity(city))
                        .and(ItemSpecifications.hasCategory(categoryId))
                        .and(ItemSpecifications.priceBetween(minPrice, maxPrice))
                        .and(ItemSpecifications.hasType(type))
                        .and(ItemSpecifications.excludeIds(unavailableItemIds))
                        .and(ItemSpecifications.includeIds(ratedItemIds));

    /* =========================
       4️⃣ REQUÊTE DB
       ========================= */
        Page<Item> itemsPage =
                repository.findAll(spec, pageable);

    /* =========================
       5️⃣ RÉCUPÉRATION RATINGS
       ========================= */
        Map<Long, Double> ratings =
                reviewClient.getItemsRatings();

    /* =========================
       6️⃣ MAPPING DTO
       ========================= */
        return itemsPage.map(item -> {
            String imageUrl = null;
            if (item.getImageUrls() != null && !item.getImageUrls().isEmpty()) {
                // Ici on prend la première image et on préfixe avec l'URL du gateway
                imageUrl = ITEM_SERVICE_BASE_URL + item.getImageUrls().get(0);
            }

            return new ItemSearchResponseDto(
                    item.getId(),
                    item.getTitle(),
                    item.getDescription(),
                    item.getCity(),
                    item.getPricePerDay(),
                    ratings.getOrDefault(item.getId(), 0.0),
                    item.getType().name(),
                    imageUrl
            );
        });
    }

    @Override
    public List<ItemSummaryDto> getPublishedItemsByUser(UUID userId) {

        Map<Long, Double> ratings = reviewClient.getItemsRatings();

        return repository.findByOwnerId(userId).stream()
                .map(item -> {
                    // Si l'item est RENTAL, garder pricePerDay, sinon mettre null ou 0
                    Double price = item.getType() == ItemType.RENTAL
                            ? item.getPricePerDay()
                            : null; // ou 0.0 si tu préfères

                    return new ItemSummaryDto(
                            item.getId(),
                            item.getTitle(),
                            price,
                            ratings.getOrDefault(item.getId(), 0.0),
                            item.getCreatedAt(),
                            null,
                            null
                    );
                })
                .toList();
    }

    @Override
    public ItemDetailsDto getItemDetails(Long itemId) {

        // 1️⃣ Item (DB locale)
        Item item = repository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item not found"));

        // 2️⃣ Rating de l’item
        Double itemRating = reviewClient.getAverageRatingForItem(itemId);
        Double safeItemRating = itemRating != null ? itemRating : 0.0;

        // 3️⃣ Publisher (ownerId)
        UserProfileInternalDto user = authClient.getUserProfile(item.getOwnerId());
        PublisherDto publisher = PublisherDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .city(user.getCity())
                .averageRating(user.getAverageRating() != null ? user.getAverageRating() : 0.0)
                .reviewsCount(user.getReviewsCount() != null ? user.getReviewsCount() : 0L)
                .badge(user.getBadge())
                .build();

        // 4️⃣ Composition finale
        ItemDetailsDto.ItemDetailsDtoBuilder builder = ItemDetailsDto.builder()
                .itemId(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .categoryId(item.getCategoryId())
                .city(item.getCity())
                .address(item.getAddress())
                .imageUrls(item.getImageUrls())
                .active(item.getActive())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .type(item.getType())
                .averageRating(safeItemRating)
                .publisher(publisher);

        // Ajouter pricePerDay uniquement si type RENTAL
        if (item.getType() == ItemType.RENTAL) {
            builder.pricePerDay(item.getPricePerDay());
        }

        return builder.build();
    }

    @Override
    public ItemResponseDTO createWithImages(
            ItemRequestDTO dto,
            List<MultipartFile> images,
            UUID ownerId
    ) {

        validateItem(dto);

        PremiumStatusResponse premiumStatus = subscriptionClient.getPremiumStatus(ownerId);

        if (dto.getType() == ItemType.AUCTION && !premiumStatus.premium()) {
            throw new IllegalStateException("Premium required for auction");
        }

        List<String> imageUrls = imageStorageService.uploadImages(images);

        ItemStatus itemStatus;
        boolean active;

        if (dto.getType() == ItemType.AUCTION) {
            // 🔴 enchère → paiement requis
            itemStatus = ItemStatus.PENDING_AUCTION_FEE;
            active = false;
        } else {
            // 🟢 location → actif immédiatement
            itemStatus = ItemStatus.ACTIVE;
            active = true;
        }
// Si le front n'envoie pas les coordonnées, on géocode automatiquement
        Double lat = dto.getLatitude();
        Double lng = dto.getLongitude();

        if (lat == null || lng == null) {
            double[] coords = geocodingService.getCoordinates(dto.getCity(), dto.getAddress());
            if (coords != null) {
                lat = coords[0];
                lng = coords[1];
            }
        }
        Item item = Item.builder()
                .ownerId(ownerId)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .categoryId(dto.getCategoryId())
                .type(dto.getType())
                .pricePerDay(dto.getPricePerDay())
                .city(dto.getCity())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .address(dto.getAddress())
                .imageUrls(imageUrls)
                .status(itemStatus)
                .active(active)
                .build();

        Item saved = repository.save(item);

        return map(saved);
    }
    private void validateItem(ItemRequestDTO dto) {

        if (dto.getType() == ItemType.RENTAL && dto.getPricePerDay() == null) {
            throw new IllegalArgumentException("pricePerDay is required for rental items");
        }

        if (dto.getType() == ItemType.AUCTION && dto.getPricePerDay() != null) {
            throw new IllegalArgumentException("pricePerDay must be null for auction items");
        }
    }

    @Override
    public List<AdminItemDto> findAllAdminItems() {

        List<Item> items = repository.findAll();

        return items.stream().map(item -> {

            // AUTH SERVICE
            UserProfileInternalDto user =
                    authClient.getUserProfile(item.getOwnerId());

            // SUBSCRIPTION SERVICE
            PremiumStatusResponse premium =
                    subscriptionClient.getPremiumStatus(item.getOwnerId());

            Double currentPrice = null;

            // AUCTION SERVICE
            if (item.getType() == ItemType.AUCTION) {

                try {

                    AuctionDto auction =
                            auctionClient.getAuctionByItemId(item.getId());

                    if (auction != null) {
                        currentPrice = auction.currentPrice();
                    }

                } catch (Exception ignored) {}

            }

            return AdminItemDto.builder()

                    .itemId(item.getId())
                    .title(item.getTitle())
                    .description(item.getDescription())

                    .city(item.getCity())
                    .address(item.getAddress())

                    .pricePerDay(item.getPricePerDay())
                    .active(item.getActive())

                    .type(item.getType().name())

                    .imageUrls(item.getImageUrls())

                    // publisher
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .publisherCity(user.getCity())

                    .averageRating(user.getAverageRating())
                    .reviewsCount(user.getReviewsCount())
                    .badge(user.getBadge())

                    // subscription
                    .premium(premium.premium())
                    .gracePeriod(premium.gracePeriod())

                    // auction
                    .currentPrice(currentPrice)

                    .build();

        }).toList();
    }

    @Override
    public void updateItemWithImages(Long itemId, UUID userId,
                                     UpdateItemRequest dto,
                                     List<MultipartFile> files,
                                     String existingImages
                                     ) {

        Item item = repository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // 🔒 sécurité
        if (!item.getOwnerId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }

        if (item.getStatus() == ItemStatus.BLOCKED) {
            throw new RuntimeException("Blocked item cannot be edited");
        }

        // 🔹 update classique
        if (dto.title() != null) item.setTitle(dto.title());
        if (dto.description() != null) item.setDescription(dto.description());
        if (dto.categoryId() != null) item.setCategoryId(dto.categoryId());
        if (dto.city() != null) item.setCity(dto.city());
        if (dto.address() != null) item.setAddress(dto.address());
        if (dto.latitude() != null) item.setLatitude(dto.latitude());
        if (dto.longitude() != null) item.setLongitude(dto.longitude());
        if (dto.pricePerDay() != null) item.setPricePerDay(dto.pricePerDay());

        if (dto.type() != null) {
            if (dto.type().equalsIgnoreCase("AUCTION")
                    && !subscriptionClient.getPremiumStatus(userId).premium()) {
                throw new IllegalStateException("Premium required for auction");
            }
            item.setType(ItemType.valueOf(dto.type()));
        }

        if (dto.latitude() != null) item.setLatitude(dto.latitude());
        if (dto.longitude() != null) item.setLongitude(dto.longitude());

// Si city ou address modifiée et pas de coords fournies → regéocoder
        if ((dto.city() != null || dto.address() != null)
                && dto.latitude() == null && dto.longitude() == null) {
            String city = dto.city() != null ? dto.city() : item.getCity();
            String address = dto.address() != null ? dto.address() : item.getAddress();
            double[] coords = geocodingService.getCoordinates(city, address);
            if (coords != null) {
                item.setLatitude(coords[0]);
                item.setLongitude(coords[1]);
            }
        }

        // =========================
        // 🔥 GESTION IMAGES PROPRE
        // =========================

        // 1. Base = images envoyées par le front (peut être vide si suppression)
        List<String> finalImages = new ArrayList<>();

// 🔥 1. récupérer anciennes images depuis le frontend
        if (existingImages != null && !existingImages.isEmpty()) {
            try {
                List<String> existingList = objectMapper.readValue(existingImages, List.class);
                finalImages.addAll(existingList);

                System.out.println("📦 Images existantes: " + existingList);

            } catch (Exception e) {
                System.out.println("❌ Erreur parsing existingImages");
            }
        }

        // 2. Ajouter nouvelles images uploadées
        if (files != null && !files.isEmpty()) {
            List<String> uploaded = imageStorageService.uploadImages(files);
            finalImages.addAll(uploaded);
        }

        // 3. Appliquer seulement si modifié
        item.setImageUrls(finalImages);

        repository.save(item);
    }

    @Override
    public List<ItemSummaryWithDistanceDto> getNearbyItems(double lat, double lng, double radiusKm) {
        return repository.findAll()
                .stream()
                .filter(item -> item.getLatitude() != null && item.getLongitude() != null)
                .filter(item -> item.getStatus() == ItemStatus.ACTIVE)
                .map(item -> {
                    double distance = calculateDistance(lat, lng, item.getLatitude(), item.getLongitude());
                    return new ItemSummaryWithDistanceDto(item, distance);
                })
                .filter(dto -> dto.getDistanceKm() <= radiusKm)
                .sorted(Comparator.comparingDouble(ItemSummaryWithDistanceDto::getDistanceKm))
                .collect(Collectors.toList());
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371; // rayon de la Terre en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
