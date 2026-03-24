package com.smartiadev.auction_service.repository;

import com.smartiadev.auction_service.entity.Auction;
import com.smartiadev.auction_service.entity.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByStatus(AuctionStatus status);
    boolean existsByItemIdAndStatus(Long itemId, AuctionStatus status);
    List<Auction> findByStatusAndEndDateBefore(
            AuctionStatus status,
            LocalDateTime date
    );

// stats
Long countByStatus(AuctionStatus status);

    @Query("""
        SELECT COUNT(a)
        FROM Auction a
        WHERE a.status = 'CLOSED'
        AND EXISTS (
            SELECT b FROM Bid b WHERE b.auctionId = a.id
        )
    """)
    Long countAuctionsWithWinner();

    @Query("""
        SELECT COUNT(a)
        FROM Auction a
        WHERE a.status = 'CLOSED'
        AND NOT EXISTS (
            SELECT b FROM Bid b WHERE b.auctionId = a.id
        )
    """)
    Long countAuctionsWithoutBid();

    @Query("""
        SELECT AVG(a.currentPrice)
        FROM Auction a
        WHERE a.status = 'CLOSED'
    """)
    Double averageWinningPrice();

    Optional<Auction> findByItemIdAndStatus(Long itemId, AuctionStatus status);

    Optional<Auction> findByItemId(Long itemId);
}
