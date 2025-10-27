package com.example.fastfoodshop.service;

import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final CartService cartService;

    public String createPaymentIntent(int totalPrice) throws StripeException {
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount((long) totalPrice)
                        .setCurrency("vnd")
                        .setDescription("Thanh toán đơn hàng")
                        .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return paymentIntent.getClientSecret();
    }
}

