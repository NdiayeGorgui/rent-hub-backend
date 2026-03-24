package com.smartiadev.rental_service.service;

import com.smartiadev.rental_service.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalStatsService {

    private final RentalRepository rentalRepository;

    public Long countAllRentals() {
        return rentalRepository.countAllRentals();
    }

    public Long countActiveRentals() {
        return rentalRepository.countActiveRentals();
    }

    public Double getTotalRevenue() {
        return rentalRepository.getTotalRevenue();
    }
}
