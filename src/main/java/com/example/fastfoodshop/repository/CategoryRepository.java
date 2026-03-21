package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.projection.CategoryStatsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsBySlug(String slug);

    Page<Category> findByIsDeletedFalse(Pageable pageable);

    List<Category> findByIsDeletedFalseAndIsActivatedTrue();

    Optional<Category> findBySlug(String slug);

    @Query(value = """
            SELECT c.name, SUM(od.discounted_price * od.quantity) as totalRevenue, SUM(od.quantity) as totalQuantitySold
            FROM categories c
            JOIN products p ON p.category_id = c.id
            JOIN order_details od ON od.product_id = p.id
            JOIN orders o ON od.order_id = o.id
            WHERE o.delivered_at IS NOT NULL
            GROUP BY c.id
            ORDER BY totalRevenue DESC;
            """, nativeQuery = true)
    List<CategoryStatsProjection> getStats();
}
