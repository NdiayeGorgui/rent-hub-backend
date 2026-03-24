package com.smartiadev.auction_service.repository;

import com.smartiadev.auction_service.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<Bid> findTopByAuctionIdOrderByAmountDesc(Long auctionId);


    @Query("""
SELECT COUNT(DISTINCT b.bidderId)
FROM Bid b
WHERE b.auctionId = :auctionId
""")
    Integer countParticipants(Long auctionId);
}
