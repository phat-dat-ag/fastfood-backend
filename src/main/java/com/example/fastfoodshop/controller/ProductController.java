package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.response.product.ProductDisplayResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.product.ProductResponse;
import com.example.fastfoodshop.response.review.ReviewProductsResponse;
import com.example.fastfoodshop.service.ProductService;
import com.example.fastfoodshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController extends BaseController {
    private final ProductService productService;
    private final ReviewService reviewService;

    @GetMapping("/category/{categorySlug}")
    public ResponseEntity<ResponseWrapper<ProductDisplayResponse>> getDisplayableProducts(
            @PathVariable("categorySlug") String categorySlug
    ) {
        return okResponse(productService.getAllDisplayableProducts(categorySlug));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ResponseWrapper<ProductResponse>> getProductsBySlug(
            @PathVariable("slug") String productSlug
    ) {
        return okResponse(productService.getProductBySlug(productSlug));
    }

    @GetMapping("/{id}/reviews")
    ResponseEntity<ResponseWrapper<ReviewProductsResponse>> getAllReviewsByProduct(
            @PathVariable("id") Long productId
    ) {
        return okResponse(reviewService.getAllReviewsByProduct(productId));
    }
}
