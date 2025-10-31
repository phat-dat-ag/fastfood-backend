package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.ReviewForm;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<String>> createReviews(
            @RequestParam("orderId") Long orderId,
            @Valid @ModelAttribute ReviewForm reviewsForm
    ) {
        return reviewService.createReviews(reviewsForm.getReviews(), orderId);
    }
}



