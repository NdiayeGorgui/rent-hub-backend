package com.smartiadev.payments_service.controller;

import com.smartiadev.base_domain_service.model.PaymentStatus;
import com.smartiadev.payments_service.dto.CancelRequest;
import com.smartiadev.payments_service.dto.CreatePaymentRequest;
import com.smartiadev.payments_service.dto.PaymentIntentResponse;
import com.smartiadev.payments_service.dto.PaymentResponse;
import com.smartiadev.payments_service.entity.Payment;
import com.smartiadev.payments_service.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Endpoints for managing user payments and premium subscriptions")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService service;

    /* ============================
       SUBSCRIBE WITH PAYMENT
       ============================ */
    @Operation(
            summary = "Create a subscription payment",
            description = "Create a payment for activating a premium subscription for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid payment request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "402", description = "Payment required / Payment failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/subscribe")
    public PaymentIntentResponse subscribe(
            @AuthenticationPrincipal Jwt jwt,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payment request details (amount, payment method, etc.)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreatePaymentRequest.class)
                    )
            )
            @RequestBody CreatePaymentRequest request
    ) throws StripeException {
        UUID userId = UUID.fromString(jwt.getSubject());
        return service.createPremiumPayment(userId, request);
    }


    @GetMapping
    @Operation(summary = "Get all payments (admin)")
    public List<PaymentResponse> getAllPayments() {
        return service.getAllPayments();
    }

    @GetMapping("/me")
    @Operation(summary = "Get my payments")
    public List<PaymentResponse> getMyPayments(
            @AuthenticationPrincipal Jwt jwt
    ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        return service.getMyPayments(userId);
    }

    @GetMapping("/pending")
    public List<PaymentResponse> getPendingPayments() {
        return service.getPendingPayments();
    }

    @PostMapping("/auction-fee/{itemId}")
    public PaymentIntentResponse payAuctionFee(
            @PathVariable Long itemId,
            @AuthenticationPrincipal Jwt jwt
    ) throws StripeException {

        UUID userId = UUID.fromString(jwt.getSubject());

        return service.createAuctionFeePayment(userId,itemId);
    }

    @PostMapping("/admin/refund-auction-fee")
    public void refundAuctionFee(
            @RequestParam String paymentIntentId,
            @RequestParam UUID winnerId,
            @RequestParam Long auctionId
    ) throws StripeException {
        service.refundAuctionFee(paymentIntentId, winnerId, auctionId);
    }

    // Dans PaymentController
    @PostMapping("/penalty/pay")
    public ResponseEntity<PaymentIntentResponse> payPenalty(
            @AuthenticationPrincipal Jwt jwt
    ) throws StripeException {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(service.createPenaltyPayment(userId));
    }

    // PaymentController
    @GetMapping("/admin/auction-fee")
    public PaymentResponse getAuctionFeeByItemId(
            @RequestParam Long itemId
    ) {
        return service.getAuctionFeePayment(itemId);
    }

    @GetMapping("/internal/{paymentId}/status")
    public PaymentStatus getStatus(@PathVariable Long paymentId) {

        return service.getPaymentStatus(paymentId);
    }

    @PostMapping("/auction/cancel")
    public PaymentIntentResponse cancelAuctionPayment(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CancelRequest request
    ) throws Exception {

        UUID userId = UUID.fromString(jwt.getSubject());

        return service.createCancellationPayment(
                request.auctionId(),
                request.itemId(),
                userId, // ✅ sécurisé
                request.amount()
        );
    }
}