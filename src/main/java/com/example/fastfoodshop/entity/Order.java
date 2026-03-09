package com.example.fastfoodshop.entity;

import com.example.fastfoodshop.entity.base.BaseCreatedEntity;
import com.example.fastfoodshop.enums.OrderStatus;
import com.example.fastfoodshop.enums.PaymentMethod;
import com.example.fastfoodshop.enums.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants(innerTypeName = "Field")
public class Order extends BaseCreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 20, nullable = false)
    OrderStatus orderStatus;

    @Column(name = "placed_at", nullable = false)
    LocalDateTime placedAt;

    @Column(name = "confirmed_at")
    LocalDateTime confirmedAt;

    @Column(name = "delivering_at")
    LocalDateTime deliveringAt;

    @Column(name = "delivered_at")
    LocalDateTime deliveredAt;

    @Column(name = "cancelled_at")
    LocalDateTime cancelledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20, nullable = false)
    PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20, nullable = false)
    PaymentStatus paymentStatus;

    @Column(name = "original_price", nullable = false)
    int originalPrice;

    @Column(name = "subtotal_price", nullable = false)
    int subtotalPrice;

    @Column(name = "delivery_fee", nullable = false)
    int deliveryFee;

    @Column(name = "total_price", nullable = false)
    int totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<OrderNote> orderNotes = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<OrderDetail> orderDetails = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Review> reviews = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    Promotion promotion;
}
