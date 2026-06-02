package com.smartiadev.rental_service.scheduler;

import com.smartiadev.base_domain_service.dto.RentalStartedEvent;
import com.smartiadev.rental_service.client.ItemClient;
import com.smartiadev.rental_service.dto.ItemInternalDTO;
import com.smartiadev.rental_service.entity.RentalStatus;
import com.smartiadev.rental_service.entity.Rental;
import com.smartiadev.rental_service.kafka.RentalEventProducer;
import com.smartiadev.rental_service.repository.RentalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RentalStatusScheduler {

    private final RentalRepository repository;
    private final RentalEventProducer eventProducer;
    private final ItemClient itemClient;

    @Scheduled(cron = "0 0 * * * *") // toutes les heures
    @Transactional
    public void startRentals() {



        LocalDate today = LocalDate.now();

        List<Rental> toStart =
                repository.findByStatusAndStartDate(
                        RentalStatus.APPROVED,
                        today
                );
        // 📦 1. batch items (ICI)
        List<Long> itemIds = toStart.stream()
                .map(Rental::getItemId)
                .toList();

        Map<Long, ItemInternalDTO> items = itemClient.getItemsBatch(itemIds)
                .stream()
                .collect(Collectors.toMap(ItemInternalDTO::id, i -> i));

        // 🔁 2. boucle rentals
        for (Rental rental : toStart) {

            rental.setStatus(RentalStatus.ONGOING);

            ItemInternalDTO item = items.get(rental.getItemId());
            String itemTitle = (item != null) ? item.title() : "Item";

            eventProducer.sendRentalStarted(
                    new RentalStartedEvent(
                            rental.getId(),
                            rental.getItemId(),
                            rental.getOwnerId(),
                            rental.getRenterId(),
                            itemTitle
                    )
            );
        }

        repository.saveAll(toStart);
    }
}

