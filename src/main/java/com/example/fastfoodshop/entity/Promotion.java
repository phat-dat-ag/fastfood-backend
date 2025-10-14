package com.example.fastfoodshop.entity;

import com.example.fastfoodshop.entity.base.BaseAuditableEntity;
import com.example.fastfoodshop.enums.PromotionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "promotions")
public class Promotion extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    private PromotionType type;

    @Column(name = "value", nullable = false)
    private int value;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "used_quantity", nullable = false)
    private int usedQuantity;

    @Column(name = "max_discount_amount", nullable = false)
    private int maxDiscountAmount;

    @Column(name = "min_spend_amount", nullable = false)
    private int minSpendAmount;

    @Column(name = "code", length = 40, unique = true)
    private String code;

    @Column(name = "is_global", nullable = false)
    private boolean isGlobal = false;

    @Column(name = "is_activated", nullable = false)
    private boolean isActivated = false;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
}
