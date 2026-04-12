package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.service.CartService;
import com.example.fastfoodshop.service.PaymentService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final CartService cartService;

    public String createPaymentIntent(Order order) throws StripeException {
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount((long) order.getTotalPrice())
                        .setCurrency("vnd")
                        .setDescription("Thanh toán đơn hàng")
                        .putMetadata("orderId", order.getId().toString())
                        .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return paymentIntent.getClientSecret();
    }
}