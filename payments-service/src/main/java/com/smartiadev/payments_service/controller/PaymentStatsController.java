package com.smartiadev.payments_service.controller;

import com.smartiadev.payments_service.dto.PaymentStats;
import com.smartiadev.payments_service.service.PaymentStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/internal")
@Tag(name = "Payments - Internal", description = "Internal endpoints for payment monitoring and statistics")
public class PaymentStatsController {

    private final PaymentStatsService service;

    /* ============================
       PAYMENT STATISTICS (INTERNAL)
       ============================ */
    @Operation(
            summary = "Get payment statistics (internal)",
            description = "Internal endpoint used for monitoring and analytics. Returns aggregated payment statistics such as total payments, total revenue, and successful transactions."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment statistics retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentStats.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/stats")
    public PaymentStats stats() {
        return service.getStats();
    }
}