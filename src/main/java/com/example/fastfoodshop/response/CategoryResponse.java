package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.CategoryDTO;
import com.example.fastfoodshop.entity.Category;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryResponse {
    List<CategoryDTO> categories = new ArrayList<>();
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public CategoryResponse(Page<Category> page) {
        for (Category category : page.getContent()) {
            this.categories.add(new CategoryDTO(category));
        }
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
        this.totalItems = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
