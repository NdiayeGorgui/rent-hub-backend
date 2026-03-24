package com.smartiadev.payments_service.dto;

public record PaymentStats(
        Long totalPayments,
        Long successPayments,
        Long failedPayments,
        Long pendingPayments,
        Double totalAmount
) {}

