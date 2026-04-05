package com.example.fastfoodshop.service;

import com.example.fastfoodshop.constant.FolderNameConstants;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.exception.category.CategoryNotFoundException;
import com.example.fastfoodshop.exception.category.DeletedCategoryException;
import com.example.fastfoodshop.exception.category.InvalidCategoryStatusException;
import com.example.fastfoodshop.factory.category.CategoryCreateRequestFactory;
import com.example.fastfoodshop.factory.category.CategoryFactory;
import com.example.fastfoodshop.repository.CategoryRepository;
import com.example.fastfoodshop.request.CategoryCreateRequest;
import com.example.fastfoodshop.response.category.CategoryResponse;
import com.example.fastfoodshop.response.category.CategoryUpdateResponse;
import com.example.fastfoodshop.service.implementation.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @Mock
    CategoryRepository categoryRepository;

    @Mock
    CloudinaryService cloudinaryService;

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

    @Test
    void createCategory_validRequest_shouldReturnCategoryResponse() {
        when(cloudinaryService.uploadImage(
                any(MultipartFile.class), eq(FolderNameConstants.categoryFolderName)
        )).thenReturn(new HashMap<>());

        CategoryCreateRequest validRequest = CategoryCreateRequestFactory.createValidWithImageFile();

        Category category = CategoryFactory.createActivatedCategory(CATEGORY_ID);

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse categoryResponse = categoryService.createCategory(validRequest);

        assertNotNull(categoryResponse);
        assertNotNull(categoryResponse.category());

        assertEquals(CATEGORY_ID, categoryResponse.category().id());

        verify(cloudinaryService).uploadImage(
                any(MultipartFile.class), eq(FolderNameConstants.categoryFolderName)
        );

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_duplicatedSlug_shouldReturnCategoryResponse() {
        Category category = CategoryFactory.createActivatedCategory(CATEGORY_ID);

        when(categoryRepository.existsBySlug(anyString())).thenReturn(true).thenReturn(false);

        when(cloudinaryService.uploadImage(
                any(MultipartFile.class), eq(FolderNameConstants.categoryFolderName)
        )).thenReturn(new HashMap<>());

        CategoryCreateRequest validRequest = CategoryCreateRequestFactory.createValidWithImageFile();

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse categoryResponse = categoryService.createCategory(validRequest);

        assertNotNull(categoryResponse);
        assertNotNull(categoryResponse.category());

        assertEquals(CATEGORY_ID, categoryResponse.category().id());

        verify(categoryRepository, times(2)).existsBySlug(anyString());

        verify(cloudinaryService).uploadImage(
                any(MultipartFile.class), eq(FolderNameConstants.categoryFolderName)
        );

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_nullImageFile_shouldReturnCategoryResponse() {
        CategoryCreateRequest validRequest = CategoryCreateRequestFactory.createValidWithNullImageFile();

        Category category = CategoryFactory.createActivatedCategory(CATEGORY_ID);

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse categoryResponse = categoryService.createCategory(validRequest);

        assertNotNull(categoryResponse);
        assertNotNull(categoryResponse.category());

        assertEquals(CATEGORY_ID, categoryResponse.category().id());

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_emptyImageFile_shouldReturnCategoryResponse() {
        CategoryCreateRequest validRequest = CategoryCreateRequestFactory.createValidWithEmptyImageFile();

        Category category = CategoryFactory.createActivatedCategory(CATEGORY_ID);

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse categoryResponse = categoryService.createCategory(validRequest);

        assertNotNull(categoryResponse);
        assertNotNull(categoryResponse.category());

        assertEquals(CATEGORY_ID, categoryResponse.category().id());

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategoryActivation_activate_shouldReturnCategoryUpdateResponse() {
        Category category = CategoryFactory.createDeactivatedCategory(CATEGORY_ID);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryUpdateResponse categoryUpdateResponse =
                categoryService.updateCategoryActivation(category.getId(), true);

        assertNotNull(categoryUpdateResponse);
        assertNotNull(categoryUpdateResponse.message());

        assertTrue(category.isActivated());

        verify(categoryRepository).findById(category.getId());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategoryActivation_deactivate_shouldReturnCategoryUpdateResponse() {
        Category category = CategoryFactory.createActivatedCategory(CATEGORY_ID);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryUpdateResponse categoryUpdateResponse =
                categoryService.updateCategoryActivation(category.getId(), false);

        assertNotNull(categoryUpdateResponse);
        assertNotNull(categoryUpdateResponse.message());

        assertFalse(category.isActivated());

        verify(categoryRepository).findById(category.getId());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategoryActivation_notFoundCategory_shouldThrowCategoryNotFoundException() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.updateCategoryActivation(CATEGORY_ID, true)
        );

        verify(categoryRepository).findById(CATEGORY_ID);
    }

    @Test
    void updateCategoryActivation_invalidStatus_shouldReturnCategoryUpdateResponse() {
        Category activatedCategory = CategoryFactory.createActivatedCategory(CATEGORY_ID);

        when(categoryRepository.findById(activatedCategory.getId()))
                .thenReturn(Optional.of(activatedCategory));

        assertThrows(
                InvalidCategoryStatusException.class,
                () -> categoryService.updateCategoryActivation(activatedCategory.getId(), true)
        );

        verify(categoryRepository).findById(activatedCategory.getId());
    }

    @Test
    void deleteCategory_existingCategory_shouldReturnCategoryUpdateResponse() {
        Category category = CategoryFactory.createActivatedCategory(CATEGORY_ID);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryUpdateResponse categoryUpdateResponse = categoryService.deleteCategory(category.getId());

        assertNotNull(categoryUpdateResponse);
        assertNotNull(categoryUpdateResponse.message());

        assertTrue(category.isDeleted());

        verify(categoryRepository).findById(category.getId());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void deleteCategory_notFoundCategory_shouldThrowCategoryNotFoundException() {
        when(categoryRepository.findById(CATEGORY_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(CATEGORY_ID)
        );

        verify(categoryRepository).findById(CATEGORY_ID);
    }

    @Test
    void deleteCategory_deletedCategory_shouldThrowDeletedCategoryException() {
        Category deletedCategory = CategoryFactory.createDeletedCategory(CATEGORY_ID);

        when(categoryRepository.findById(deletedCategory.getId()))
                .thenReturn(Optional.of(deletedCategory));

        assertThrows(
                DeletedCategoryException.class,
                () -> categoryService.deleteCategory(CATEGORY_ID)
        );

        verify(categoryRepository).findById(CATEGORY_ID);
    }
}