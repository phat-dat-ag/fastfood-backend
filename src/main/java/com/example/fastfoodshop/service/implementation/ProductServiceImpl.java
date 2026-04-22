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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final CloudinaryService cloudinaryService;
    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public List<Product> findAllByIds(List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);

        if (products.size() != productIds.size()) {
            throw new ProductNotFoundException();
        }

        log.info("[ProductService] Got {} products by IDs", products.size());

        return products;
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtils.toSlug(name);
        String uniqueSlug = baseSlug;
        int counter = 1;

        while (productRepository.existsBySlug((uniqueSlug))) {
            uniqueSlug = baseSlug + "-" + counter++;
        }

        log.debug("[ProductService] Generated slug {} for product", uniqueSlug);

        return uniqueSlug;
    }

    public Product findProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private Product findProductOrThrow(String productSlug) {
        return productRepository.findBySlug(productSlug)
                .orElseThrow(() -> new ProductNotFoundException(productSlug));
    }

    public void checkActivatedCategoryAndActivatedProduct(Long productId) {
        Product product = findProductOrThrow(productId);
        if (product.isDeleted() || !product.isActivated()) {
            throw new UnavailableProductException(product.getName());
        }

        log.debug("[ProductService] Product id={} is activated", productId);

        Category category = product.getCategory();
        if (category.isDeleted() || !category.isActivated()) {
            throw new UnavailableCategoryException(category.getName());
        }

        log.debug("[ProductService] Category id={} is activated", category.getId());
    }

    private void handleProductImage(Product product, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty())
            return;

        String oldPublicId = product.getImagePublicId();
        Map<?, ?> result = cloudinaryService.uploadImage(imageFile, "product");

        product.setImageUrl((String) result.get("secure_url"));
        product.setImagePublicId((String) result.get("public_id"));

        log.debug(
                "[ProductService] Successfully uploaded image for product id={}",
                product.getId()
        );

        if (oldPublicId != null && !oldPublicId.isEmpty()) {
            try {
                cloudinaryService.deleteImage(oldPublicId);
                log.debug("[ProductService] Successfully deleted old public_id of image");
            } catch (Exception e) {
                log.warn(
                        "[ProductService] Deleted old public_id of an image with exception {}",
                        e.getMessage()
                );
            }
        }
    }

    private void handleProductModel3D(Product product, MultipartFile modelFile) {
        if (modelFile == null || modelFile.isEmpty()) return;

        String oldPublicId = product.getModelPublicId();
        Map<?, ?> result = cloudinaryService.uploadImage(modelFile, "product_model");

        product.setModelUrl((String) result.get("secure_url"));
        product.setModelPublicId((String) result.get("public_id"));

        log.debug(
                "[ProductService] Successfully uploaded model for product id={}",
                product.getId()
        );

        if (oldPublicId != null && !oldPublicId.isEmpty()) {
            try {
                cloudinaryService.deleteImage(oldPublicId);
                log.debug("[ProductService] Successfully deleted old public_id of model");
            } catch (Exception e) {
                log.warn(
                        "[ProductService] Deleted old public_id of model with exception {}",
                        e.getMessage()
                );
            }
        }
    }

    private Product buildProduct(
            ProductCreateRequest request, Category category, String slug
    ) {
        Product product = new Product();
        product.setCategory(category);
        product.setName(request.name());
        product.setSlug(slug);
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setActivated(request.activated());
        product.setDeleted(false);
        return product;
    }

    public ProductResponse createProduct(ProductCreateRequest request) {
        Category category = categoryService.findCategoryByIdOrThrow(request.categoryId());

        String slug = generateUniqueSlug(request.name());

        Product product = buildProduct(request, category, slug);

        handleProductImage(product, request.imageUrl());
        handleProductModel3D(product, request.modelUrl());

        Product savedProduct = productRepository.save(product);

        log.info("[ProductService] Successfully created product id={}", savedProduct.getId());

        return new ProductResponse(ProductDTO.from(savedProduct));
    }

    private void updateProductFields(Product product, ProductUpdateRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setActivated(request.activated());
    }

    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = findProductOrThrow(productId);

        updateProductFields(product, request);
        handleProductImage(product, request.imageUrl());
        handleProductModel3D(product, request.modelUrl());

        Product savedProduct = productRepository.save(product);

        log.info("[ProductService] Successfully updated product id={}", savedProduct.getId());

        return new ProductResponse(ProductDTO.from(savedProduct));
    }

    public ProductPageResponse getProductPage(ProductGetByCategoryRequest request) {
        Category category = categoryService.findCategoryBySlugOrThrow(
                request.getCategorySlug()
        );
        if (category.isDeleted()) {
            throw new DeletedCategoryException(request.getCategorySlug());
        }

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<Product> productPage = productRepository.findByCategoryAndIsDeletedFalse(category, pageable);

        log.info("[ProductService] Successfully got product page");

        return ProductPageResponse.from(productPage);
    }

    public ProductSelectionResponse getProductSelections() {
        List<ProductSelectionDTO> productSelectionDTOs = productRepository
                .findByIsDeletedFalseAndIsActivatedTrue()
                .stream()
                .map(
                        product -> new ProductSelectionDTO(product.getId(), product.getName())
                )
                .toList();

        log.info("[ProductService] Got {} selective products", productSelectionDTOs.size());

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

        log.info(
                "[ProductService] Successfully set new activated status: {} to product id={}",
                activated, productId
        );

        return new ProductUpdateResponse(message);
    }

    public ProductUpdateResponse deleteProduct(Long productId) {
        Product product = findProductOrThrow(productId);
        if (product.isDeleted()) {
            throw new DeletedProductException(productId);
        }
        product.setDeleted(true);

        productRepository.save(product);

        log.info("[ProductService] Successfully deleted product id={}", productId);

        return new ProductUpdateResponse("Xóa sản phẩm thành công: " + productId);
    }

    private Map<Long, ProductRatingStatsProjection> getRatingMap(List<Long> ids) {
        return productRepository.getRatingStatsByProductIds(ids)
                .stream()
                .collect(Collectors.toMap(
                        ProductRatingStatsProjection::getProductId, r -> r)
                );
    }

    private Map<Long, ProductSoldCountProjection> getSoldMap(List<Long> ids) {
        return productRepository.getSoldCountByProductIds(ids)
                .stream()
                .collect(Collectors.toMap(
                        ProductSoldCountProjection::getProductId, s -> s)
                );
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
        Category category = categoryService.findCategoryBySlugOrThrow(categorySlug);
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

        log.info("[ProductService] Successfully got {} displayable products", productDTOs.size());

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

        log.info("[ProductService] Successfully got product slug={}", productSlug);

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

        log.info("[ProductService] Successfully got product stats");

        return new ProductStatsResponse(productStatsDTOs);
    }
}