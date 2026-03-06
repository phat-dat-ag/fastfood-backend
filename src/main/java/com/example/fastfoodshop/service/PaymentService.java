package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Order;
import com.stripe.exception.StripeException;

public interface PaymentService {
    String createPaymentIntent(int totalPrice, Order order) throws StripeException;
}

