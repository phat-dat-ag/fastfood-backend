package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySlug(String slug);

    ArrayList<Product> findByIsDeletedFalse();
}
