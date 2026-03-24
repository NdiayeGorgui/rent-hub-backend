package com.smartiadev.item_service.repository.specification;

import com.smartiadev.item_service.entity.Item;
import com.smartiadev.item_service.entity.ItemStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ItemSpecifications {

    public static Specification<Item> isActive(Boolean active) {
        return (root, query, cb) ->
                active == null ? null : cb.equal(root.get("active"), active);
    }

    public static Specification<Item> hasCity(String city) {
        return (root, query, cb) ->
                city == null ? null :
                        cb.like(cb.lower(root.get("city")),
                                "%" + city.toLowerCase() + "%");
    }

    public static Specification<Item> searchKeyword(String keyword) {
        return (root, query, cb) -> {

            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Item> hasCategory(Long categoryId) {
        return (root, query, cb) ->
                categoryId == null ? null :
                        cb.equal(root.get("categoryId"), categoryId);
    }
    public static Specification<Item> hasType(String type) {
        return (root, query, cb) -> {

            if (type == null || type.isEmpty()) {
                return null;
            }

            return cb.equal(root.get("type"), type);
        };
    }

    public static Specification<Item> hasStatus(ItemStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Item> priceBetween(Double min, Double max) {
        return (root, query, cb) -> {

            if (min == null && max == null) return null;

            if (min != null && max != null)
                return cb.between(root.get("pricePerDay"), min, max);

            if (min != null)
                return cb.greaterThanOrEqualTo(
                        root.get("pricePerDay"), min);

            return cb.lessThanOrEqualTo(
                    root.get("pricePerDay"), max);
        };
    }

    public static Specification<Item> ownedBy(UUID ownerId) {
        return (root, query, cb) ->
                ownerId == null ? null :
                        cb.equal(root.get("ownerId"), ownerId);
    }

    public static Specification<Item> createdAfter(LocalDateTime date) {
        return (root, query, cb) ->
                date == null ? null :
                        cb.greaterThanOrEqualTo(
                                root.get("createdAt"), date);
    }

    // ✅ Exclure des articles (déjà loués)
    public static Specification<Item> excludeIds(List<Long> ids) {
        return (ids == null || ids.isEmpty())
                ? null
                : (root, query, cb) ->
                cb.not(root.get("id").in(ids));
    }

    // ⭐ Inclure uniquement les articles avec note ≥ minRating
    public static Specification<Item> includeIds(List<Long> ids) {
        return (ids == null || ids.isEmpty())
                ? null
                : (root, query, cb) ->
                root.get("id").in(ids);
    }
}