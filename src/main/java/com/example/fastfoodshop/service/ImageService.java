package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Image;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.PageType;
import com.example.fastfoodshop.projection.ItemPromotionProjection;
import com.example.fastfoodshop.repository.ImageRepository;
import com.example.fastfoodshop.repository.PromotionRepository;
import com.example.fastfoodshop.request.ImageCreateRequest;
import com.example.fastfoodshop.response.AboutUsImageResponse;
import com.example.fastfoodshop.response.ChallengeIntroductionImageResponse;
import com.example.fastfoodshop.response.ItemPromotionResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final CloudinaryService cloudinaryService;
    private final UserService userService;
    private final PromotionRepository promotionRepository;
    private final ImageRepository imageRepository;

    private Image findImageOrThrow(Long imageId) {
        return imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh"));
    }

    private void handleUploadImage(Image image, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty())
            return;

        String folderName = image.getPageType().name() + "/" + image.getSectionType().name();
        Map<?, ?> result = cloudinaryService.uploadImage(imageFile, folderName);

        image.setUrl((String) result.get("secure_url"));
        image.setPublicId((String) result.get("public_id"));
    }

    public ResponseEntity<ResponseWrapper<String>> uploadImage(String phone, ImageCreateRequest imageCreateRequest) {
        try {
            User user = userService.findUserOrThrow(phone);

            Image image = new Image();
            image.setUser(user);
            image.setAlternativeText(imageCreateRequest.getAlternativeText());
            image.setPageType(imageCreateRequest.getPageType());
            image.setSectionType(imageCreateRequest.getSectionType());
            handleUploadImage(image, imageCreateRequest.getImageFile());

            imageRepository.save(image);
            return ResponseEntity.ok(ResponseWrapper.success("Đã lưu ảnh thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "UPLOAD_IMAGE_FAILED",
                    "Lỗi tải ảnh " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<AboutUsImageResponse>> getAboutUsPageImages() {
        try {
            List<Image> images = imageRepository.findByPageType(PageType.ABOUT_US);
            return ResponseEntity.ok(ResponseWrapper.success(new AboutUsImageResponse(images)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ABOUT_US_PAGE_IMAGE_FAILED",
                    "Lỗi lấy các ảnh trong trang về chúng tôi " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<ChallengeIntroductionImageResponse>> getChallengeIntroductionImages() {
        try {
            List<Image> images = imageRepository.findByPageType(PageType.CHALLENGE);
            return ResponseEntity.ok(ResponseWrapper.success(new ChallengeIntroductionImageResponse(images)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_CHALLENGE_INTRODUCTION_PAGE_IMAGE_FAILED",
                    "Lỗi lấy các ảnh trong trang giới thiệu thử thách " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<ItemPromotionResponse>> getItemPromotionImages() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<ItemPromotionProjection> categoryProjections = promotionRepository.getDisplayableCategoryPromotionsLimited4(now);
            List<ItemPromotionProjection> productProjections = promotionRepository.getDisplayableProductPromotionsLimited4(now);

            return ResponseEntity.ok(ResponseWrapper.success(new ItemPromotionResponse(categoryProjections, productProjections)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ITEM_PROMOTION_PAGE_IMAGE_FAILED",
                    "Lỗi lấy các ảnh trong trang giới thiệu khuyến mãi " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<String>> deleteImage(Long imageId) {
        try {
            Image image = findImageOrThrow(imageId);
            cloudinaryService.deleteImage(image.getPublicId());
            imageRepository.delete(image);

            return ResponseEntity.ok(ResponseWrapper.success("Xóa ảnh thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "DELETE_IMAGE_FAILED",
                    "Lỗi xóa ảnh " + e.getMessage()
            ));
        }
    }
}
