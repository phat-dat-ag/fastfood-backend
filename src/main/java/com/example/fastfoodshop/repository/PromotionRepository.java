package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Promotion;
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
}
