package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.projection.ItemPromotionProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByCode(String code);

    Optional<Promotion> findByIdAndIsDeletedFalse(Long id);

    Page<Promotion> findByCategoryIsNotNullAndIsDeletedFalse(Pageable pageable);

    Page<Promotion> findByProductIsNotNullAndIsDeletedFalse(Pageable pageable);

    @Query("SELECT p FROM Promotion p WHERE p.isGlobal = true AND p.category IS NULL AND p.product IS NULL AND p.user IS NULL AND p.isDeleted = false")
    Page<Promotion> findGlobalOrderPromotions(Pageable pageable);

    @Query("""
            SELECT p
            FROM Promotion p
            WHERE
                p.isGlobal = true
                AND p.category IS NULL
                AND p.product IS NULL
                AND (p.user IS NULL OR p.user.id = :userId)
                AND p.isActivated = true
                AND p.isDeleted = false
                AND p.usedQuantity < p.quantity
                AND p.startAt <= :now
                AND p.endAt >= :now
            """)
    List<Promotion> findGlobalOrderPromotionsByUser(Long userId, LocalDateTime now);

    @Query(value = """
            SELECT
                c.name,
                c.image_url,
                p.type,
                p.value,
                p.code,
                p.start_at,
                p.end_at
            FROM categories c
            JOIN promotions p
                ON p.category_id = c.id
            WHERE c.is_activated = TRUE
              AND c.is_deleted = FALSE
              AND c.image_url IS NOT NULL
              AND p.id = (
                    SELECT MIN(p2.id)
                    FROM promotions p2
                    WHERE p2.category_id = c.id
                      AND p2.is_activated = TRUE
                      AND p2.is_deleted = FALSE
                      AND p2.is_global = FALSE
                      AND p2.start_at <= :now
                      AND p2.end_at >= :now
                )
              AND p.start_at <= :now
              AND p.end_at >= :now
            LIMIT 4
            """, nativeQuery = true)
    List<ItemPromotionProjection> getDisplayableCategoryPromotionsLimited4(LocalDateTime now);

    @Query(value = """
            SELECT
                p.name,
                p.image_url,
                promo.type,
                promo.value,
                promo.code,
                promo.start_at,
                promo.end_at
            FROM products p
            JOIN promotions promo
                ON promo.product_id = p.id
            WHERE p.is_activated = TRUE
              AND p.is_deleted = FALSE
              AND p.image_url IS NOT NULL
              AND promo.id = (
                    SELECT MIN(p2.id)
                    FROM promotions p2
                    WHERE p2.product_id = p.id
                      AND p2.is_activated = TRUE
                      AND p2.is_deleted = FALSE
                      AND p2.is_global = FALSE
                      AND p2.start_at <= :now
                      AND p2.end_at >= :now
                )
              AND promo.start_at <= :now
              AND promo.end_at >= :now
            LIMIT 4
            """, nativeQuery = true)
    List<ItemPromotionProjection> getDisplayableProductPromotionsLimited4(LocalDateTime now);
}
