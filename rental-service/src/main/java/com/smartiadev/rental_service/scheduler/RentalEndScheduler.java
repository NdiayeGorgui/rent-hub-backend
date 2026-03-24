package com.smartiadev.rental_service.scheduler;

import com.smartiadev.base_domain_service.dto.RentalEndedEvent;
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
public class RentalEndScheduler {

    private final RentalRepository repository;
    private final RentalEventProducer eventProducer;

    // toutes les heures
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void endRentals() {

        LocalDate today = LocalDate.now();

        List<Rental> toEnd =
                repository.findByStatusAndEndDate(
                        RentalStatus.ONGOING,
                        today
                );

        for (Rental rental : toEnd) {
            rental.setStatus(RentalStatus.ENDED);

            // 📣 EVENT KAFKA
            eventProducer.sendRentalEnded(
                    new RentalEndedEvent(
                            rental.getId(),
                            rental.getItemId(),
                            rental.getOwnerId(),
                            rental.getRenterId()
                    )
            );
        }

        repository.saveAll(toEnd);
    }
}
