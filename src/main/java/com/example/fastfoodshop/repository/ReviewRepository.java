package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
