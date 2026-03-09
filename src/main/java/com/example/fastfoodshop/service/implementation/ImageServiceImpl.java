package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.entity.Image;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.PageType;
import com.example.fastfoodshop.exception.image.ImageNotFoundException;
import com.example.fastfoodshop.projection.ItemPromotionProjection;
import com.example.fastfoodshop.repository.ImageRepository;
import com.example.fastfoodshop.repository.PromotionRepository;
import com.example.fastfoodshop.request.ImageCreateRequest;
import com.example.fastfoodshop.response.AboutUsImageResponse;
import com.example.fastfoodshop.response.ChallengeIntroductionImageResponse;
import com.example.fastfoodshop.response.ItemPromotionResponse;
import com.example.fastfoodshop.service.CloudinaryService;
import com.example.fastfoodshop.service.ImageService;
import com.example.fastfoodshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final CloudinaryService cloudinaryService;
    private final UserService userService;
    private final PromotionRepository promotionRepository;
    private final ImageRepository imageRepository;

    private Image findImageOrThrow(Long imageId) {
        return imageRepository.findById(imageId).orElseThrow(() -> new ImageNotFoundException(imageId));
    }

    private void handleUploadImage(Image image, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty())
            return;

        String folderName = image.getPageType().name() + "/" + image.getSectionType().name();
        Map<?, ?> result = cloudinaryService.uploadImage(imageFile, folderName);

        image.setUrl((String) result.get("secure_url"));
        image.setPublicId((String) result.get("public_id"));
    }

    public String uploadImage(String phone, ImageCreateRequest imageCreateRequest) {
        User user = userService.findUserOrThrow(phone);

        Image image = new Image();
        image.setUser(user);
        image.setAlternativeText(imageCreateRequest.getAlternativeText());
        image.setPageType(imageCreateRequest.getPageType());
        image.setSectionType(imageCreateRequest.getSectionType());
        handleUploadImage(image, imageCreateRequest.getImageFile());

        imageRepository.save(image);
        return "Đã lưu ảnh thành công";
    }

    public AboutUsImageResponse getAboutUsPageImages() {
        List<Image> images = imageRepository.findByPageType(PageType.ABOUT_US);
        return new AboutUsImageResponse(images);
    }

    public ChallengeIntroductionImageResponse getChallengeIntroductionImages() {
        List<Image> images = imageRepository.findByPageType(PageType.CHALLENGE);
        return new ChallengeIntroductionImageResponse(images);
    }

    public ItemPromotionResponse getItemPromotionImages() {
        LocalDateTime now = LocalDateTime.now();
        List<ItemPromotionProjection> categoryProjections = promotionRepository.getDisplayableCategoryPromotionsLimited4(now);
        List<ItemPromotionProjection> productProjections = promotionRepository.getDisplayableProductPromotionsLimited4(now);

        return new ItemPromotionResponse(categoryProjections, productProjections);
    }

    public String deleteImage(Long imageId) {
        Image image = findImageOrThrow(imageId);
        cloudinaryService.deleteImage(image.getPublicId());
        imageRepository.delete(image);

        return "Xóa ảnh thành công";
    }
}
