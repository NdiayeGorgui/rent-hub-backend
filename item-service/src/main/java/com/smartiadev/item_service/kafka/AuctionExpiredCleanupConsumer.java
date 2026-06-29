package com.smartiadev.item_service.kafka;

import com.smartiadev.base_domain_service.dto.AuctionExpiredCleanupEvent;
import com.smartiadev.item_service.entity.Item;
import com.smartiadev.item_service.repository.ItemRepository;
import com.smartiadev.item_service.service.ImageStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionExpiredCleanupConsumer {

    private final ItemRepository itemRepository;
    private final ImageStorageService imageStorageService;

    @KafkaListener(
            topics = "auction.expired.cleanup"
    )
    @Transactional
    public void onAuctionExpiredCleanup(AuctionExpiredCleanupEvent event) {

        Optional<Item> optionalItem = itemRepository.findById(event.itemId());

        if (optionalItem.isEmpty()) {
            log.warn("Item {} déjà supprimé ou introuvable", event.itemId());
            return;
        }

        Item item = optionalItem.get();

        // ← Supprime les fichiers images physiques
        if (item.getImageUrls() != null && !item.getImageUrls().isEmpty()) {
            imageStorageService.deleteImages(item.getImageUrls());
        }

        // ← Supprime l'item de la BD (les rows item_images sont supprimées via @ElementCollection)
        itemRepository.delete(item);

        log.info("🗑️ Item {} et ses images supprimés (enchère {} expirée)",
                event.itemId(), event.auctionId());
    }
}
