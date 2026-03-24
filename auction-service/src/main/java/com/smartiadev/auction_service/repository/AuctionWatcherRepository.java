package com.smartiadev.auction_service.repository;

import com.smartiadev.auction_service.entity.AuctionWatcher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuctionWatcherRepository
        extends JpaRepository<AuctionWatcher, Long> {

    boolean existsByAuctionIdAndUserId(Long auctionId, UUID userId);

    long countByAuctionId(Long auctionId);

    List<AuctionWatcher> findByAuctionId(Long auctionId);
}
