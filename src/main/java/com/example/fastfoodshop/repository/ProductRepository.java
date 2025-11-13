package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.projection.ProductStatsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySlug(String slug);

    Optional<Product> findBySlug(String slug);

    Page<Product> findByCategoryAndIsDeletedFalse(Category category, Pageable pageable);

    List<Product> findByCategoryAndIsDeletedFalseAndIsActivatedTrue(Category category);

    List<Product> findByIsDeletedFalseAndIsActivatedTrue();

    @Query(value = """
            SELECT p.name, SUM(od.discounted_price * od.quantity) as totalRevenue, SUM(od.quantity) as totalQuantitySold
            FROM products p
            JOIN order_details od ON od.product_id = p.id
            JOIN orders o ON od.order_id = o.id
            WHERE o.delivered_at IS NOT NULL
            GROUP BY p.id
            ORDER BY totalRevenue DESC;
            """, nativeQuery = true)
    List<ProductStatsProjection> getStats();
}
