package com.smartiadev.payments_service.service;

import com.smartiadev.base_domain_service.model.PaymentStatus;
import com.smartiadev.payments_service.dto.PaymentStats;
import com.smartiadev.payments_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentStatsService {

    private final PaymentRepository repository;

    public PaymentStats getStats() {

        return new PaymentStats(
                repository.count(),
                repository.countByStatus(PaymentStatus.SUCCESS),
                repository.countByStatus(PaymentStatus.FAILED),
                repository.countByStatus(PaymentStatus.PENDING),
                repository.sumSuccessfulPayments()
        );
    }
}
