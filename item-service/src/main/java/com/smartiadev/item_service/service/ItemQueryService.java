package com.smartiadev.item_service.service;

import com.smartiadev.item_service.dto.ItemSearchRequest;
import com.smartiadev.item_service.entity.Item;
import com.smartiadev.item_service.repository.ItemRepository;
import com.smartiadev.item_service.repository.specification.ItemSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemQueryService {

    private final ItemRepository itemRepository;

    public Page<Item> searchPublishedItems(ItemSearchRequest request) {

        Specification<Item> spec = Specification
                .where(ItemSpecifications.isActive(true)) // articles publiés
                .and(ItemSpecifications.hasCity(request.getCity()))
                .and(ItemSpecifications.hasCategory(request.getCategoryId()))
                .and(ItemSpecifications.priceBetween(
                        request.getMinPrice(),
                        request.getMaxPrice()))
                .and(ItemSpecifications.ownedBy(request.getOwnerId()))
                .and(ItemSpecifications.createdAfter(request.getCreatedAfter()));

        Sort sort = Sort.by(
                Sort.Direction.fromString(request.getDirection()),
                request.getSortBy()
        );

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                sort
        );

        return itemRepository.findAll(spec, pageable);
    }


}
