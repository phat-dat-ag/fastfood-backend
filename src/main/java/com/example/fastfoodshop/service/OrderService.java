package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.enums.PaymentStatus;
import com.example.fastfoodshop.request.OrderCreateRequest;
import com.example.fastfoodshop.request.OrderStatusUpdateRequest;
import com.example.fastfoodshop.response.order.OrderPageResponse;
import com.example.fastfoodshop.response.order.OrderResponse;
import com.example.fastfoodshop.response.order.OrderUpdateResponse;

public interface OrderService {
    Order findOrderOrThrow(Long orderId);

    OrderResponse createCashOnDeliveryOrder(String phone, OrderCreateRequest orderCreateRequest);

    OrderResponse createStripePaymentOrder(String phone, OrderCreateRequest orderCreateRequest);

    OrderResponse getPaymentIntent(String phone, Long orderId);

    OrderResponse getOrder(String phone, Long orderId);

    OrderPageResponse getAllUnfinishedOrders(int page, int size);

    OrderResponse getUnfinishedOrder(Long orderId);

    void updatePaymentStatus(Long orderId, PaymentStatus paymentStatus);

    OrderUpdateResponse updateStatus(Long orderId, String phone, OrderStatusUpdateRequest request);

    OrderPageResponse getAllActiveOrders(String phone, int page, int size);

    OrderResponse getActiveOrder(Long orderId, String phone);

    OrderPageResponse getAllOrderHistory(String phone, int page, int size);

    OrderResponse getOrderHistory(Long orderId, String phone);

    OrderPageResponse getAllOrdersByAdmin(int page, int size);
}
