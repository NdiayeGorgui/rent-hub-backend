package com.smartiadev.item_service.repository;

import com.smartiadev.item_service.entity.Item;
import com.smartiadev.item_service.entity.ItemStatus;
import org.hibernate.sql.results.graph.FetchList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, Long>,  JpaSpecificationExecutor<Item> {

    List<Item> findByActiveTrue();

    List<Item> findByOwnerId(UUID ownerId);
    Page<Item> findByOwnerId(UUID ownerId, Pageable pageable);

    @Query("""
SELECT i FROM Item i
WHERE i.active = true
AND (:city IS NULL OR i.city = :city)
AND (:categoryId IS NULL OR i.categoryId = :categoryId)
AND (:minPrice IS NULL OR i.pricePerDay >= :minPrice)
AND (:maxPrice IS NULL OR i.pricePerDay <= :maxPrice)
AND (:excludedIds IS NULL OR i.id NOT IN :excludedIds)
AND (:ratedItemIds IS NULL OR i.id IN :ratedItemIds)
""")
    Page<Item> searchAvailableItems(
            String city,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            List<Long> excludedIds,
            List<Long> ratedItemIds,
            Pageable pageable
    );

    // 📦 total items
    @Query("SELECT COUNT(i) FROM Item i")
    Long countAllItems();

    // 📢 items publiés (visibles)
    @Query("SELECT COUNT(i) FROM Item i WHERE i.active = true")
    Long countActiveItems();

    List<Item> findByOwnerIdAndStatusNot(UUID ownerId, ItemStatus status);
    List<Item> findByStatus(ItemStatus status);
}
