package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.review.ReviewPageResponse;
import com.example.fastfoodshop.response.review.ReviewUpdateResponse;
import com.example.fastfoodshop.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController extends BaseController {
    private final ReviewService reviewService;

    @GetMapping
    ResponseEntity<ResponseWrapper<ReviewPageResponse>> getAllReviewsByAdmin(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(reviewService.getAllReviewsByAdmin(request.getPage(), request.getSize()));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseWrapper<ReviewUpdateResponse>> deleteReview(
            @PathVariable("id") Long reviewId
    ) {
        return okResponse(reviewService.deleteReview(reviewId));
    }
}
