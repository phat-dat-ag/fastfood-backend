package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.request.ProductCreateRequest;
import com.example.fastfoodshop.request.ProductUpdateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<ProductDTO>> createProduct(@ModelAttribute ProductCreateRequest req) {
        return productService.createProduct(
                req.getCategory_id(), req.getName(), req.getDescription(), req.getPrice(), req.isActivated(),
                req.getImageUrl(), req.getModelUrl()
        );
    }

    @GetMapping()
    public ResponseEntity<ResponseWrapper<ArrayList<ProductDTO>>> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/detail")
    public ResponseEntity<ResponseWrapper<ProductDTO>> getProductBySlug(
            @RequestParam("slug") String slug
    ) {
        return productService.getProductBySlug(slug);
    }

    @PutMapping()
    public ResponseEntity<ResponseWrapper<ProductDTO>> updateProduct(@ModelAttribute ProductUpdateRequest req) {
        return productService.updateProduct(
                req.getId(), req.getName(), req.getDescription(), req.isActivated(),
                req.getImageUrl(), req.getModelUrl()
        );
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<ProductDTO>> deleteProduct(@RequestParam("id") Long id) {
        return productService.deleteCategory(id);
    }
}
