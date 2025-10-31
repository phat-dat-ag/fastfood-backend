package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct(Product product);
}
