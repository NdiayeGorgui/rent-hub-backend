package com.smartiadev.payments_service.controller;

import com.smartiadev.base_domain_service.dto.PaymentCompletedEvent;
import com.smartiadev.base_domain_service.model.PaymentStatus;
import com.smartiadev.payments_service.entity.Payment;
import com.smartiadev.payments_service.kafka.PaymentEventPublisher;
import com.smartiadev.payments_service.repository.PaymentRepository;
import com.smartiadev.payments_service.service.PaymentService;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments/stripe/webhook")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final PaymentRepository repository;
    private final PaymentEventPublisher publisher;
    private final PaymentService paymentService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping
    @Transactional
    public void handle(@RequestBody String payload,
                       @RequestHeader("Stripe-Signature") String sig) throws Exception {

        Event event = Webhook.constructEvent(payload, sig, webhookSecret);
        log.info("Received Stripe event: {}", event.getType());

        PaymentIntent intent = null;

        // 1️⃣ Essayer de désérialiser directement le PaymentIntent
        if (event.getDataObjectDeserializer().getObject().isPresent()) {
            intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
        } else {
            // 2️⃣ Sinon caster le StripeObject au type PaymentIntent
            StripeObject stripeObject = event.getData().getObject();
            if (stripeObject instanceof PaymentIntent) {
                intent = (PaymentIntent) stripeObject;
            } else {
                log.warn("Event object is not a PaymentIntent: {}", stripeObject.getClass());
                return;
            }
        }

        // Chercher le Payment correspondant en base
        Optional<Payment> optionalPayment = repository.findByPaymentIntentId(intent.getId());
        if (optionalPayment.isEmpty()) {
            log.warn("Payment NOT FOUND in DB for PaymentIntentId: {}", intent.getId());
            return;
        }

        Payment payment = optionalPayment.get();
        log.info("Payment FOUND in DB: id={}, status={}", payment.getId(), payment.getStatus());

        switch (event.getType()) {
            case "payment_intent.succeeded":
                log.info("🔥 PAYMENT SUCCESS received for intentId={}", intent.getId());
                if (payment.getStatus() != PaymentStatus.SUCCESS) {
                    payment.setStatus(PaymentStatus.SUCCESS);
                    repository.save(payment);
                    log.info("Publishing PaymentCompletedEvent for user {}", payment.getUserId());
                  /*  publisher.publishPaymentCompleted(
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
                    );*/
                    paymentService.handlePaymentSuccess(payment);
                    log.info("Payment succeeded and updated to SUCCESS: {}", payment.getId());
                }
                break;

            case "payment_intent.payment_failed":
                if (payment.getStatus() != PaymentStatus.FAILED) {
                    payment.setStatus(PaymentStatus.FAILED);
                    payment.setFailureReason("STRIPE_FAILED");
                    repository.save(payment);

                    publisher.publishPaymentFailed(payment.getUserId(), "STRIPE_FAILED");
                    log.info("Payment failed and updated to FAILED: {}", payment.getId());
                }
                break;

            default:
                log.info("Unhandled Stripe event type: {}", event.getType());
        }
    }
}