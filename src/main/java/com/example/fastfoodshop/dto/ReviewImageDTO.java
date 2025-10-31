package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.ReviewImage;
import lombok.Data;

@Data
public class ReviewImageDTO {
    private String imageUrl;

    public ReviewImageDTO(ReviewImage reviewImage) {
        this.imageUrl = reviewImage.getImageUrl();
    }
}
