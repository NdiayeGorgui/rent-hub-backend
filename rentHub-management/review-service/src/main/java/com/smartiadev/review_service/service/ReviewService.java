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

        // 4️⃣ Un seul avis par location(1 pour owner envers le renter, 1 pour renter envers le item)
        if (reviewRepository.existsByRentalIdAndReviewerId(
                request.rentalId(),
                reviewerId)) {
            throw new IllegalStateException(
                    "User already reviewed this rental"
            );
        }
       /* if (reviewRepository.existsByRentalId(request.rentalId())) {
            throw new IllegalStateException(
                    "Review already exists for this rental"
            );
        }*/

        // 5️⃣ Déterminer l’utilisateur noté
        ReviewType type;
        UUID reviewedUserId = null;

        if (reviewerId.equals(rental.ownerId())) {
            type = ReviewType.USER;
            reviewedUserId = rental.renterId();
        } else {
            type = ReviewType.ITEM;
            reviewedUserId = rental.ownerId();
        }

        // 6️⃣ Créer l’avis
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

        // 7️⃣ Publier l’événement
        eventProducer.sendReviewCreated(
                new ReviewCreatedEvent(
                        saved.getId(),
                        saved.getRentalId(),
                        saved.getItemId(),
                        saved.getReviewerId(),
                        saved.getReviewedUserId(),
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

        return reviewRepository
                .findByItemIdAndReviewerIdNot(itemId, item.ownerId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<ReviewDto> getReviewsByUser(UUID userId) {
        return reviewRepository.findByReviewedUserId(userId)
                .stream()
                .map(this::mapToDto)
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

    private ReviewDto mapToDto(Review review) {

        String username = "Unknown";

        try {
            UserProfileInternalDto user = authClient.getUserById(review.getReviewerId());
            username = user.getUsername();
        } catch (Exception e) {
            System.out.println("Auth service unavailable");
        }

        return new ReviewDto(
                review.getId(),
                review.getItemId(),
                review.getReviewerId(),
                review.getReviewedUserId(),
                username,
                review.getRating(),
                review.getComment()
        );
    }


}
