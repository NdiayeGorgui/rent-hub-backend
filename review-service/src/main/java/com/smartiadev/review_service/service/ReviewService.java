package com.smartiadev.review_service.service;

import com.smartiadev.base_domain_service.dto.ReviewCreatedEvent;
import com.smartiadev.review_service.client.AuthClient;
import com.smartiadev.review_service.client.ItemClient;
import com.smartiadev.review_service.client.RentalClient;
import com.smartiadev.review_service.dto.*;
import com.smartiadev.review_service.entity.Review;
import com.smartiadev.review_service.entity.ReviewType;
import com.smartiadev.review_service.kafka.ReviewEventProducer;
import com.smartiadev.review_service.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RentalClient rentalClient;
    private final AuthClient authClient;
    private final ItemClient itemClient;
    private final ReviewEventProducer eventProducer;

    /* =========================
       CREATE REVIEW
       ========================= */

    @Transactional
    public Review createReview(CreateReviewRequest request, UUID reviewerId) {

        // 1️⃣ Vérifier la location
        RentalInfoDTO rental = rentalClient.getRental(request.rentalId());

        // 2️⃣ La location doit être terminée
        if (!"ENDED".equals(rental.status())) {
            throw new IllegalStateException(
                    "Review allowed only after rental is ended"
            );
        }

        // 3️⃣ L’utilisateur doit être impliqué
        if (!reviewerId.equals(rental.ownerId())
                && !reviewerId.equals(rental.renterId())) {
            throw new IllegalStateException("Forbidden");
        }

        // 4️⃣ Un seul avis par utilisateur et par location
        if (reviewRepository.existsByRentalIdAndReviewerId(
                request.rentalId(),
                reviewerId)) {
            throw new IllegalStateException(
                    "User already reviewed this rental"
            );
        }

        // 5️⃣ Déterminer le type d’avis
        ReviewType type;
        UUID reviewedUserId;

        if (reviewerId.equals(rental.ownerId())) {
            type = ReviewType.USER;
            reviewedUserId = rental.renterId();
        } else {
            type = ReviewType.ITEM;
            reviewedUserId = null;
        }

     // ✅ Validation métier
        if (type == ReviewType.USER && reviewedUserId == null) {
            throw new IllegalStateException("USER review must have reviewedUserId");
        }

        if (type == ReviewType.ITEM && reviewedUserId != null) {
            throw new IllegalStateException("ITEM review must not have reviewedUserId");
        }
        if (request.rating() < 1 || request.rating() > 5) {
            throw new IllegalStateException("Rating must be between 1 and 5");
        }

        // 7️⃣ Créer l’avis
        Review review = Review.builder()
                .rentalId(request.rentalId())
                .itemId(rental.itemId())
                .reviewerId(reviewerId)
                .reviewedUserId(reviewedUserId)
                .rating(request.rating())
                .comment(request.comment())
                .createdAt(LocalDateTime.now())
                .type(type)
                .build();

        Review saved = reviewRepository.save(review);

        // 8️⃣ Publier l’événement
        eventProducer.sendReviewCreated(
                new ReviewCreatedEvent(
                        saved.getId(),
                        saved.getRentalId(),
                        saved.getItemId(),
                        saved.getReviewerId(),
                        saved.getReviewedUserId(),
                        rental.ownerId(),
                        saved.getRating()
                )
        );

        return saved;
    }
    /* =========================
       READ (DTO ONLY)
       ========================= */

    public List<ReviewDto> getReviewsByItemId(Long itemId) {

        ItemInternalDTO item = itemClient.getItemById(itemId);

        List<Review> reviews =
                reviewRepository.findByItemIdAndReviewerIdNot(
                        itemId,
                        item.ownerId()
                );

        Map<UUID, UserProfileInternalDto> userMap =
                loadUsersMap(reviews);

        return reviews.stream()
                .map(r -> mapToDto(r, userMap))
                .toList();
    }

   /* public List<ReviewDto> getReviewsByUser(UUID userId) {
        return reviewRepository.findByReviewedUserId(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }*/
   public List<ReviewDto> getReviewsByUser(UUID userId) {
       List<Review> reviews =
               reviewRepository.findByReviewedUserIdAndType(
                       userId,
                       ReviewType.USER
               );

       Map<UUID, UserProfileInternalDto> userMap =
               loadUsersMap(reviews);

       return reviews.stream()
               .map(r -> mapToDto(r, userMap))
               .toList();
   }

   /* public Double getAverageRatingForItem(Long itemId) {
        return reviewRepository.getAverageRatingByItem(itemId);
    }*/
   public Double getAverageRatingForItem(Long itemId) {

       // 🔥 récupérer ownerId
       ItemInternalDTO item = itemClient.getItemById(itemId);
       UUID ownerId = item.ownerId();

       Double avg = reviewRepository
               .getAverageRatingByItemExcludingOwner(itemId, ownerId);

       return avg != null ? avg : 0.0;
   }

    public Double getAverageRatingForUser(UUID userId) {
        return reviewRepository.getAverageRatingByUser(userId);
    }

    public Long getReviewsCountForUser(UUID userId) {
        return reviewRepository.countReviewsByUser(userId);
    }

    public List<Long> getItemIdsWithMinRating(Double minRating) {
        return reviewRepository.findItemIdsWithMinRating(minRating);
    }

    public Long countReviewsForItem(Long itemId) {

        // 🔥 récupérer ownerId
        ItemInternalDTO item = itemClient.getItemById(itemId);
        UUID ownerId = item.ownerId();

        // 🔥 compter uniquement les locataires
        return reviewRepository
                .countByItemIdAndReviewerIdNot(itemId, ownerId);
    }

    public Map<Long, Double> getItemsAverageRatings() {
        return reviewRepository.findItemsWithAverageRating()
                .stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Double) r[1]
                ));
    }



    /* =========================
       MAPPING
       ========================= */


    // Tous les avis reçus par un user (sans filtre de type)
    public List<ReviewDto> getAllReviewsForUser(UUID userId) {
        List<Review> reviews =
                reviewRepository.findByReviewedUserId(userId);

        Map<UUID, UserProfileInternalDto> userMap =
                loadUsersMap(reviews);

        return reviews.stream()
                .map(r -> mapToDto(r, userMap))
                .toList();
    }

    private Map<UUID, UserProfileInternalDto> loadUsersMap(
            List<Review> reviews
    ) {

        List<UUID> userIds = reviews.stream()
                .map(Review::getReviewerId)
                .distinct()
                .toList();

        List<UserProfileInternalDto> users =
                authClient.getUsersBatch(userIds);

        return users.stream()
                .collect(Collectors.toMap(
                        UserProfileInternalDto::getUserId,
                        u -> u
                ));
    }


    private ReviewDto mapToDto(
            Review review,
            Map<UUID, UserProfileInternalDto> userMap
    ) {

        UserProfileInternalDto user =
                userMap.get(review.getReviewerId());

        return new ReviewDto(
                review.getId(),
                review.getItemId(),

                review.getReviewerId(),
                review.getReviewedUserId(),

                user != null
                        ? user.getUsername()
                        : "Unknown",

                user != null
                        ? user.getAverageRating()
                        : 0.0,

                user != null
                        ? user.getReviewsCount()
                        : 0L,

                user != null
                        ? user.getBadge()
                        : null,

                review.getRating(),
                review.getComment()
        );
    }

    public Map<Long, Boolean> hasReviewedBatch(
            List<Long> rentalIds,
            UUID reviewerId
    ) {

        List<Review> reviews =
                reviewRepository.findByRentalIdInAndReviewerId(
                        rentalIds,
                        reviewerId
                );

        Set<Long> reviewedIds =
                reviews.stream()
                        .map(Review::getRentalId)
                        .collect(Collectors.toSet());

        return rentalIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        reviewedIds::contains
                ));
    }

}
