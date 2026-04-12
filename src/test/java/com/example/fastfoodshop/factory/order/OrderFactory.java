package com.example.fastfoodshop.factory.order;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.OrderStatus;
import com.example.fastfoodshop.enums.PaymentMethod;
import com.example.fastfoodshop.enums.PaymentStatus;

import java.time.LocalDateTime;

public class OrderFactory {
    private static final int ORIGINAL_PRICE = 10000000;
    private static final int SUBTOTAL_PRICE = 10000000;
    private static final int DELIVERY_FEE = 8000;
    private static final int TOTAL_PRICE = 10008000;

    public static Order createpPendingOrder(User user, Long orderId) {
        LocalDateTime now = LocalDateTime.now();

        Order order = new Order();

        order.setUser(user);
        order.setId(orderId);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPlacedAt(now);
        order.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOriginalPrice(ORIGINAL_PRICE);
        order.setSubtotalPrice(SUBTOTAL_PRICE);
        order.setDeliveryFee(DELIVERY_FEE);
        order.setTotalPrice(TOTAL_PRICE);

        return order;
    }

    public static Order createDeliveredOrder(User user, Long orderId) {
        Order order = createpPendingOrder(user, orderId);

        order.setOrderStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());

        return order;
    }

    public static Order createCancelledOrder(User user, Long orderId) {
        Order order = createpPendingOrder(user, orderId);

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());

        return order;
    }

    public static Order createUnpaidCODAndPendingOrder(User user, Long orderId) {
        Order order = createpPendingOrder(user, orderId);

        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        order.setPaymentStatus(PaymentStatus.PENDING);

        return order;
    }

    public static Order createPaidCODAndPendingOrder(User user, Long orderId) {
        Order order = createpPendingOrder(user, orderId);

        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        order.setPaymentStatus(PaymentStatus.PAID);

        return order;
    }

    public static Order createUnpaidCODAndConfirmedOrder(User user, Long orderId) {
        Order order = createpPendingOrder(user, orderId);

        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        order.setPaymentStatus(PaymentStatus.PENDING);

        return order;
    }

    public static Order createUnpaidCODAndDeliveringOrder(User user, Long orderId) {
        Order order = createpPendingOrder(user, orderId);

        order.setOrderStatus(OrderStatus.DELIVERING);
        order.setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        order.setPaymentStatus(PaymentStatus.PENDING);

        return order;
    }

    public static Order createPaidOnlineAndPendingOrder(User user, Long orderId) {
        Order order = createpPendingOrder(user, orderId);

        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        order.setPaymentStatus(PaymentStatus.PAID);

        return order;
    }

    public static Order createPaidOnlineAndDeliveringOrder(User user, Long orderId) {
        Order order = createpPendingOrder(user, orderId);

        order.setOrderStatus(OrderStatus.DELIVERING);
        order.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        order.setPaymentStatus(PaymentStatus.PAID);

        return order;
    }

    public static Order createUnpaidOnlineAndPendingOrder(User user, Long orderId) {
        Order order = createpPendingOrder(user, orderId);

        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        order.setPaymentStatus(PaymentStatus.PENDING);

        return order;
    }
}