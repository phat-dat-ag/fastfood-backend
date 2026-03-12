package com.example.fastfoodshop.response.product;

import com.example.fastfoodshop.dto.ProductDTO;

import java.util.List;

public record ProductDisplayResponse(
        List<ProductDTO> displayableProducts
) {
}
