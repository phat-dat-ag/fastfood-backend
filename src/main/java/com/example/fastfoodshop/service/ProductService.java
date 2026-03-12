package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.request.ProductCreateRequest;
import com.example.fastfoodshop.request.ProductGetByCategoryRequest;
import com.example.fastfoodshop.request.ProductUpdateRequest;
import com.example.fastfoodshop.response.product.ProductDisplayResponse;
import com.example.fastfoodshop.response.product.ProductPageResponse;
import com.example.fastfoodshop.response.product.ProductResponse;
import com.example.fastfoodshop.response.product.ProductUpdateResponse;

public interface ProductService {
    Product findProductOrThrow(Long productId);

    void checkActivatedCategoryAndActivatedProduct(Long productId);

    ProductResponse createProduct(ProductCreateRequest productCreateRequest);

    ProductResponse updateProduct(ProductUpdateRequest productUpdateRequest);

    ProductPageResponse getAllProductsByCategory(ProductGetByCategoryRequest productGetByCategoryRequest);

    ProductDisplayResponse getAllDisplayableProductsByCategory(String categorySlug);

    ProductDisplayResponse getAllDisplayableProducts();

    ProductResponse getProductBySlug(String productSlug);

    ProductUpdateResponse activateProduct(Long productId);

    ProductUpdateResponse deactivateProduct(Long productId);

    ProductUpdateResponse deleteCategory(Long productId);
}
