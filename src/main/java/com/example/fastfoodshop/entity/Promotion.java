package com.example.fastfoodshop.entity;

import com.example.fastfoodshop.entity.base.BaseAuditableEntity;
import com.example.fastfoodshop.enums.PromotionType;
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
@Table(name = "promotions")
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants(innerTypeName = "Field")
public class Promotion extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    PromotionType type;

    @Column(name = "value", nullable = false)
    int value;

    @Column(name = "start_at", nullable = false)
    LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    LocalDateTime endAt;

    @Column(name = "quantity", nullable = false)
    int quantity;

    @Column(name = "used_quantity", nullable = false)
    int usedQuantity;

    @Column(name = "max_discount_amount", nullable = false)
    int maxDiscountAmount;

    @Column(name = "min_spend_amount", nullable = false)
    int minSpendAmount;

    @Column(name = "code", length = 40, unique = true)
    String code;

    @Column(name = "is_global", nullable = false)
    boolean isGlobal = false;

    @Column(name = "is_activated", nullable = false)
    boolean isActivated = false;

    @Column(name = "is_deleted", nullable = false)
    boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true)
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    User user;

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Quiz> quizzes = new ArrayList<>();
}
