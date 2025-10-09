package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.request.ProductCreateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<ProductDTO>> createProduct(@ModelAttribute ProductCreateRequest req) {
        return productService.createProduct(req.getCategory_id(), req.getName(), req.getDescription(), req.getPrice(), req.isActivated(), req.getProductImageUrl());
    }

    @GetMapping()
    public ResponseEntity<ResponseWrapper<ArrayList<ProductDTO>>> getProducts() {
        return productService.getProducts();
    }
}
