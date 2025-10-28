package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.enums.OrderStatus;
import com.example.fastfoodshop.enums.PaymentMethod;
import com.example.fastfoodshop.enums.PaymentStatus;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDTO {
    private Long id;
    private OrderStatus orderStatus;
    private LocalDateTime placedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime deliveringAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private int originalPrice;
    private int subtotalPrice;
    private int deliveryFee;
    private int totalPrice;
    private AddressDTO address;
    private UserDTO user;
    private String clientSecret;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.orderStatus = order.getOrderStatus();
        this.placedAt = order.getPlacedAt();
        this.confirmedAt = order.getConfirmedAt();
        this.deliveringAt = order.getDeliveringAt();
        this.deliveredAt = order.getDeliveredAt();
        this.cancelledAt = order.getCancelledAt();
        this.paymentMethod = order.getPaymentMethod();
        this.paymentStatus = order.getPaymentStatus();
        this.originalPrice = order.getOriginalPrice();
        this.subtotalPrice = order.getSubtotalPrice();
        this.deliveryFee = order.getDeliveryFee();
        this.totalPrice = order.getTotalPrice();
        this.address = order.getAddress() != null ? new AddressDTO(order.getAddress()) : null;
        this.user = order.getUser() != null ? new UserDTO(order.getUser()) : null;
    }
}
