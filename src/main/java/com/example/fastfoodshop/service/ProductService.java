package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.response.ProductResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public interface ProductService {
    Product findProductOrThrow(Long id);

    void checkActivatedCategoryAndActivatedProduct(Long productId);

    ResponseEntity<ResponseWrapper<ProductDTO>> createProduct(
            Long category_id, String name, String description, int price, boolean activated,
            MultipartFile imageFile, MultipartFile modelFile
    );

    ResponseEntity<ResponseWrapper<ProductDTO>> updateProduct(
            Long id, String name, String description, boolean isActivated,
            MultipartFile imageFile, MultipartFile modelFile
    );

    ResponseEntity<ResponseWrapper<ProductResponse>> getAllProductsByCategory(
            String categorySlug, int page, int size
    );

    ResponseEntity<ResponseWrapper<ArrayList<ProductDTO>>> getAllDisplayableProductsByCategory(String categorySlug);

    ResponseEntity<ResponseWrapper<ArrayList<ProductDTO>>> getAllDisplayableProducts();

    ResponseEntity<ResponseWrapper<ProductDTO>> getProductBySlug(String slug);

    ResponseEntity<ResponseWrapper<String>> activateProduct(Long id);

    ResponseEntity<ResponseWrapper<String>> deactivateProduct(Long id);

    ResponseEntity<ResponseWrapper<ProductDTO>> deleteCategory(Long id);
}
