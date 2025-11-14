package com.example.fastfoodshop.projection;

public interface ProductRatingStatsProjection {
    Long getProductId();

    Double getAvgRating();

    Long getReviewCount();
}
