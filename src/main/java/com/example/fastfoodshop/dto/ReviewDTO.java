package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Review;
import com.example.fastfoodshop.entity.ReviewImage;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

@Data
public class ReviewDTO {
    private String userName;
    private String userAvatar;
    private Long productId;
    private Long id;
    private int rating;
    private String comment;
    private ArrayList<ReviewImageDTO> reviewImages = new ArrayList<>();
    private LocalDateTime createdAt;

    public ReviewDTO(Review review) {
        this.userName = review.getOrder().getUser().getName();
        this.userAvatar = review.getOrder().getUser().getAvatarUrl();
        this.productId = review.getProduct().getId();
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        for (ReviewImage reviewImage : review.getReviewImages()) {
            this.reviewImages.add(new ReviewImageDTO(reviewImage));
        }
        this.createdAt = review.getCreatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
