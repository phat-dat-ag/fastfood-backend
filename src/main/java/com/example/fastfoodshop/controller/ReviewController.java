package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.ReviewForm;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.review.ReviewPageResponse;
import com.example.fastfoodshop.response.review.ReviewProductsResponse;
import com.example.fastfoodshop.response.review.ReviewUpdateResponse;
import com.example.fastfoodshop.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController extends BaseController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<ReviewUpdateResponse>> createReviews(
            @RequestParam("orderId") Long orderId,
            @Valid @ModelAttribute ReviewForm reviewsForm
    ) {
        return okResponse(reviewService.createReviews(reviewsForm.getReviews(), orderId));
    }

    @GetMapping
    ResponseEntity<ResponseWrapper<ReviewProductsResponse>> getAllReviewsByProduct(
            @RequestParam("productId") Long productId
    ) {
        return okResponse(reviewService.getAllReviewsByProduct(productId));
    }

    @GetMapping("/manage")
    ResponseEntity<ResponseWrapper<ReviewPageResponse>> getAllReviewsByAdmin(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(reviewService.getAllReviewsByAdmin(request.getPage(), request.getSize()));
    }

    @DeleteMapping("/manage")
    ResponseEntity<ResponseWrapper<ReviewUpdateResponse>> deleteReview(@RequestParam("reviewId") Long reviewId) {
        return okResponse(reviewService.deleteReview(reviewId));
    }
}
