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
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "awards")
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants(innerTypeName = "Field")
public class Award extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    PromotionType type;

    @Column(name = "min_value", nullable = false)
    int minValue;

    @Column(name = "max_value", nullable = false)
    int maxValue;

    @Column(name = "used_quantity", nullable = false)
    int usedQuantity;

    @Column(name = "quantity", nullable = false)
    int quantity;

    @Column(name = "max_discount_amount", nullable = false)
    int maxDiscountAmount;

    @Column(name = "min_spend_amount", nullable = false)
    int minSpendAmount;

    @Column(name = "is_activated", nullable = false)
    boolean isActivated;

    @Column(name = "is_deleted", nullable = false)
    boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_difficulty_id", nullable = false)
    TopicDifficulty topicDifficulty;
}
