package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.ProductCreateRequest;
import com.example.fastfoodshop.request.ProductGetByCategoryRequest;
import com.example.fastfoodshop.request.ProductUpdateRequest;
import com.example.fastfoodshop.request.UpdateActivationRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.product.ProductSelectionResponse;
import com.example.fastfoodshop.response.product.ProductStatsResponse;
import com.example.fastfoodshop.response.product.ProductPageResponse;
import com.example.fastfoodshop.response.product.ProductResponse;
import com.example.fastfoodshop.response.product.ProductUpdateResponse;
import com.example.fastfoodshop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController extends BaseController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<ProductResponse>> createProduct(
            @Valid @ModelAttribute ProductCreateRequest productCreateRequest
    ) {
        return okResponse(productService.createProduct(productCreateRequest));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<ProductPageResponse>> getProductPage(
            @Valid @ModelAttribute ProductGetByCategoryRequest productGetByCategoryRequest
    ) {
        return okResponse(productService.getProductPage(productGetByCategoryRequest));
    }

    @GetMapping("/selection")
    public ResponseEntity<ResponseWrapper<ProductSelectionResponse>> getProductSelections() {
        return okResponse(productService.getProductSelections());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ProductResponse>> updateProduct(
            @PathVariable("id") Long productId,
            @Valid @ModelAttribute ProductUpdateRequest productUpdateRequest
    ) {
        return okResponse(productService.updateProduct(productId, productUpdateRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ProductUpdateResponse>> updateProductActivation(
            @PathVariable("id") Long productId,
            @RequestBody @Valid UpdateActivationRequest updateActivationRequest
    ) {
        return okResponse(productService.updateProductActivation(
                productId, updateActivationRequest.activated()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ProductUpdateResponse>> deleteProduct(
            @PathVariable("id") Long productId
    ) {
        return okResponse(productService.deleteProduct(productId));
    }

    @GetMapping("/stats")
    public ResponseEntity<ResponseWrapper<ProductStatsResponse>> getProductStats() {
        return okResponse(productService.getProductStats());
    }
}
