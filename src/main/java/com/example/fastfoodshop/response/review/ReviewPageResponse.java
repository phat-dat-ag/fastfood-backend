package com.example.fastfoodshop.response.review;

import com.example.fastfoodshop.dto.ReviewDTO;
import com.example.fastfoodshop.entity.Review;
import org.springframework.data.domain.Page;

import java.util.List;

public record ReviewPageResponse(
        List<ReviewDTO> reviews,
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages
) {
    public static ReviewPageResponse from(Page<Review> page) {
        List<ReviewDTO> reviews = page.getContent().stream().map(ReviewDTO::from).toList();

        return new ReviewPageResponse(
                reviews,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
