package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Order;
import com.stripe.exception.StripeException;

public interface PaymentService {
    String createPaymentIntent(Order order) throws StripeException;
}