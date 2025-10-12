package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.repository.ProductRepository;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final CloudinaryService cloudinaryService;
    private final CategoryService categoryService;
    private final ProductRepository productRepository;

    public String generateUniqueSlug(String name) {
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

    public void handleProductImage(Product product, MultipartFile imageFile) {
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

    public ResponseEntity<ResponseWrapper<ProductDTO>> createProduct(
            Long category_id, String name, String description, int price, boolean activated, MultipartFile imageFile) {
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
            Product savedProduct = productRepository.save(product);
            return ResponseEntity.ok(ResponseWrapper.success(new ProductDTO(savedProduct)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error("CREATE_PRODUCT_FAILED", "Lỗi tạo sản phẩm " + e.getMessage()));
        }
    }

    public ResponseEntity<ResponseWrapper<ProductDTO>> updateProduct(Long id, String name, String description, boolean isActivated, MultipartFile imageFile) {
        try {
            Product product = findProductOrThrow(id);
            product.setName(name);
            product.setDescription(description);
            product.setActivated(isActivated);

            handleProductImage(product, imageFile);

            Product savedProduct = productRepository.save(product);
            return ResponseEntity.ok(ResponseWrapper.success(new ProductDTO(savedProduct)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "UPDATE_PRODUCT_FAILED",
                    "Lỗi cập nhật thông tin sản phẩm"
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<ArrayList<ProductDTO>>> getProducts() {
        try {
            ArrayList<Product> products = productRepository.findByIsDeletedFalse();
            ArrayList<ProductDTO> productDTOs = new ArrayList<>();
            for (Product product : products) {
                productDTOs.add(new ProductDTO(product));
            }
            return ResponseEntity.ok(ResponseWrapper.success(productDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_PRODUCT_FAILED",
                    "Lỗi lấy sản phẩm: " + e.getMessage())
            );
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
