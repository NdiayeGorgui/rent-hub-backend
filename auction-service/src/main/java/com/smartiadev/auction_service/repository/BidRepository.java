package com.smartiadev.auction_service.repository;

import com.smartiadev.auction_service.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<Bid> findTopByAuctionIdOrderByAmountDesc(Long auctionId);


    @Query("""
SELECT COUNT(DISTINCT b.bidderId)
FROM Bid b
WHERE b.auctionId = :auctionId
""")
    Integer countParticipants(Long auctionId);

    @Query("SELECT DISTINCT b.auctionId FROM Bid b WHERE b.bidderId = :userId")
    List<Long> findDistinctAuctionIdsByBidderId(@Param("userId") UUID userId);
}
