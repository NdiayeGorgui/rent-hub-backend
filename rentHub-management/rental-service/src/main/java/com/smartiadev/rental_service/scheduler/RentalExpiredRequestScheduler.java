package com.smartiadev.rental_service.scheduler;

import com.smartiadev.base_domain_service.dto.RentalRequestExpiredEvent;
import com.smartiadev.rental_service.entity.Rental;
import com.smartiadev.rental_service.entity.RentalStatus;
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
public class RentalExpiredRequestScheduler {

    private final RentalRepository repository;
    private final RentalEventProducer eventProducer;

    @Scheduled(cron = "0 0 * * * *") // toutes les heures
   //@Scheduled(cron = "0 */2 * * * *") // toutes les 2 minutes
    @Transactional
    public void cancelExpiredRequests() {

        LocalDate today = LocalDate.now();

        List<Rental> expiredRequests =
                repository.findByStatusAndStartDateBefore(
                        RentalStatus.CREATED,
                        today
                );
        System.out.println("⚠️ Aujourd'hui : " + today);
        expiredRequests.forEach(r -> System.out.println(r.getId() + " startDate=" + r.getStartDate()));
        for (Rental rental : expiredRequests) {

            rental.setStatus(RentalStatus.CANCELLED);

            // notification kafka
            eventProducer.sendRentalRequestExpired(
                    new RentalRequestExpiredEvent(
                            rental.getId(),
                            rental.getItemId(),
                            rental.getOwnerId(),
                            rental.getRenterId()
                    )
            );
        }

        repository.saveAll(expiredRequests);
    }
}
