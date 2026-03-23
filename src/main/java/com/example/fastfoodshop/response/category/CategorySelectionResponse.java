package com.example.fastfoodshop.response.category;

import com.example.fastfoodshop.dto.CategorySelectionDTO;

import java.util.List;

public record CategorySelectionResponse(
        List<CategorySelectionDTO> selectiveCategories
) {
}
