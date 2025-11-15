package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.dto.ReviewDTO;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.Review;
import com.example.fastfoodshop.projection.ProductRatingStatsProjection;
import com.example.fastfoodshop.projection.ProductSoldCountProjection;
import com.example.fastfoodshop.repository.ProductRepository;
import com.example.fastfoodshop.repository.ReviewRepository;
import com.example.fastfoodshop.response.ProductResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final CloudinaryService cloudinaryService;
    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtils.toSlug(name);
        String uniqueSlug = baseSlug;
        int counter = 1;

        while (productRepository.existsBySlug((uniqueSlug))) {
            uniqueSlug = baseSlug + "-" + counter++;
        }
        return uniqueSlug;
    }

    public Product findProductOrThrow(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
    }

    public Product findProductOrThrow(String slug) {
        return productRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
    }

    private Product findActivatedProductOrThrow(Long id) {
        return productRepository.findByIdAndIsActivatedTrueAndIsDeletedFalse(id).orElseThrow(
                () -> new RuntimeException("Không tìm thấy sản phẩm này đang được kích hoạt")
        );
    }

    private Product findDeactivatedProductOrThrow(Long id) {
        return productRepository.findByIdAndIsActivatedFalseAndIsDeletedFalse(id).orElseThrow(
                () -> new RuntimeException("Không tìm thấy sản phẩm này đang bị hủy kích hoạt")
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
            } catch (Exception e) {
                System.out.println("Ngoai lệ khi dọn ảnh sản phẩm cũ: " + e.getMessage());
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
            } catch (Exception e) {
                System.out.println("Ngoai lệ khi dọn mô hình 3D sản phẩm cũ: " + e.getMessage());
            }
        }
    }

    public ResponseEntity<ResponseWrapper<ProductDTO>> createProduct(
            Long category_id, String name, String description, int price, boolean activated,
            MultipartFile imageFile, MultipartFile modelFile
    ) {
        try {
            Category category = categoryService.findCategoryOrThrow(category_id);

            String slug = generateUniqueSlug(name);

            Product product = new Product();
            product.setCategory(category);
            product.setName(name);
            product.setSlug(slug);
            product.setDescription(description);
            product.setPrice(price);
            product.setActivated(activated);
            product.setDeleted(false);

            handleProductImage(product, imageFile);
            handleProductModel3D(product, modelFile);
            Product savedProduct = productRepository.save(product);
            return ResponseEntity.ok(ResponseWrapper.success(new ProductDTO(savedProduct)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "CREATE_PRODUCT_FAILED",
                            "Lỗi tạo sản phẩm " + e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<ProductDTO>> updateProduct(
            Long id, String name, String description, boolean isActivated,
            MultipartFile imageFile, MultipartFile modelFile
    ) {
        try {
            Product product = findProductOrThrow(id);
            product.setName(name);
            product.setDescription(description);
            product.setActivated(isActivated);

            handleProductImage(product, imageFile);
            handleProductModel3D(product, modelFile);

            Product savedProduct = productRepository.save(product);
            return ResponseEntity.ok(ResponseWrapper.success(new ProductDTO(savedProduct)));
        } catch (Exception e) {
            System.out.println("Lỗi cập nhật SP: " + e.getMessage());
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "UPDATE_PRODUCT_FAILED",
                            "Lỗi cập nhật thông tin sản phẩm"
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<ProductResponse>> getAllProductsByCategory(
            String categorySlug, int page, int size
    ) {
        try {
            Category category = categoryService.findUndeletedCategoryOrThrow(categorySlug);
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> productPage = productRepository.findByCategoryAndIsDeletedFalse(category, pageable);

            return ResponseEntity.ok(ResponseWrapper.success(new ProductResponse(productPage)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ALL_PRODUCT_BY_CATEGORY_FAILED",
                    "Lỗi lấy tất cả sản phẩm theo danh mục: " + e.getMessage())
            );
        }
    }

    public ResponseEntity<ResponseWrapper<ArrayList<ProductDTO>>> getAllDisplayableProductsByCategory(String categorySlug) {
        try {
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
                ProductDTO productDTO = new ProductDTO(product);

                ProductRatingStatsProjection r = ratingMap.get(product.getId());
                if (r != null) {
                    productDTO.setAverageRating(r.getAvgRating() != null ? r.getAvgRating() : 0.0);
                    productDTO.setReviewCount(r.getReviewCount());
                }

                ProductSoldCountProjection s = soldMap.get(product.getId());
                productDTO.setSoldCount(s != null ? s.getSoldCount() : 0L);

                categoryService.applyPromotion(productDTO, category);
                productDTOs.add(productDTO);
            }
            return ResponseEntity.ok(ResponseWrapper.success(productDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ALL_DISPLAYABLE_PRODUCT_BY_CATEGORY_FAILED",
                    "Lỗi lấy sản phẩm trưng bày của danh mục: " + e.getMessage())
            );
        }
    }

    public ResponseEntity<ResponseWrapper<ArrayList<ProductDTO>>> getAllDisplayableProducts() {
        try {
            List<Product> products = productRepository.findByIsDeletedFalseAndIsActivatedTrue();

            ArrayList<ProductDTO> productDTOs = new ArrayList<>();
            for (Product product : products) {
                ProductDTO productDTO = new ProductDTO(product);
                categoryService.applyPromotion(productDTO, product.getCategory());
                productDTOs.add(productDTO);
            }
            return ResponseEntity.ok(ResponseWrapper.success(productDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ALL_DISPLAYABLE_PRODUCTS_FAILED",
                    "Lỗi lấy sản phẩm trưng bày cho người dùng: " + e.getMessage())
            );
        }
    }

    public ResponseEntity<ResponseWrapper<ProductDTO>> getProductBySlug(String slug) {
        try {
            Product product = findProductOrThrow(slug);
            Category category = product.getCategory();
            ProductDTO productDTO = new ProductDTO(product);

            ProductRatingStatsProjection ratingStats = productRepository.getRatingStatsByProductIds(
                    List.of(product.getId())
            ).stream().findFirst().orElse(null);
            if (ratingStats != null) {
                productDTO.setAverageRating(ratingStats.getAvgRating() != null ? ratingStats.getAvgRating() : 0.0);
                productDTO.setReviewCount(ratingStats.getReviewCount());
            }

            ProductSoldCountProjection soldStats = productRepository.getSoldCountByProductIds(
                    List.of(product.getId())
            ).stream().findFirst().orElse(null);
            productDTO.setSoldCount(soldStats != null ? soldStats.getSoldCount() : 0L);

            Pageable top5 = PageRequest.of(0, 5);
            List<Review> topReviews = reviewRepository.findTop5ByProductIdOrderByRatingDescCreatedAtDesc(
                    product.getId(), top5
            );
            List<ReviewDTO> reviewDTOs = topReviews.stream()
                    .map(ReviewDTO::new)
                    .toList();
            productDTO.setReviews(new ArrayList<>(reviewDTOs));

            categoryService.applyPromotion(productDTO, category);
            return ResponseEntity.ok(ResponseWrapper.success(productDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_PRODUCT_FAILED",
                    "Lỗi lấy sản phẩm: " + e.getMessage())
            );
        }
    }

    public ResponseEntity<ResponseWrapper<String>> activateProduct(Long id) {
        try {
            Product product = findDeactivatedProductOrThrow(id);
            product.setActivated(true);

            productRepository.save(product);
            return ResponseEntity.ok(ResponseWrapper.success("Kích hoạt sản phẩm thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "ACTIVATE_PRODUCT_FAILED",
                    "Lỗi kích hoạt sản phẩm " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<String>> deactivateProduct(Long id) {
        try {
            Product product = findActivatedProductOrThrow(id);
            product.setActivated(false);

            productRepository.save(product);
            return ResponseEntity.ok(ResponseWrapper.success("Hủy kích hoạt sản phẩm thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "DEACTIVATE_PRODUCT_FAILED",
                    "Lỗi hủy kích hoạt sản phẩm " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<ProductDTO>> deleteCategory(Long id) {
        try {
            Product product = findProductOrThrow(id);
            product.setDeleted(true);

            Product deletedProduct = productRepository.save(product);
            return ResponseEntity.ok(ResponseWrapper.success(new ProductDTO(deletedProduct)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "DELETE_PRODUCT_FAILED",
                    "Lỗi xóa sản phẩm " + e.getMessage())
            );
        }
    }
}
