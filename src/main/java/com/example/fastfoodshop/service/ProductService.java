package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.request.ProductCreateRequest;
import com.example.fastfoodshop.request.ProductGetByCategoryRequest;
import com.example.fastfoodshop.request.ProductUpdateRequest;
import com.example.fastfoodshop.response.product.ProductDisplayResponse;
import com.example.fastfoodshop.response.product.ProductPageResponse;
import com.example.fastfoodshop.response.product.ProductResponse;
import com.example.fastfoodshop.response.product.ProductUpdateResponse;
import com.example.fastfoodshop.response.product.ProductSelectionResponse;
import com.example.fastfoodshop.response.product.ProductStatsResponse;

import java.util.List;

public interface ProductService {
    List<Product> findAllByIds(List<Long> productIds);

    Product findProductOrThrow(Long productId);

    void checkActivatedCategoryAndActivatedProduct(Long productId);

    ProductResponse createProduct(ProductCreateRequest productCreateRequest);

    ProductResponse updateProduct(Long productId, ProductUpdateRequest productUpdateRequest);

    ProductPageResponse getProductPage(ProductGetByCategoryRequest productGetByCategoryRequest);

    ProductSelectionResponse getProductSelections();

    ProductUpdateResponse updateProductActivation(Long productId, boolean activated);

    ProductUpdateResponse deleteProduct(Long productId);

    ProductDisplayResponse getAllDisplayableProducts(String categorySlug);

    ProductResponse getProductBySlug(String productSlug);

    ProductStatsResponse getProductStats();
}
