package com.smartiadev.rental_service.scheduler;

import com.smartiadev.base_domain_service.dto.RentalStartedEvent;
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

@Component
@RequiredArgsConstructor
public class RentalStatusScheduler {

    private final RentalRepository repository;
    private final RentalEventProducer eventProducer;

    @Scheduled(cron = "0 0 * * * *") // toutes les heures
    @Transactional
    public void startRentals() {

        LocalDate today = LocalDate.now();

        List<Rental> toStart =
                repository.findByStatusAndStartDate(
                        RentalStatus.APPROVED,
                        today
                );

        for (Rental rental : toStart) {
            rental.setStatus(RentalStatus.ONGOING);
            eventProducer.sendRentalStarted(
                    new RentalStartedEvent(
                            rental.getId(),
                            rental.getItemId(),
                            rental.getOwnerId(),
                            rental.getRenterId()
                    )
            );
        }

        repository.saveAll(toStart);
    }
}

