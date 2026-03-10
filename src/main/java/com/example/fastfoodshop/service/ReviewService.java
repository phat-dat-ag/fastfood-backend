package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.ReviewDTO;
import com.example.fastfoodshop.request.ReviewCreateRequest;
import com.example.fastfoodshop.response.ReviewResponse;

import java.util.ArrayList;
import java.util.List;

public interface ReviewService {
    String createReviews(List<ReviewCreateRequest> reviewRequests, Long orderId);

    ArrayList<ReviewDTO> getAllReviewsByProduct(Long productId);

    ReviewResponse getAllReviewsByAdmin(int page, int size);

    String deleteReview(Long reviewId);
}
