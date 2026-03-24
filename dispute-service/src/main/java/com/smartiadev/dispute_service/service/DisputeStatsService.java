package com.smartiadev.dispute_service.service;

import com.smartiadev.dispute_service.dto.DisputeStats;
import com.smartiadev.dispute_service.entity.DisputeStatus;
import com.smartiadev.dispute_service.repository.DisputeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DisputeStatsService {

    private final DisputeRepository repository;

    public DisputeStats getStats() {

        return new DisputeStats(
                repository.count(),
                repository.countByStatus(DisputeStatus.OPEN),
                repository.countByStatus(DisputeStatus.IN_REVIEW),
                repository.countByStatus(DisputeStatus.RESOLVED),
                repository.countByStatus(DisputeStatus.REJECTED),
                repository.countByCreatedAtAfter(LocalDateTime.now().minusDays(30)),
                repository.avgResolutionTimeMinutes(),
                repository.countWithAdminDecision()
        );
    }
}
