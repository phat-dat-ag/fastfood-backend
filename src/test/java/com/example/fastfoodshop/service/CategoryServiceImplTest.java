package com.example.fastfoodshop.service;

import com.example.fastfoodshop.constant.CloudinaryResult;
import com.example.fastfoodshop.constant.FolderNameConstants;
import com.example.fastfoodshop.dto.PromotionResult;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.exception.category.CategoryNotFoundException;
import com.example.fastfoodshop.exception.category.DeletedCategoryException;
import com.example.fastfoodshop.exception.category.InvalidCategoryStatusException;
import com.example.fastfoodshop.exception.cloudinary.ImageDeleteException;
import com.example.fastfoodshop.factory.category.CategoryCreateRequestFactory;
import com.example.fastfoodshop.factory.category.CategoryFactory;
import com.example.fastfoodshop.factory.category.CategoryPageFactory;
import com.example.fastfoodshop.factory.category.CategoryUpdateRequestFactory;
import com.example.fastfoodshop.factory.file.CloudinaryUpdateResultFactory;
import com.example.fastfoodshop.factory.product.ProductFactory;
import com.example.fastfoodshop.factory.promotion.PromotionFactory;
import com.example.fastfoodshop.projection.CategoryStatsProjection;
import com.example.fastfoodshop.repository.CategoryRepository;
import com.example.fastfoodshop.request.CategoryCreateRequest;
import com.example.fastfoodshop.request.CategoryUpdateRequest;
import com.example.fastfoodshop.response.category.CategoryDisplayResponse;
import com.example.fastfoodshop.response.category.CategoryResponse;
import com.example.fastfoodshop.response.category.CategoryPageResponse;
import com.example.fastfoodshop.response.category.CategorySelectionResponse;
import com.example.fastfoodshop.response.category.CategoryUpdateResponse;
import com.example.fastfoodshop.response.category.CategoryStatsResponse;
import com.example.fastfoodshop.service.implementation.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @Mock
    CategoryRepository categoryRepository;

    @Mock
    CloudinaryService cloudinaryService;

    @InjectMocks
    CategoryServiceImpl categoryService;

    private static final Long PROMOTION_ID = 333L;

    private static final Long PRODUCT_ID = 777L;

    private static final Long CATEGORY_ID = 888L;
    private static final String CATEGORY_SLUG = "Tra-sua";

    private static final int PAGE = 5;
    private static final int SIZE = 5;

    private Pageable createPageRequest() {
        return PageRequest.of(PAGE, SIZE);
    }

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
    void applyPromotion_withProductPromotion_shouldReturnPromotionResult() {
        Promotion validPromotion = PromotionFactory.createValidPromotion(PROMOTION_ID);

        Product product = ProductFactory.createActivatedProductWithPromotions(
                PRODUCT_ID, List.of(validPromotion)
        );

        Category category = CategoryFactory.createActivatedCategory(CATEGORY_ID);

        PromotionResult promotionResult = categoryService.applyPromotion(product, category);

        assertNotNull(promotionResult);

        assertEquals(validPromotion.getId(), promotionResult.promotionId());
    }

    @Test
    void applyPromotion_withCategoryPromotion_shouldReturnPromotionResult() {
        Product product = ProductFactory.createActivatedProductWithPromotions(
                PRODUCT_ID, List.of()
        );

        Promotion validPromotion = PromotionFactory.createValidPromotion(PROMOTION_ID);

        Category category = CategoryFactory.createActivatedCategoryWithPromotions(
                CATEGORY_ID, List.of(validPromotion)
        );

        PromotionResult promotionResult = categoryService.applyPromotion(product, category);

        assertNotNull(promotionResult);

        assertEquals(validPromotion.getId(), promotionResult.promotionId());
    }

    @Test
    void applyPromotion_withoutPromotion_shouldReturnPromotionResult() {
        Product product = ProductFactory.createActivatedProductWithPromotions(
                PRODUCT_ID, List.of()
        );

        Category category = CategoryFactory.createActivatedCategoryWithPromotions(
                CATEGORY_ID, List.of()
        );

        PromotionResult promotionResult = categoryService.applyPromotion(product, category);

        assertNotNull(promotionResult);

        assertNull(promotionResult.promotionId());
    }

    @Test
    void applyPromotion_deactivatedCategoryPromotion_shouldReturnPromotionResult() {
        Product product = ProductFactory.createActivatedProductWithPromotions(
                PRODUCT_ID, List.of()
        );

        Promotion deactivatedPromotion = PromotionFactory.createDeactivatedPromotion(PROMOTION_ID);

        Category category = CategoryFactory.createActivatedCategoryWithPromotions(
                CATEGORY_ID, List.of(deactivatedPromotion)
        );

        PromotionResult promotionResult = categoryService.applyPromotion(product, category);

        assertNotNull(promotionResult);

        assertNull(promotionResult.promotionId());
    }

    @Test
    void applyPromotion_deletedCategoryPromotion_shouldReturnPromotionResult() {
        Product product = ProductFactory.createActivatedProductWithPromotions(
                PRODUCT_ID, List.of()
        );

        Promotion deletedPromotion = PromotionFactory.createDeletedPromotion(PROMOTION_ID);

        Category category = CategoryFactory.createActivatedCategoryWithPromotions(
                CATEGORY_ID, List.of(deletedPromotion)
        );

        PromotionResult promotionResult = categoryService.applyPromotion(product, category);

        assertNotNull(promotionResult);

        assertNull(promotionResult.promotionId());
    }

    @Test
    void applyPromotion_globalCategoryPromotion_shouldReturnPromotionResult() {
        Product product = ProductFactory.createActivatedProductWithPromotions(
                PRODUCT_ID, List.of()
        );

        Promotion globalPromotion = PromotionFactory.createGlobalPromotion(PROMOTION_ID);

        Category category = CategoryFactory.createActivatedCategoryWithPromotions(
                CATEGORY_ID, List.of(globalPromotion)
        );

        PromotionResult promotionResult = categoryService.applyPromotion(product, category);

        assertNotNull(promotionResult);

        assertNull(promotionResult.promotionId());
    }

    @Test
    void applyPromotion_notStartedYetCategoryPromotion_shouldReturnPromotionResult() {
        Product product = ProductFactory.createActivatedProductWithPromotions(
                PRODUCT_ID, List.of()
        );

        Promotion notStartedYetPromotion = PromotionFactory.createPromotionNotStartedYet(PROMOTION_ID);

        Category category = CategoryFactory.createActivatedCategoryWithPromotions(
                CATEGORY_ID, List.of(notStartedYetPromotion)
        );

        PromotionResult promotionResult = categoryService.applyPromotion(product, category);

        assertNotNull(promotionResult);

        assertNull(promotionResult.promotionId());
    }

    @Test
    void applyPromotion_ExpiredCategoryPromotion_shouldReturnPromotionResult() {
        Product product = ProductFactory.createActivatedProductWithPromotions(
                PRODUCT_ID, List.of()
        );

        Promotion expiredPromotion = PromotionFactory.createExpiredPromotion(PROMOTION_ID);

        Category category = CategoryFactory.createActivatedCategoryWithPromotions(
                CATEGORY_ID, List.of(expiredPromotion)
        );

        PromotionResult promotionResult = categoryService.applyPromotion(product, category);

        assertNotNull(promotionResult);

        assertNull(promotionResult.promotionId());
    }

    @Test
    void applyPromotion_ExhaustedCategoryPromotion_shouldReturnPromotionResult() {
        Product product = ProductFactory.createActivatedProductWithPromotions(
                PRODUCT_ID, List.of()
        );

        Promotion exhaustedPromotion = PromotionFactory.createExhaustedPromotion(PROMOTION_ID);

        Category category = CategoryFactory.createActivatedCategoryWithPromotions(
                CATEGORY_ID, List.of(exhaustedPromotion)
        );

        PromotionResult promotionResult = categoryService.applyPromotion(product, category);

        assertNotNull(promotionResult);

        assertNull(promotionResult.promotionId());
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
    void updateCategory_validRequest_shouldReturnCategoryUpdateResponse() {
        Category category = CategoryFactory.createWithPublicIdField(CATEGORY_ID);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        CategoryUpdateRequest validRequest = CategoryUpdateRequestFactory.createValidWithFile();

        Map<String, Object> mockResult = CloudinaryUpdateResultFactory.createValidResult();

        when(cloudinaryService.uploadImage(
                eq(validRequest.imageUrl()), eq(FolderNameConstants.categoryFolderName))
        ).thenReturn((Map) mockResult);

        when(cloudinaryService.deleteImage(anyString())).thenReturn(true);

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse categoryResponse = categoryService.updateCategory(category.getId(), validRequest);

        assertNotNull(categoryResponse);
        assertNotNull(categoryResponse.category());

        assertEquals(validRequest.name(), categoryResponse.category().name());
        assertEquals(validRequest.description(), categoryResponse.category().description());
        assertEquals(validRequest.activated(), categoryResponse.category().activated());

        assertEquals(mockResult.get(CloudinaryResult.SECURE_URL), categoryResponse.category().imageUrl());

        verify(categoryRepository).findById(category.getId());
        verify(cloudinaryService).uploadImage(
                eq(validRequest.imageUrl()), eq(FolderNameConstants.categoryFolderName));
        verify(cloudinaryService).deleteImage(anyString());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_withoutOldPublicId_shouldReturnCategoryUpdateResponse() {
        Category category = CategoryFactory.createWithoutPublicIdField(CATEGORY_ID);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        CategoryUpdateRequest validRequest = CategoryUpdateRequestFactory.createValidWithFile();

        Map<String, Object> mockResult = CloudinaryUpdateResultFactory.createValidResult();

        when(cloudinaryService.uploadImage(
                eq(validRequest.imageUrl()), eq(FolderNameConstants.categoryFolderName))
        ).thenReturn((Map) mockResult);

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse categoryResponse = categoryService.updateCategory(category.getId(), validRequest);

        assertNotNull(categoryResponse);
        assertNotNull(categoryResponse.category());

        assertEquals(validRequest.name(), categoryResponse.category().name());
        assertEquals(validRequest.description(), categoryResponse.category().description());
        assertEquals(validRequest.activated(), categoryResponse.category().activated());

        assertEquals(mockResult.get(CloudinaryResult.SECURE_URL), categoryResponse.category().imageUrl());

        verify(categoryRepository).findById(category.getId());
        verify(cloudinaryService).uploadImage(
                eq(validRequest.imageUrl()), eq(FolderNameConstants.categoryFolderName));
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_withoutEmptyOldPublicId_shouldReturnCategoryUpdateResponse() {
        Category category = CategoryFactory.createWithEmptyPublicIdField(CATEGORY_ID);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        CategoryUpdateRequest validRequest = CategoryUpdateRequestFactory.createValidWithFile();

        Map<String, Object> mockResult = CloudinaryUpdateResultFactory.createValidResult();

        when(cloudinaryService.uploadImage(
                eq(validRequest.imageUrl()), eq(FolderNameConstants.categoryFolderName))
        ).thenReturn((Map) mockResult);

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse categoryResponse = categoryService.updateCategory(category.getId(), validRequest);

        assertNotNull(categoryResponse);
        assertNotNull(categoryResponse.category());

        assertEquals(validRequest.name(), categoryResponse.category().name());
        assertEquals(validRequest.description(), categoryResponse.category().description());
        assertEquals(validRequest.activated(), categoryResponse.category().activated());

        assertEquals(mockResult.get(CloudinaryResult.SECURE_URL), categoryResponse.category().imageUrl());

        verify(categoryRepository).findById(category.getId());
        verify(cloudinaryService).uploadImage(
                eq(validRequest.imageUrl()), eq(FolderNameConstants.categoryFolderName));
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_withEmptyImageFile_shouldReturnCategoryUpdateResponse() {
        Category category = CategoryFactory.createWithPublicIdField(CATEGORY_ID);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        CategoryUpdateRequest validRequest = CategoryUpdateRequestFactory.createValidWithEmptyFile();

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse categoryResponse = categoryService.updateCategory(category.getId(), validRequest);

        assertNotNull(categoryResponse);
        assertNotNull(categoryResponse.category());

        assertEquals(validRequest.name(), categoryResponse.category().name());
        assertEquals(validRequest.description(), categoryResponse.category().description());
        assertEquals(validRequest.activated(), categoryResponse.category().activated());

        verify(categoryRepository).findById(category.getId());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_withoutFile_shouldReturnCategoryUpdateResponse() {
        Category category = CategoryFactory.createWithPublicIdField(CATEGORY_ID);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        CategoryUpdateRequest validRequest = CategoryUpdateRequestFactory.createValidWithNullFile();

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse categoryResponse = categoryService.updateCategory(category.getId(), validRequest);

        assertNotNull(categoryResponse);
        assertNotNull(categoryResponse.category());

        assertEquals(validRequest.name(), categoryResponse.category().name());
        assertEquals(validRequest.description(), categoryResponse.category().description());
        assertEquals(validRequest.activated(), categoryResponse.category().activated());

        verify(categoryRepository).findById(category.getId());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_withImageUpdatingException_shouldThrowImageDeleteException() {
        Category category = CategoryFactory.createWithPublicIdField(CATEGORY_ID);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        CategoryUpdateRequest validRequest = CategoryUpdateRequestFactory.createValidWithFile();

        Map<String, Object> mockResult = CloudinaryUpdateResultFactory.createValidResult();

        when(cloudinaryService.uploadImage(
                eq(validRequest.imageUrl()), eq(FolderNameConstants.categoryFolderName))
        ).thenReturn((Map) mockResult);

        when(cloudinaryService.deleteImage(anyString()))
                .thenThrow(new ImageDeleteException("Exception"));

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse categoryResponse = categoryService.updateCategory(category.getId(), validRequest);

        assertNotNull(categoryResponse);
        assertNotNull(categoryResponse.category());

        assertEquals(validRequest.name(), categoryResponse.category().name());
        assertEquals(validRequest.description(), categoryResponse.category().description());
        assertEquals(validRequest.activated(), categoryResponse.category().activated());

        verify(categoryRepository).findById(category.getId());
        verify(cloudinaryService).uploadImage(
                eq(validRequest.imageUrl()), eq(FolderNameConstants.categoryFolderName));
        verify(categoryRepository).save(any(Category.class));
        verify(cloudinaryService).deleteImage(anyString());
    }

    @Test
    void updateCategory_notFoundCategory_shouldThrowCategoryNotFoundException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        CategoryUpdateRequest validRequest = CategoryUpdateRequestFactory.createValidWithFile();

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.updateCategory(CATEGORY_ID, validRequest)
        );

        verify(categoryRepository).findById(anyLong());
    }

    @Test
    void getCategoryPage_shouldReturnCategoryPageResponse() {
        Pageable pageable = createPageRequest();

        Page<Category> categoryPage = CategoryPageFactory.createCategoryPage();

        when(categoryRepository.findByIsDeletedFalse(pageable)).thenReturn(categoryPage);

        CategoryPageResponse categoryPageResponse = categoryService.getCategoryPage(PAGE, SIZE);

        assertNotNull(categoryPageResponse);

        assertFalse(categoryPageResponse.categories().isEmpty());

        assertEquals(
                categoryPage.getContent().size(),
                categoryPageResponse.categories().size()
        );

        verify(categoryRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void getCategoryPage_emptyPageResponse_shouldReturnCategoryPageResponse() {
        Pageable pageable = createPageRequest();

        Page<Category> categoryPage = CategoryPageFactory.createEmptyCategoryPage();

        when(categoryRepository.findByIsDeletedFalse(pageable)).thenReturn(categoryPage);

        CategoryPageResponse categoryPageResponse = categoryService.getCategoryPage(PAGE, SIZE);

        assertNotNull(categoryPageResponse);

        assertTrue(categoryPageResponse.categories().isEmpty());

        verify(categoryRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void getCategorySelections_shouldReturnCategorySelectionResponse() {
        List<Category> categories = CategoryFactory.createDisplayableCategories();

        when(categoryRepository.findByIsDeletedFalseAndIsActivatedTrue()).thenReturn(categories);

        CategorySelectionResponse categorySelectionResponse = categoryService.getCategorySelections();

        assertNotNull(categorySelectionResponse);

        assertFalse(categorySelectionResponse.selectiveCategories().isEmpty());

        assertEquals(
                categories.size(),
                categorySelectionResponse.selectiveCategories().size()
        );

        verify(categoryRepository).findByIsDeletedFalseAndIsActivatedTrue();
    }

    @Test
    void getCategorySelections_emptyList_shouldReturnCategorySelectionResponse() {
        List<Category> categories = List.of();

        when(categoryRepository.findByIsDeletedFalseAndIsActivatedTrue()).thenReturn(categories);

        CategorySelectionResponse categorySelectionResponse = categoryService.getCategorySelections();

        assertNotNull(categorySelectionResponse);

        assertTrue(categorySelectionResponse.selectiveCategories().isEmpty());

        verify(categoryRepository).findByIsDeletedFalseAndIsActivatedTrue();
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

    @Test
    void getAllDisplayableCategories_shouldReturnCategoryDisplayResponse() {
        List<Category> displayableCategories =
                CategoryFactory.createDisplayableCategories();

        when(categoryRepository.findByIsDeletedFalseAndIsActivatedTrue())
                .thenReturn(displayableCategories);

        CategoryDisplayResponse categoryDisplayResponse =
                categoryService.getAllDisplayableCategories();

        assertNotNull(categoryDisplayResponse);
        assertNotNull(categoryDisplayResponse.displayableCategories());

        assertEquals(
                displayableCategories.size(),
                categoryDisplayResponse.displayableCategories().size()
        );
        assertEquals(
                displayableCategories.get(0).getId(),
                categoryDisplayResponse.displayableCategories().get(0).id()
        );

        verify(categoryRepository).findByIsDeletedFalseAndIsActivatedTrue();
    }

    @Test
    void getAllDisplayableCategories_emptyList_shouldReturnCategoryDisplayResponse() {
        when(categoryRepository.findByIsDeletedFalseAndIsActivatedTrue())
                .thenReturn(List.of());

        CategoryDisplayResponse categoryDisplayResponse =
                categoryService.getAllDisplayableCategories();

        assertNotNull(categoryDisplayResponse);
        assertNotNull(categoryDisplayResponse.displayableCategories());

        assertTrue(categoryDisplayResponse.displayableCategories().isEmpty());

        verify(categoryRepository).findByIsDeletedFalseAndIsActivatedTrue();
    }

    @Test
    void getCategoryStats_shouldReturnCategoryStatsResponse() {
        CategoryStatsProjection categoryStatsProjection = mock(CategoryStatsProjection.class);

        String categoryName = "Tau Hu";
        Long totalQuantitySold = 10000L;
        BigDecimal totalRevenue = BigDecimal.valueOf(999999999);

        when(categoryStatsProjection.getName()).thenReturn(categoryName);
        when(categoryStatsProjection.getTotalQuantitySold()).thenReturn(totalQuantitySold);
        when(categoryStatsProjection.getTotalRevenue()).thenReturn(totalRevenue);

        List<CategoryStatsProjection> categoryStatsProjectionList = List.of(categoryStatsProjection);

        when(categoryRepository.getStats()).thenReturn(categoryStatsProjectionList);

        CategoryStatsResponse categoryStatsResponse = categoryService.getCategoryStats();

        assertNotNull(categoryStatsResponse);

        assertFalse(categoryStatsResponse.categoryStats().isEmpty());

        assertEquals(
                categoryStatsProjectionList.size(),
                categoryStatsResponse.categoryStats().size()
        );

        verify(categoryRepository).getStats();
    }

    @Test
    void getCategoryStats_emptyList_shouldReturnCategoryStatsResponse() {
        List<CategoryStatsProjection> categoryStatsProjectionList = List.of();

        when(categoryRepository.getStats()).thenReturn(categoryStatsProjectionList);

        CategoryStatsResponse categoryStatsResponse = categoryService.getCategoryStats();

        assertNotNull(categoryStatsResponse);

        assertTrue(categoryStatsResponse.categoryStats().isEmpty());

        verify(categoryRepository).getStats();
    }
}