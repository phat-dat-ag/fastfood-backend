package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.projection.ProductRatingStatsProjection;
import com.example.fastfoodshop.projection.ProductSoldCountProjection;
import com.example.fastfoodshop.projection.ProductStatsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySlug(String slug);

    Optional<Product> findBySlug(String slug);

    Page<Product> findByCategoryAndIsDeletedFalse(Category category, Pageable pageable);

    List<Product> findByCategoryAndIsDeletedFalseAndIsActivatedTrue(Category category);

    List<Product> findByIsDeletedFalseAndIsActivatedTrue();

    @Query("""
                SELECT r.product.id AS productId,
                       AVG(r.rating) AS avgRating,
                       COUNT(r.id) AS reviewCount
                FROM Review r
                WHERE r.product.id IN :productIds
                  AND r.isDeleted = false
                GROUP BY r.product.id
            """)
    List<ProductRatingStatsProjection> getRatingStatsByProductIds(@Param("productIds") List<Long> productIds);

    @Query("""
                SELECT p.id AS productId,
                       COUNT(od.id) AS soldCount
                FROM Product p
                JOIN p.orderDetails od
                JOIN od.order o
                WHERE p.id IN :productIds
                  AND o.deliveredAt IS NOT NULL
                GROUP BY p.id
            """)
    List<ProductSoldCountProjection> getSoldCountByProductIds(@Param("productIds") List<Long> productIds);

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
