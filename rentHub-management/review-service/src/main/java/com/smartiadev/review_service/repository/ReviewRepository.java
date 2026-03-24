package com.smartiadev.review_service.repository;

import com.smartiadev.review_service.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    boolean existsByRentalId(Long rentalId);

    List<Review> findByReviewedUserId(UUID reviewedUserId);

    List<Review> findByItemId(Long itemId);

   // List<Review> findByTargetUserId(UUID targetUserId);
   // ⭐ Moyenne d’un article
   @Query("SELECT AVG(r.rating) FROM Review r WHERE r.itemId = :itemId")
   Double getAverageRatingByItem(Long itemId);

    // ⭐ Moyenne d’un utilisateur
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewedUserId = :userId")
    Double getAverageRatingByUser(UUID userId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewedUserId = :userId")
    Long countReviewsByUser(UUID userId);

    @Query("""
    SELECT r.itemId
    FROM Review r
    GROUP BY r.itemId
    HAVING AVG(r.rating) >= :minRating
    """)
    List<Long> findItemIdsWithMinRating(Double minRating);


    @Query("""
    SELECT r.itemId, AVG(r.rating)
    FROM Review r
    GROUP BY r.itemId
    ORDER BY AVG(r.rating) DESC
    """)
    List<Object[]> findItemsWithAverageRating();


    /* ==========================
       📊 STATS ADMIN (GLOBAL)
       ========================== */

    // Nombre total d’avis sur la plateforme
    @Query("SELECT COUNT(r) FROM Review r")
    Long countAllReviews();

    // Note moyenne globale (plateforme)
    @Query("SELECT AVG(r.rating) FROM Review r")
    Double getPlatformAverageRating();


    /* ==========================
       👤 STATS UTILISATEUR
       ========================== */

    // Nombre d’avis reçus par un utilisateur
    @Query("""
           SELECT COUNT(r)
           FROM Review r
           WHERE r.reviewedUserId = :userId
           """)
    Long countReviewsForUser(UUID userId);

    // Note moyenne d’un utilisateur
    @Query("""
           SELECT AVG(r.rating)
           FROM Review r
           WHERE r.reviewedUserId = :userId
           """)
    Double getAverageRatingForUser(UUID userId);


    /* ==========================
       📦 STATS ITEM
       ========================== */

    // Nombre d’avis pour un article
    Long countByItemId(Long itemId);

    // Note moyenne d’un article
    @Query("""
           SELECT AVG(r.rating)
           FROM Review r
           WHERE r.itemId = :itemId
           """)
    Double getAverageRatingForItem(Long itemId);


 boolean existsByRentalIdAndReviewerId(Long rentalId, UUID reviewerId);
 List<Review> findByItemIdAndReviewerIdNot(Long itemId, UUID reviewerId);
 Long countByItemIdAndReviewerIdNot(Long itemId, UUID reviewerId);

 @Query("""
       SELECT AVG(r.rating)
       FROM Review r
       WHERE r.itemId = :itemId
       AND r.reviewerId <> :ownerId
       """)
 Double getAverageRatingByItemExcludingOwner(
         @Param("itemId") Long itemId,
         @Param("ownerId") UUID ownerId
 );
}


