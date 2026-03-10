package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Review;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public record ReviewDTO(
        String userName,
        String userAvatar,
        Long productId,
        Long id,
        int rating,
        String comment,
        List<ReviewImageDTO> reviewImages,
        LocalDateTime createdAt
) {
    public static ReviewDTO from(Review review) {
        List<ReviewImageDTO> reviewImages = review.getReviewImages().stream().map(ReviewImageDTO::from).toList();
        return new ReviewDTO(
                review.getOrder().getUser().getName(),
                review.getOrder().getUser().getAvatarUrl(),
                review.getProduct().getId(),
                review.getId(),
                review.getRating(),
                review.getComment(),
                reviewImages,
                review.getCreatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );
    }
}
