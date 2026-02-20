package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.request.ProductCreateRequest;
import com.example.fastfoodshop.request.ProductGetByCategoryRequest;
import com.example.fastfoodshop.request.ProductUpdateRequest;
import com.example.fastfoodshop.response.ProductResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;

@RestController
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
    public ResponseEntity<ResponseWrapper<ProductResponse>> getAllProductsByCategory(
            @Valid @ModelAttribute ProductGetByCategoryRequest request
    ) {
        return productService.getAllProductsByCategory(request.getCategorySlug(), request.getPage(), request.getSize());
    }

    @PutMapping()
    public ResponseEntity<ResponseWrapper<ProductDTO>> updateProduct(@ModelAttribute ProductUpdateRequest req) {
        return productService.updateProduct(
                req.getId(), req.getName(), req.getDescription(), req.isActivated(),
                req.getImageUrl(), req.getModelUrl()
        );
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseWrapper<String>> activateProduct(@RequestParam("id") Long id) {
        return productService.activateProduct(id);
    }

    @PutMapping("deactivate")
    public ResponseEntity<ResponseWrapper<String>> deactivateProduct(@RequestParam("id") Long id) {
        return productService.deactivateProduct(id);
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<ProductDTO>> deleteProduct(@RequestParam("id") Long id) {
        return productService.deleteCategory(id);
    }

    @GetMapping("/display/all/by-category")
    public ResponseEntity<ResponseWrapper<ArrayList<ProductDTO>>> getAllDisplayableProductsByCategory(
            @RequestParam("categorySlug") String categorySlug
    ) {
        return productService.getAllDisplayableProductsByCategory(categorySlug);
    }

    @GetMapping("/display/all")
    public ResponseEntity<ResponseWrapper<ArrayList<ProductDTO>>> getAllDisplayableProducts() {
        return productService.getAllDisplayableProducts();
    }

    @GetMapping("/display/detail")
    public ResponseEntity<ResponseWrapper<ProductDTO>> getProductBySlug(
            @RequestParam("slug") String slug
    ) {
        return productService.getProductBySlug(slug);
    }
}
