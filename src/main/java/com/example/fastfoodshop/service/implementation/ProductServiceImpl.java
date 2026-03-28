package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.dto.ProductSelectionDTO;
import com.example.fastfoodshop.dto.PromotionResult;
import com.example.fastfoodshop.dto.ReviewDTO;
import com.example.fastfoodshop.dto.ProductStatsDTO;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.exception.category.DeletedCategoryException;
import com.example.fastfoodshop.exception.category.UnavailableCategoryException;
import com.example.fastfoodshop.exception.product.DeletedProductException;
import com.example.fastfoodshop.exception.product.InvalidStatusProductException;
import com.example.fastfoodshop.exception.product.ProductNotFoundException;
import com.example.fastfoodshop.exception.product.UnavailableProductException;
import com.example.fastfoodshop.projection.ProductRatingStatsProjection;
import com.example.fastfoodshop.projection.ProductSoldCountProjection;
import com.example.fastfoodshop.projection.ProductStatsProjection;
import com.example.fastfoodshop.repository.ProductRepository;
import com.example.fastfoodshop.repository.ReviewRepository;
import com.example.fastfoodshop.request.ProductCreateRequest;
import com.example.fastfoodshop.request.ProductGetByCategoryRequest;
import com.example.fastfoodshop.request.ProductUpdateRequest;
import com.example.fastfoodshop.response.product.ProductStatsResponse;
import com.example.fastfoodshop.response.product.ProductDisplayResponse;
import com.example.fastfoodshop.response.product.ProductSelectionResponse;
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

    public List<Product> findAllByIds(List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);

        if (products.size() != productIds.size()) {
            throw new ProductNotFoundException();
        }

        return products;
    }

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

    private Product buildProduct(
            ProductCreateRequest productCreateRequest, Category category, String slug
    ) {
        Product product = new Product();
        product.setCategory(category);
        product.setName(productCreateRequest.name());
        product.setSlug(slug);
        product.setDescription(productCreateRequest.description());
        product.setPrice(productCreateRequest.price());
        product.setActivated(productCreateRequest.activated());
        product.setDeleted(false);
        return product;
    }

    public ProductResponse createProduct(ProductCreateRequest productCreateRequest) {
        Category category = categoryService.findCategoryOrThrow(productCreateRequest.categoryId());

        String slug = generateUniqueSlug(productCreateRequest.name());

        Product product = buildProduct(productCreateRequest, category, slug);

        handleProductImage(product, productCreateRequest.imageUrl());
        handleProductModel3D(product, productCreateRequest.modelUrl());

        Product savedProduct = productRepository.save(product);
        return new ProductResponse(ProductDTO.from(savedProduct));
    }

    private void updateProductFields(Product product, ProductUpdateRequest productUpdateRequest) {
        product.setName(productUpdateRequest.name());
        product.setDescription(productUpdateRequest.description());
        product.setActivated(productUpdateRequest.activated());
    }

    public ProductResponse updateProduct(Long productId, ProductUpdateRequest productUpdateRequest) {
        Product product = findProductOrThrow(productId);

        updateProductFields(product, productUpdateRequest);
        handleProductImage(product, productUpdateRequest.imageUrl());
        handleProductModel3D(product, productUpdateRequest.modelUrl());

        Product savedProduct = productRepository.save(product);
        return new ProductResponse(ProductDTO.from(savedProduct));
    }

    public ProductPageResponse getProductPage(ProductGetByCategoryRequest productGetByCategoryRequest) {
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

    public ProductSelectionResponse getProductSelections() {
        List<ProductSelectionDTO> productSelectionDTOs = productRepository
                .findByIsDeletedFalseAndIsActivatedTrue()
                .stream()
                .map(product -> new ProductSelectionDTO(product.getId(), product.getName()))
                .toList();

        return new ProductSelectionResponse(productSelectionDTOs);
    }

    public ProductUpdateResponse updateProductActivation(Long productId, boolean activated) {
        Product product = findProductOrThrow(productId);
        if (product.isActivated() == activated) {
            throw new InvalidStatusProductException(productId);
        }

        product.setActivated(activated);
        productRepository.save(product);

        String message = activated
                ? "Kích hoạt sản phẩm thành công: " + productId
                : "Hủy kích hoạt sản phẩm thành công: " + productId;

        return new ProductUpdateResponse(message);
    }

    public ProductUpdateResponse deleteProduct(Long productId) {
        Product product = findProductOrThrow(productId);
        if (product.isDeleted()) {
            throw new DeletedProductException(productId);
        }
        product.setDeleted(true);

        productRepository.save(product);
        return new ProductUpdateResponse("Xóa sản phẩm thành công: " + productId);
    }

    private Map<Long, ProductRatingStatsProjection> getRatingMap(List<Long> ids) {
        return productRepository.getRatingStatsByProductIds(ids)
                .stream()
                .collect(Collectors.toMap(ProductRatingStatsProjection::getProductId, r -> r));
    }

    private Map<Long, ProductSoldCountProjection> getSoldMap(List<Long> ids) {
        return productRepository.getSoldCountByProductIds(ids)
                .stream()
                .collect(Collectors.toMap(ProductSoldCountProjection::getProductId, s -> s));
    }

    private ProductDTO buildProductDTO(
            Product product,
            Category category,
            ProductRatingStatsProjection productRatingStatsProjection,
            ProductSoldCountProjection productSoldCountProjection,
            List<ReviewDTO> reviews
    ) {
        double averageRating = 0.0;
        long reviewCount = 0;

        if (productRatingStatsProjection != null) {
            averageRating = productRatingStatsProjection.getAvgRating() != null
                    ? productRatingStatsProjection.getAvgRating()
                    : 0.0;
            reviewCount = productRatingStatsProjection.getReviewCount();
        }

        long soldCount = productSoldCountProjection != null ? productSoldCountProjection.getSoldCount() : 0L;

        PromotionResult promotion = categoryService.applyPromotion(product, category);

        return ProductDTO.from(product, reviews, promotion, averageRating, reviewCount, soldCount);
    }

    public ProductDisplayResponse getAllDisplayableProducts(String categorySlug) {
        Category category = categoryService.findCategoryOrThrow(categorySlug);
        List<Product> products = productRepository.findByCategoryAndIsDeletedFalseAndIsActivatedTrue(category);

        List<Long> productIds = products.stream().map(Product::getId).toList();

        var ratingMap = getRatingMap(productIds);

        var soldMap = getSoldMap(productIds);

        List<ProductDTO> productDTOs = products.stream()
                .map(product -> buildProductDTO(
                        product,
                        category,
                        ratingMap.get(product.getId()),
                        soldMap.get(product.getId()),
                        List.of()
                ))
                .toList();

        return new ProductDisplayResponse(productDTOs);
    }

    public ProductResponse getProductBySlug(String productSlug) {
        Product product = findProductOrThrow(productSlug);

        Long productId = product.getId();
        checkActivatedCategoryAndActivatedProduct(productId);

        Category category = product.getCategory();

        ProductRatingStatsProjection ratingStats = productRepository.getRatingStatsByProductIds(
                List.of(productId)
        ).stream().findFirst().orElse(null);

        ProductSoldCountProjection soldStats = productRepository.getSoldCountByProductIds(
                List.of(productId)
        ).stream().findFirst().orElse(null);

        Pageable top5 = PageRequest.of(0, 5);
        List<ReviewDTO> reviewDTOs = reviewRepository
                .findTop5ByProductIdOrderByRatingDescCreatedAtDesc(productId, top5)
                .stream()
                .map(ReviewDTO::from)
                .toList();

        ProductDTO productDTO = buildProductDTO(
                product,
                category,
                ratingStats,
                soldStats,
                reviewDTOs
        );

        return new ProductResponse(productDTO);
    }

    public ProductStatsResponse getProductStats() {
        List<ProductStatsProjection> statsProjections = productRepository.getStats();

        List<ProductStatsDTO> productStatsDTOs = statsProjections
                .stream()
                .map(productStatsProjection -> new ProductStatsDTO(
                        productStatsProjection.getName(),
                        productStatsProjection.getTotalRevenue(),
                        productStatsProjection.getTotalQuantitySold()
                )).toList();

        return new ProductStatsResponse(productStatsDTOs);
    }
}
