package com.smartiadev.item_service.client;

import com.smartiadev.base_domain_service.model.PaymentStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "payments-service")
public interface PaymentClient {

    @GetMapping("/api/payments/internal/{paymentId}/status")
    PaymentStatus getPaymentStatus(@PathVariable Long paymentId);

}