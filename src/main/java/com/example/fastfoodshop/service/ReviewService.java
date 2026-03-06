package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.ReviewDTO;
import com.example.fastfoodshop.request.ReviewCreateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.ReviewResponse;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface ReviewService {
    ResponseEntity<ResponseWrapper<String>> createReviews(List<ReviewCreateRequest> reviewRequests, Long orderId);

    ResponseEntity<ResponseWrapper<ArrayList<ReviewDTO>>> getAllReviewsByProduct(Long productId);

    ResponseEntity<ResponseWrapper<ReviewResponse>> getAllReviewsByAdmin(int page, int size);

    ResponseEntity<ResponseWrapper<String>> deleteReview(Long reviewId);
}
