package com.example.fastfoodshop.service;

import com.example.fastfoodshop.request.ReviewCreateRequest;
import com.example.fastfoodshop.response.review.ReviewPageResponse;
import com.example.fastfoodshop.response.review.ReviewProductsResponse;
import com.example.fastfoodshop.response.review.ReviewUpdateResponse;

import java.util.List;

public interface ReviewService {
    ReviewUpdateResponse createReviews(List<ReviewCreateRequest> reviewRequests, Long orderId);

    ReviewProductsResponse getAllReviewsByProduct(Long productId);

    ReviewPageResponse getAllReviewsByAdmin(int page, int size);

    ReviewUpdateResponse deleteReview(Long reviewId);
}
