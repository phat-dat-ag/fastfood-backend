package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.enums.PaymentStatus;
import com.example.fastfoodshop.dto.OrderDTO;
import com.example.fastfoodshop.request.OrderCancelRequest;
import com.example.fastfoodshop.request.OrderCreateRequest;
import com.example.fastfoodshop.response.OrderResponse;

public interface OrderService {
    Order findOrderOrThrow(Long orderId);

    OrderDTO createCashOnDeliveryOrder(String phone, OrderCreateRequest orderCreateRequest);

    OrderDTO createStripePaymentOrder(String phone, OrderCreateRequest orderCreateRequest);

    OrderDTO getPaymentIntent(String phone, Long orderId);

    OrderDTO getOrder(String phone, Long orderId);

    OrderResponse getAllUnfinishedOrders(int page, int size);

    OrderDTO getUnfinishedOrder(Long orderId);

    Order updatePaymentStatus(Long orderId, PaymentStatus paymentStatus);

    OrderDTO confirmOrder(Long orderId);

    OrderDTO markAsDelivering(Long orderId);

    OrderDTO markAsDelivered(Long orderId);

    OrderResponse getAllActiveOrders(String phone, int page, int size);

    OrderDTO getActiveOrder(Long orderId, String phone);

    OrderResponse getAllOrderHistory(String phone, int page, int size);

    OrderDTO getOrderHistory(Long orderId, String phone);

    OrderDTO cancelOrderByUser(Long orderId, OrderCancelRequest orderCancelRequest);

    OrderDTO cancelOrderByStaff(Long orderId, OrderCancelRequest orderCancelRequest);

    OrderResponse getAllOrdersByAdmin(int page, int size);
}
