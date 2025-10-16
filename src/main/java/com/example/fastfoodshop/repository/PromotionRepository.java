package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByCode(String code);

    List<Promotion> findByCategoryIsNotNullAndIsDeletedFalse();

    List<Promotion> findByProductIsNotNullAndIsDeletedFalse();
}
