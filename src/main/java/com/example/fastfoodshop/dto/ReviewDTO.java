package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Review;
import com.example.fastfoodshop.entity.ReviewImage;
import lombok.Data;

import java.util.ArrayList;

@Data
public class ReviewDTO {
    private Long productId;
    private Long id;
    private int rating;
    private String comment;
    private ArrayList<ReviewImageDTO> reviewImages = new ArrayList<>();

    public ReviewDTO(Review review) {
        this.productId = review.getProduct().getId();
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        for (ReviewImage reviewImage : review.getReviewImages()) {
            this.reviewImages.add(new ReviewImageDTO(reviewImage));
        }
    }
}
