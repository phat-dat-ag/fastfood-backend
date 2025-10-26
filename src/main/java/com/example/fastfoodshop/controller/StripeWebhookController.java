package com.example.fastfoodshop.controller;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String ENDPOINT_SECRET;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, ENDPOINT_SECRET);
        } catch (Exception e) {
            System.out.println("Webhook verification failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        System.out.println("Received event: " + event.getType());

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                if (dataObjectDeserializer.getObject().isPresent()) {
                    PaymentIntent intent = (PaymentIntent) dataObjectDeserializer.getObject().get();
                    System.out.println("✅ Payment succeeded: " + intent.getId());
                } else {
                    System.out.println("⚠ Could not deserialize payment_intent.succeeded event data. Raw: "
                            + event.getData().getObject().toJson());
                }
            }

            case "payment_intent.payment_failed" -> {
                if (dataObjectDeserializer.getObject().isPresent()) {
                    PaymentIntent failedIntent = (PaymentIntent) dataObjectDeserializer.getObject().get();
                    System.out.println("❌ Payment failed: " + failedIntent.getId());
                    System.out.println("Failure message: " + failedIntent.getLastPaymentError().getMessage());
                } else {
                    System.out.println("⚠ Could not deserialize payment_intent.payment_failed event data. Raw: "
                            + event.getData().getObject().toJson());
                }
            }

            case "charge.failed" -> {
                System.out.println("❌ Charge failed event received. Raw data: "
                        + event.getData().getObject().toJson());
            }

            default -> System.out.println("Event not handled: " + event.getType());
        }
        return ResponseEntity.ok("Received");
    }
}
