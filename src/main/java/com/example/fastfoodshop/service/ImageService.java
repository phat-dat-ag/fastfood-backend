package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Image;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.PageType;
import com.example.fastfoodshop.repository.ImageRepository;
import com.example.fastfoodshop.request.ImageCreateRequest;
import com.example.fastfoodshop.response.AboutUseImageResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final CloudinaryService cloudinaryService;
    private final UserService userService;
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

    public ResponseEntity<ResponseWrapper<AboutUseImageResponse>> getAboutUsPageImages() {
        try {
            List<Image> images = imageRepository.findByPageType(PageType.ABOUT_US);
            return ResponseEntity.ok(ResponseWrapper.success(new AboutUseImageResponse(images)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ABOUT_US_PAGE_IMAGE_FAILED",
                    "Lỗi lấy các ảnh trong trang về chúng tôi " + e.getMessage()
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
