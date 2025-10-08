package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsBySlug(String slug);

    List<Category> findByIsDeletedFalse();
}
