package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.enums.PaymentStatus;
import com.example.fastfoodshop.service.OrderService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String ENDPOINT_SECRET;

    private final OrderService orderService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, ENDPOINT_SECRET);
        } catch (Exception e) {
            System.out.println("❌ Webhook verification failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        System.out.println("📩 Received event: " + event.getType());

        JsonObject eventJson = JsonParser.parseString(payload).getAsJsonObject();
        JsonObject dataObject = eventJson.getAsJsonObject("data").getAsJsonObject("object");

        Long orderId = null;
        try {
            JsonObject metadata = dataObject.getAsJsonObject("metadata");
            if (metadata != null && metadata.has("orderId")) {
                orderId = Long.valueOf(metadata.get("orderId").getAsString());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Cannot extract orderId from metadata: " + e.getMessage());
        }

        if (orderId == null) {
            System.out.println("⚠️ Missing orderId in metadata.");
            return ResponseEntity.ok("Ignored");
        }

        try {
            switch (event.getType()) {
                case "payment_intent.succeeded" -> {
                    System.out.println("✅ Payment succeeded for order " + orderId);
                    orderService.updatePaymentStatus(orderId, PaymentStatus.PAID);
                }
                case "payment_intent.payment_failed", "charge.failed" -> {
                    System.out.println("❌ Payment failed for order " + orderId);
                    orderService.updatePaymentStatus(orderId, PaymentStatus.FAILED);
                }
                default -> System.out.println("ℹ️ Unhandled event: " + event.getType());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to update payment status for order " + orderId + ": " + e.getMessage());
        }

        return ResponseEntity.ok("Received");
    }
}
