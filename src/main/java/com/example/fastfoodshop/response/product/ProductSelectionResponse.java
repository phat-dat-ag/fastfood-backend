package com.example.fastfoodshop.response.product;

import com.example.fastfoodshop.dto.ProductSelectionDTO;

import java.util.List;

public record ProductSelectionResponse(
        List<ProductSelectionDTO> selectiveProducts
) {
}
