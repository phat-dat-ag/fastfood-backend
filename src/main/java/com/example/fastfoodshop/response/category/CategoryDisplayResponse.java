package com.example.fastfoodshop.response.category;

import com.example.fastfoodshop.dto.CategoryDTO;

import java.util.List;

public record CategoryDisplayResponse(
        List<CategoryDTO> displayableCategories
) {
}
