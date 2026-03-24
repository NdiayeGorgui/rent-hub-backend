package com.smartiadev.item_service.service;

import com.smartiadev.item_service.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemStatsService {

    private final ItemRepository itemRepository;

    public Long countAllItems() {
        return itemRepository.countAllItems();
    }

    public Long countPublishedItems() {
        return itemRepository.countActiveItems();
    }
}
