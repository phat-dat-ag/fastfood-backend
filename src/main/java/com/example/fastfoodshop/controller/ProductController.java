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
public class ProductController extends BaseController {
    private final ProductService productService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<ProductDTO>> createProduct(
            @ModelAttribute ProductCreateRequest productCreateRequest
    ) {
        return okResponse(productService.createProduct(productCreateRequest));
    }

    @GetMapping()
    public ResponseEntity<ResponseWrapper<ProductResponse>> getAllProductsByCategory(
            @Valid @ModelAttribute ProductGetByCategoryRequest productGetByCategoryRequest
    ) {
        return okResponse(productService.getAllProductsByCategory(productGetByCategoryRequest));
    }

    @PutMapping()
    public ResponseEntity<ResponseWrapper<ProductDTO>> updateProduct(
            @ModelAttribute ProductUpdateRequest productUpdateRequest
    ) {
        return okResponse(productService.updateProduct(productUpdateRequest));
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseWrapper<String>> activateProduct(@RequestParam("id") Long productId) {
        return okResponse(productService.activateProduct(productId));
    }

    @PutMapping("deactivate")
    public ResponseEntity<ResponseWrapper<String>> deactivateProduct(@RequestParam("id") Long productId) {
        return okResponse(productService.deactivateProduct(productId));
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<ProductDTO>> deleteProduct(@RequestParam("id") Long productId) {
        return okResponse(productService.deleteCategory(productId));
    }

    @GetMapping("/display/all/by-category")
    public ResponseEntity<ResponseWrapper<ArrayList<ProductDTO>>> getAllDisplayableProductsByCategory(
            @RequestParam("categorySlug") String categorySlug
    ) {
        return okResponse(productService.getAllDisplayableProductsByCategory(categorySlug));
    }

    @GetMapping("/display/all")
    public ResponseEntity<ResponseWrapper<ArrayList<ProductDTO>>> getAllDisplayableProducts() {
        return okResponse(productService.getAllDisplayableProducts());
    }

    @GetMapping("/display/detail")
    public ResponseEntity<ResponseWrapper<ProductDTO>> getProductBySlug(
            @RequestParam("slug") String productSlug
    ) {
        return okResponse(productService.getProductBySlug(productSlug));
    }
}
