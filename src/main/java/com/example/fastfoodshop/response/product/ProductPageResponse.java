package com.example.fastfoodshop.response.product;

import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public record ProductPageResponse(
        List<ProductDTO> products,
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages
) {
    public static ProductPageResponse from(Page<Product> page) {
        List<ProductDTO> products = page.getContent().stream().map(ProductDTO::from).toList();

        return new ProductPageResponse(
                products,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
