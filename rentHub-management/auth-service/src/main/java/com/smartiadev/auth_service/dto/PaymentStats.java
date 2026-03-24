package com.smartiadev.auth_service.dto;

public record PaymentStats(
        Long totalPayments,
        Long successPayments,
        Long failedPayments,
        Long pendingPayments,
        Double totalAmount
) {}
