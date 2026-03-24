package com.smartiadev.auth_service.client;

import com.smartiadev.auth_service.dto.PaymentStats;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "payments-service")
public interface PaymentClient {

    @GetMapping("/api/payments/internal/stats")
    PaymentStats getStats();
}
