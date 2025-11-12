package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySlug(String slug);

    Optional<Product> findBySlug(String slug);

    Page<Product> findByCategoryAndIsDeletedFalse(Category category, Pageable pageable);

    List<Product> findByCategoryAndIsDeletedFalseAndIsActivatedTrue(Category category);

    List<Product> findByIsDeletedFalseAndIsActivatedTrue();
}
