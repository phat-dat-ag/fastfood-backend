package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.exception.category.CategoryNotFoundException;
import com.example.fastfoodshop.factory.category.CategoryFactory;
import com.example.fastfoodshop.repository.CategoryRepository;
import com.example.fastfoodshop.service.implementation.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryServiceImpl categoryService;

    private static final Long CATEGORY_ID = 888L;
    private static final String CATEGORY_SLUG = "Tra-sua";

    @Test
    void findCategoryByIdOrThrow_existingCategory_shouldReturnCategory() {
        Category category = CategoryFactory.createActivatedCategory(CATEGORY_ID);

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

        Category categoryResponse = categoryService.findCategoryByIdOrThrow(CATEGORY_ID);

        assertNotNull(categoryResponse);

        assertEquals(CATEGORY_ID, categoryResponse.getId());

        verify(categoryRepository).findById(CATEGORY_ID);
    }

    @Test
    void findCategoryByIdOrThrow_notFoundCategory_shouldThrowCategoryNotFoundException() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.findCategoryByIdOrThrow(CATEGORY_ID)
        );

        verify(categoryRepository).findById(CATEGORY_ID);
    }

    @Test
    void findCategorySlugIdOrThrow_existingCategory_shouldReturnCategory() {
        Category category = CategoryFactory.createActivatedCategory(CATEGORY_ID);

        when(categoryRepository.findBySlug(CATEGORY_SLUG)).thenReturn(Optional.of(category));

        Category categoryResponse = categoryService.findCategoryBySlugOrThrow(CATEGORY_SLUG);

        assertNotNull(categoryResponse);

        assertEquals(CATEGORY_ID, categoryResponse.getId());

        verify(categoryRepository).findBySlug(CATEGORY_SLUG);
    }

    @Test
    void findCategoryBySlugOrThrow_notFoundCategory_shouldThrowCategoryNotFoundException() {
        when(categoryRepository.findBySlug(CATEGORY_SLUG)).thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.findCategoryBySlugOrThrow(CATEGORY_SLUG)
        );

        verify(categoryRepository).findBySlug(CATEGORY_SLUG);
    }
}