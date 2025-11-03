package com.example.fastfoodshop.entity;

import com.example.fastfoodshop.entity.base.BaseAuditableEntity;
import com.example.fastfoodshop.enums.PromotionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "awards")
public class Award extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    private PromotionType type;

    @Column(name = "min_value", nullable = false)
    private int minValue;

    @Column(name = "max_value", nullable = false)
    private int maxValue;

    @Column(name = "used_quantity", nullable = false)
    private int usedQuantity;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "max_discount_amount", nullable = false)
    private int maxDiscountAmount;

    @Column(name = "min_spend_amount", nullable = false)
    private int minSpendAmount;

    @Column(name = "is_activated", nullable = false)
    private boolean isActivated;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_difficulty_id", nullable = false)
    private TopicDifficulty topicDifficulty;
}
