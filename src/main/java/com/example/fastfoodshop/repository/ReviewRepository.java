package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductAndIsDeletedFalse(Product product);

    Page<Review> findByIsDeletedFalse(Pageable pageable);

    @Query("""
                SELECT r
                FROM Review r
                WHERE r.product.id = :productId
                  AND r.isDeleted = false
                ORDER BY r.rating DESC, r.createdAt DESC
            """)
    List<Review> findTop5ByProductIdOrderByRatingDescCreatedAtDesc(
            @Param("productId") Long productId,
            Pageable pageable
    );
}
