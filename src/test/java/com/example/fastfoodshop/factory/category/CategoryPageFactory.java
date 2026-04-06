package com.example.fastfoodshop.factory.category;

import com.example.fastfoodshop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

public class CategoryPageFactory {
    public static Page<Category> createCategoryPage() {
        return new PageImpl<>(
                CategoryFactory.createDisplayableCategories()
        );
    }

    public static Page<Category> createEmptyCategoryPage() {
        return new PageImpl<>(
                List.of()
        );
    }
}
