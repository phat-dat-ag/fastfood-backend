package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.response.product.ProductDisplayResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.product.ProductResponse;
import com.example.fastfoodshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController extends BaseController {
    private final ProductService productService;

    @GetMapping("/category/{categorySlug}")
    public ResponseEntity<ResponseWrapper<ProductDisplayResponse>> getDisplayableProducts(
            @PathVariable("categorySlug") String categorySlug
    ) {
        return okResponse(productService.getAllDisplayableProducts(categorySlug));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ResponseWrapper<ProductResponse>> getProductsBySlug(
            @PathVariable("slug") String productSlug
    ) {
        return okResponse(productService.getProductBySlug(productSlug));
    }
}
