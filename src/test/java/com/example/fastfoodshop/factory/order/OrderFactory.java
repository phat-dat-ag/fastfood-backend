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

    public static Order createCancelledOrder(User user, Long orderId) {
        Order order = createpPendingOrder(user, orderId);

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());

        return order;
    }
}