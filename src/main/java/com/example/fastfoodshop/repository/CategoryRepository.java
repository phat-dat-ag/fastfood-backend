package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsBySlug(String slug);

    Page<Category> findByIsDeletedFalse(Pageable pageable);

    Optional<Category> findBySlug(String slug);
}
