package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.dto.PromotionResult;
import com.example.fastfoodshop.dto.ReviewDTO;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.Review;
import com.example.fastfoodshop.exception.category.DeletedCategoryException;
import com.example.fastfoodshop.exception.category.UnavailableCategoryException;
import com.example.fastfoodshop.exception.product.DeletedProductException;
import com.example.fastfoodshop.exception.product.InvalidStatusProductException;
import com.example.fastfoodshop.exception.product.ProductNotFoundException;
import com.example.fastfoodshop.exception.product.UnavailableProductException;
import com.example.fastfoodshop.projection.ProductRatingStatsProjection;
import com.example.fastfoodshop.projection.ProductSoldCountProjection;
import com.example.fastfoodshop.repository.ProductRepository;
import com.example.fastfoodshop.repository.ReviewRepository;
import com.example.fastfoodshop.request.ProductCreateRequest;
import com.example.fastfoodshop.request.ProductGetByCategoryRequest;
import com.example.fastfoodshop.request.ProductUpdateRequest;
import com.example.fastfoodshop.response.product.ProductDisplayResponse;
import com.example.fastfoodshop.response.product.ProductPageResponse;
import com.example.fastfoodshop.response.product.ProductResponse;
import com.example.fastfoodshop.response.product.ProductUpdateResponse;
import com.example.fastfoodshop.service.CategoryService;
import com.example.fastfoodshop.service.CloudinaryService;
import com.example.fastfoodshop.service.ProductService;
import com.example.fastfoodshop.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final CloudinaryService cloudinaryService;
    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtils.toSlug(name);
        String uniqueSlug = baseSlug;
        int counter = 1;

        while (productRepository.existsBySlug((uniqueSlug))) {
            uniqueSlug = baseSlug + "-" + counter++;
        }
        return uniqueSlug;
    }

    public Product findProductOrThrow(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private Product findProductOrThrow(String productSlug) {
        return productRepository.findBySlug(productSlug).orElseThrow(() -> new ProductNotFoundException(productSlug));
    }

    public void checkActivatedCategoryAndActivatedProduct(Long productId) {
        Product product = findProductOrThrow(productId);
        if (product.isDeleted() || !product.isActivated()) {
            throw new UnavailableProductException(product.getName());
        }

        Category category = product.getCategory();
        if (category.isDeleted() || !category.isActivated()) {
            throw new UnavailableCategoryException(category.getName());
        }
    }

    private Product findActivatedProductOrThrow(Long productId) {
        return productRepository.findByIdAndIsActivatedTrueAndIsDeletedFalse(productId).orElseThrow(
                () -> new InvalidStatusProductException(productId)
        );
    }

    private Product findDeactivatedProductOrThrow(Long productId) {
        return productRepository.findByIdAndIsActivatedFalseAndIsDeletedFalse(productId).orElseThrow(
                () -> new InvalidStatusProductException(productId)
        );
    }

    private void handleProductImage(Product product, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty())
            return;

        String oldPublicId = product.getImagePublicId();
        Map<?, ?> result = cloudinaryService.uploadImage(imageFile, "product");

        product.setImageUrl((String) result.get("secure_url"));
        product.setImagePublicId((String) result.get("public_id"));

        if (oldPublicId != null && !oldPublicId.isEmpty()) {
            try {
                boolean deleted = cloudinaryService.deleteImage(oldPublicId);
                log.info("Old product image deleted successfully: {}", oldPublicId);
            } catch (Exception e) {
                log.warn("Failed to delete old product image: {}", oldPublicId, e);
            }
        }
    }

    private void handleProductModel3D(Product product, MultipartFile modelFile) {
        if (modelFile == null || modelFile.isEmpty()) return;

        String oldPublicId = product.getModelPublicId();
        Map<?, ?> result = cloudinaryService.uploadImage(modelFile, "product_model");

        product.setModelUrl((String) result.get("secure_url"));
        product.setModelPublicId((String) result.get("public_id"));

        if (oldPublicId != null && !oldPublicId.isEmpty()) {
            try {
                boolean deleted = cloudinaryService.deleteImage(oldPublicId);
                log.info("Old product 3D model deleted successfully: {}", oldPublicId);
            } catch (Exception e) {
                log.warn("Failed to delete old product 3D model: {}", oldPublicId, e);
            }
        }
    }

    public ProductResponse createProduct(ProductCreateRequest productCreateRequest) {
        Category category = categoryService.findCategoryOrThrow(productCreateRequest.categoryId());

        String slug = generateUniqueSlug(productCreateRequest.name());

        Product product = new Product();
        product.setCategory(category);
        product.setName(productCreateRequest.name());
        product.setSlug(slug);
        product.setDescription(productCreateRequest.description());
        product.setPrice(productCreateRequest.price());
        product.setActivated(productCreateRequest.activated());
        product.setDeleted(false);

        handleProductImage(product, productCreateRequest.imageUrl());
        handleProductModel3D(product, productCreateRequest.modelUrl());
        Product savedProduct = productRepository.save(product);
        return new ProductResponse(ProductDTO.from(savedProduct));
    }

    public ProductResponse updateProduct(ProductUpdateRequest productUpdateRequest) {
        Product product = findProductOrThrow(productUpdateRequest.id());
        product.setName(productUpdateRequest.name());
        product.setDescription(productUpdateRequest.description());
        product.setActivated(productUpdateRequest.activated());

        handleProductImage(product, productUpdateRequest.imageUrl());
        handleProductModel3D(product, productUpdateRequest.modelUrl());

        Product savedProduct = productRepository.save(product);
        return new ProductResponse(ProductDTO.from(savedProduct));
    }

    public ProductPageResponse getAllProductsByCategory(ProductGetByCategoryRequest productGetByCategoryRequest) {
        Category category = categoryService.findCategoryOrThrow(
                productGetByCategoryRequest.getCategorySlug()
        );
        if (category.isDeleted()) {
            throw new DeletedCategoryException(productGetByCategoryRequest.getCategorySlug());
        }

        Pageable pageable = PageRequest.of(
                productGetByCategoryRequest.getPage(), productGetByCategoryRequest.getSize()
        );
        Page<Product> productPage = productRepository.findByCategoryAndIsDeletedFalse(category, pageable);

        return ProductPageResponse.from(productPage);
    }

    public ProductDisplayResponse getAllDisplayableProductsByCategory(String categorySlug) {
        Category category = categoryService.findCategoryOrThrow(categorySlug);
        List<Product> products = productRepository.findByCategoryAndIsDeletedFalseAndIsActivatedTrue(category);

        List<Long> productIds = products.stream().map(Product::getId).toList();

        var ratingStats = productRepository.getRatingStatsByProductIds(productIds);
        Map<Long, ProductRatingStatsProjection> ratingMap = ratingStats.stream()
                .collect(Collectors.toMap(ProductRatingStatsProjection::getProductId, r -> r));

        var soldStats = productRepository.getSoldCountByProductIds(productIds);
        Map<Long, ProductSoldCountProjection> soldMap = soldStats.stream()
                .collect(Collectors.toMap(ProductSoldCountProjection::getProductId, s -> s));


        ArrayList<ProductDTO> productDTOs = new ArrayList<>();
        for (Product product : products) {
            ProductRatingStatsProjection productRatingStatsProjection = ratingMap.get(product.getId());

            double averageRating = 0.0;
            long reviewCount = 0;

            if (productRatingStatsProjection != null) {
                averageRating = productRatingStatsProjection.getAvgRating() != null
                        ? productRatingStatsProjection.getAvgRating()
                        : 0.0;
                reviewCount = productRatingStatsProjection.getReviewCount();
            }

            ProductSoldCountProjection soldCountProjection = soldMap.get(product.getId());
            long soldCount = soldCountProjection != null ? soldCountProjection.getSoldCount() : 0L;

            PromotionResult promotionResult = categoryService.applyPromotion(product, category);
            productDTOs.add(ProductDTO.from(product, List.of(), promotionResult, averageRating, reviewCount, soldCount));
        }
        return new ProductDisplayResponse(productDTOs);
    }

    public ProductDisplayResponse getAllDisplayableProducts() {
        List<Product> products = productRepository.findByIsDeletedFalseAndIsActivatedTrue();

        ArrayList<ProductDTO> productDTOs = new ArrayList<>();
        for (Product product : products) {
            categoryService.applyPromotion(product, product.getCategory());
            productDTOs.add(ProductDTO.from(product));
        }

        return new ProductDisplayResponse(productDTOs);
    }

    public ProductResponse getProductBySlug(String productSlug) {
        Product product = findProductOrThrow(productSlug);
        checkActivatedCategoryAndActivatedProduct(product.getId());
        Category category = product.getCategory();

        ProductRatingStatsProjection ratingStats = productRepository.getRatingStatsByProductIds(
                List.of(product.getId())
        ).stream().findFirst().orElse(null);

        double averageRating = 0.0;
        long reviewCount = 0;

        if (ratingStats != null) {
            averageRating = ratingStats.getAvgRating() != null ? ratingStats.getAvgRating() : 0.0;
            reviewCount = ratingStats.getReviewCount();
        }

        ProductSoldCountProjection soldStats = productRepository.getSoldCountByProductIds(
                List.of(product.getId())
        ).stream().findFirst().orElse(null);

        long soldCount = soldStats != null ? soldStats.getSoldCount() : 0L;

        Pageable top5 = PageRequest.of(0, 5);
        List<Review> topReviews = reviewRepository.findTop5ByProductIdOrderByRatingDescCreatedAtDesc(
                product.getId(), top5
        );
        List<ReviewDTO> reviewDTOs = topReviews.stream()
                .map(ReviewDTO::from)
                .toList();

        PromotionResult promotionResult = categoryService.applyPromotion(product, category);
        ProductDTO productDTO = ProductDTO.from(
                product, reviewDTOs, promotionResult, averageRating, reviewCount, soldCount
        );
        return new ProductResponse(productDTO);
    }

    public ProductUpdateResponse activateProduct(Long productId) {
        Product product = findDeactivatedProductOrThrow(productId);
        product.setActivated(true);

        productRepository.save(product);
        return new ProductUpdateResponse("Kích hoạt sản phẩm thành công: " + productId);
    }

    public ProductUpdateResponse deactivateProduct(Long productId) {
        Product product = findActivatedProductOrThrow(productId);
        product.setActivated(false);

        productRepository.save(product);
        return new ProductUpdateResponse("Hủy kích hoạt sản phẩm thành công: " + productId);
    }

    public ProductUpdateResponse deleteCategory(Long productId) {
        Product product = findProductOrThrow(productId);
        if (product.isDeleted()) {
            throw new DeletedProductException(productId);
        }
        product.setDeleted(true);

        productRepository.save(product);
        return new ProductUpdateResponse("Xóa sản phẩm thành công: " + productId);
    }
}
