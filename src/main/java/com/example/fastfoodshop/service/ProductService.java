package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.request.ProductCreateRequest;
import com.example.fastfoodshop.request.ProductGetByCategoryRequest;
import com.example.fastfoodshop.request.ProductUpdateRequest;
import com.example.fastfoodshop.response.ProductResponse;

import java.util.ArrayList;

public interface ProductService {
    Product findProductOrThrow(Long productId);

    void checkActivatedCategoryAndActivatedProduct(Long productId);

    ProductDTO createProduct(ProductCreateRequest productCreateRequest);

    ProductDTO updateProduct(ProductUpdateRequest productUpdateRequest);

    ProductResponse getAllProductsByCategory(ProductGetByCategoryRequest productGetByCategoryRequest);

    ArrayList<ProductDTO> getAllDisplayableProductsByCategory(String categorySlug);

    ArrayList<ProductDTO> getAllDisplayableProducts();

    ProductDTO getProductBySlug(String productSlug);

    String activateProduct(Long productId);

    String deactivateProduct(Long productId);

    ProductDTO deleteCategory(Long productId);
}
