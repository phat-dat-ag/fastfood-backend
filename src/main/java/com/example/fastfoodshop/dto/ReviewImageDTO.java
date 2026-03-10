package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.ReviewImage;

public record ReviewImageDTO(
        String imageUrl
) {
    public static ReviewImageDTO from(ReviewImage reviewImage) {
        return new ReviewImageDTO(
                reviewImage.getImageUrl()
        );
    }
}
