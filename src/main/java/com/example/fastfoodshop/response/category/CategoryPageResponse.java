package com.example.fastfoodshop.response.category;

import com.example.fastfoodshop.dto.CategoryDTO;
import com.example.fastfoodshop.entity.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public record CategoryPageResponse(
        List<CategoryDTO> categories,
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages
) {
    public static CategoryPageResponse from(Page<Category> page) {
        List<CategoryDTO> categories = page.getContent().stream().map(CategoryDTO::from).toList();
        return new CategoryPageResponse(
                categories,
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalPages()
        );
    }
}
