package com.smartiadev.payments_service.service;

import com.smartiadev.base_domain_service.dto.AuctionCancelledEvent;
import com.smartiadev.base_domain_service.dto.AuctionFeeRefundedEvent;
import com.smartiadev.base_domain_service.dto.PaymentCompletedEvent;
import com.smartiadev.base_domain_service.dto.PaymentCreatedEvent;
import com.smartiadev.base_domain_service.model.PaymentStatus;
import com.smartiadev.base_domain_service.model.PaymentType;
import com.smartiadev.payments_service.client.UserClient;
import com.smartiadev.payments_service.dto.CreatePaymentRequest;
import com.smartiadev.payments_service.dto.PaymentIntentResponse;
import com.smartiadev.payments_service.dto.PaymentResponse;
import com.smartiadev.payments_service.dto.PaymentProviderResult;
import com.smartiadev.payments_service.entity.Payment;
import com.smartiadev.payments_service.kafka.PaymentEventPublisher;
import com.smartiadev.payments_service.repository.PaymentRepository;
import com.smartiadev.payments_service.stripe.PaymentProvider;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentProvider paymentProvider;
    private final UserClient userClient;
    private final PaymentEventPublisher paymentEventPublisher;
    private final KafkaTemplate<String, Object> kafkaTemplate;



    private PaymentIntent createStripePaymentIntent(
            UUID userId,
            Double amount,
            PaymentType type,
            Long itemId,
            Long auctionId
    ) throws StripeException {

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount((long) (amount * 100))
                        .setCurrency("cad")
                        .putMetadata("userId", userId.toString())
                        .putMetadata("type", type.name())
                        .putMetadata("itemId", itemId != null ? itemId.toString() : "")
                        .putMetadata("auctionId", auctionId != null ? auctionId.toString() : "")
                        .build();

        return PaymentIntent.create(params);
    }

    @Transactional
    public PaymentIntentResponse createPremiumPayment(UUID userId, CreatePaymentRequest request) throws StripeException {

        PaymentIntent intent = createStripePaymentIntent(
                userId,
                request.amount(),
                PaymentType.SUBSCRIPTION,
                null,
                null
        );

        Payment payment = repository.save(
                Payment.builder()
                        .userId(userId)
                        .amount(request.amount())
                        .type(PaymentType.SUBSCRIPTION)
                        .status(PaymentStatus.PENDING)
                        .paymentIntentId(intent.getId())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return new PaymentIntentResponse(
                intent.getClientSecret(),
                intent.getId()
        );
    }

    @Transactional
    public PaymentIntentResponse createCancellationPayment(
            Long auctionId,
            Long itemId,
            UUID userId,
            Double amount
    ) throws StripeException {

        // 🔥 Utilisation de la méthode commune
        PaymentIntent intent = createStripePaymentIntent(
                userId,
                amount,
                PaymentType.AUCTION_CANCELLATION_FEE,
                itemId,
                auctionId
        );

        // 💾 Sauvegarde du paiement
        Payment payment = repository.save(
                Payment.builder()
                        .userId(userId)
                        .auctionId(auctionId)
                        .itemId(itemId)
                        .amount(amount)
                        .type(PaymentType.AUCTION_CANCELLATION_FEE)
                        .status(PaymentStatus.PENDING)
                        .paymentIntentId(intent.getId())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        // (optionnel) log
        log.info("✅ Cancellation payment created → intentId={}", intent.getId());

        // 🔁 Retour au front
        return new PaymentIntentResponse(
                intent.getClientSecret(),
                intent.getId()
        );
    }

    @Transactional
    public PaymentIntentResponse createAuctionFeePayment(UUID userId, Long itemId) throws StripeException {

        double amount = 10.0;

        PaymentIntent intent = createStripePaymentIntent(
                userId,
                amount,
                PaymentType.AUCTION_FEE,
                itemId,
                null
        );

        Payment payment = repository.save(
                Payment.builder()
                        .userId(userId)
                        .itemId(itemId)
                        .amount(amount)
                        .type(PaymentType.AUCTION_FEE)
                        .status(PaymentStatus.PENDING)
                        .paymentIntentId(intent.getId())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return new PaymentIntentResponse(
                intent.getClientSecret(),
                intent.getId()
        );
    }

    @Transactional
    public PaymentIntentResponse createRenewalPayment(UUID userId) throws StripeException {

        double amount = 9.99;

        PaymentIntent intent = createStripePaymentIntent(
                userId,
                amount,
                PaymentType.SUBSCRIPTION,
                null,
                null
        );

        // 🔥 CONFIRMATION AUTOMATIQUE (SANS FRONT)
        intent = PaymentIntent.retrieve(intent.getId());
        intent.confirm(
                PaymentIntentConfirmParams.builder()
                        .setPaymentMethod("pm_card_visa") // test
                        .setOffSession(true)
                        .build()
        );

        Payment payment = repository.save(
                Payment.builder()
                        .userId(userId)
                        .amount(amount)
                        .type(PaymentType.SUBSCRIPTION_RENEWAL)
                        .status(PaymentStatus.PENDING)
                        .paymentIntentId(intent.getId())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return new PaymentIntentResponse(
                intent.getClientSecret(),
                intent.getId()
        );
    }
    @Transactional
    public void refundAuctionFee(String paymentIntentId) throws StripeException {

        Payment payment = repository
                .findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getType() != PaymentType.AUCTION_FEE) {
            throw new IllegalStateException("Invalid payment type");
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Payment not successful");
        }

        boolean alreadyRefunded = repository.existsByPaymentIntentIdAndType(
                paymentIntentId,
                PaymentType.AUCTION_REFUND
        );

        if (alreadyRefunded) {
            throw new IllegalStateException("Payment already refunded");
        }

        // 💸 Stripe refund
        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .setAmount((long) (payment.getAmount() * 100))
                .build();

        Refund refund = Refund.create(params);

        if (!"succeeded".equals(refund.getStatus())) {
            throw new IllegalStateException("Refund failed");
        }

        // 💾 save refund
        repository.save(
                Payment.builder()
                        .userId(payment.getUserId())
                        .itemId(payment.getItemId())
                        .amount(payment.getAmount())
                        .type(PaymentType.AUCTION_REFUND)
                        .status(PaymentStatus.SUCCESS)
                        .paymentIntentId(refund.getId())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        paymentEventPublisher.publishAuctionRefunded(
                new AuctionFeeRefundedEvent(
                        payment.getItemId(),
                        payment.getUserId(),  // <-- c'est l'owner
                        payment.getAmount()
                )
        );
    }

  /*  @Transactional
    public void refundSubscription(String paymentIntentId) throws StripeException {

        Payment payment = repository
                .findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getType() != PaymentType.SUBSCRIPTION &&
                payment.getType() != PaymentType.SUBSCRIPTION_RENEWAL) {
            throw new IllegalStateException("Invalid payment type");
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Payment not successful");
        }

        // Stripe refund
        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .setAmount((long) (payment.getAmount() * 100))
                .build();

        Refund.create(params);

        repository.save(
                Payment.builder()
                        .userId(payment.getUserId())
                        .amount(payment.getAmount())
                        .type(PaymentType.SUBSCRIPTION) // ou SUBSCRIPTION_REFUND si tu veux être propre
                        .status(PaymentStatus.SUCCESS)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }*/
    public List<PaymentResponse> getAllPayments() {

        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    public List<PaymentResponse> getMyPayments(UUID userId) {

        return repository.findByUserId(userId)
                .stream()
                .map(this::map)
                .toList();
    }
    private PaymentResponse map(Payment payment) {

        String fullName = "Unknown";

        try {
            fullName = userClient.getUser(payment.getUserId()).fullName();
        } catch (Exception ignored) {}

        return new PaymentResponse(
                payment.getId(),
                payment.getItemId(),
                payment.getUserId(),
                fullName,
                payment.getAmount(),
                payment.getType(),
                payment.getStatus().name(),
                payment.getCreatedAt(),
                payment.getPaymentIntentId(),
                null
        );
    }

    public List<PaymentResponse> getPendingPayments() {

        return repository.findByStatus(PaymentStatus.PENDING)
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional
    public PaymentResponse confirmPayment(String intentId) {

        Payment payment = repository
                .findByPaymentIntentId(intentId)
                .orElseThrow();

        payment.setStatus(PaymentStatus.SUCCESS);

        repository.save(payment);

        return map(payment);
    }

    public PaymentStatus getPaymentStatus(Long paymentId) {

        Payment payment = repository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return payment.getStatus();
    }

    public void handlePaymentSuccess(Payment payment) {

        // ✅ 1. Event GLOBAL (toujours envoyé)
        paymentEventPublisher.publishPaymentCompleted(
                new PaymentCompletedEvent(
                        payment.getId(),
                        payment.getPaymentIntentId(),
                        payment.getUserId(),
                        payment.getItemId(),
                        payment.getAmount(),
                        payment.getType(),
                        payment.getStatus(),
                        LocalDateTime.now()
                )
        );

        // ✅ 2. Event MÉTIER
        switch (payment.getType()) {

            case AUCTION_CANCELLATION_FEE:
                kafkaTemplate.send(
                        "auction.cancelled",
                        new AuctionCancelledEvent(
                                payment.getAuctionId(),
                                payment.getItemId(),
                                payment.getUserId(),
                                payment.getAmount()
                        )
                );
                break;

            case AUCTION_FEE:
                kafkaTemplate.send(
                        "auction.fee.paid",
                        payment.getItemId().toString(),
                        payment
                );
                break;

            case SUBSCRIPTION:
                kafkaTemplate.send(
                        "user.premium.activated",
                        payment.getUserId().toString(),
                        payment.getUserId()
                );
                break;

            case SUBSCRIPTION_RENEWAL:
                kafkaTemplate.send(
                        "user.subscription.renewed",
                        payment.getUserId().toString(),
                        payment.getUserId()
                );
                break;

            case AUCTION_REFUND:
                paymentEventPublisher.publishAuctionRefunded(
                        new AuctionFeeRefundedEvent(
                                payment.getItemId(),
                                payment.getUserId(),  // <-- c'est l'owner
                                payment.getAmount()
                        )
                );
                break;

            default:
                log.warn("Unhandled payment type: {}", payment.getType());
        }
    }
}