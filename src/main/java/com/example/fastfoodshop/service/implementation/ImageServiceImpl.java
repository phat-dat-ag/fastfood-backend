package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.entity.Image;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.image.ImageNotFoundException;
import com.example.fastfoodshop.repository.ImageRepository;
import com.example.fastfoodshop.request.ImageCreateRequest;
import com.example.fastfoodshop.response.image.ImageUpdateResponse;
import com.example.fastfoodshop.service.CloudinaryService;
import com.example.fastfoodshop.service.ImageService;
import com.example.fastfoodshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final CloudinaryService cloudinaryService;
    private final UserService userService;
    private final ImageRepository imageRepository;

    private void handleUploadImage(Image image, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty())
            return;

        String folderName = image.getPageType().name() + "/" + image.getSectionType().name();
        Map<?, ?> result = cloudinaryService.uploadImage(imageFile, folderName);

        image.setUrl((String) result.get("secure_url"));
        image.setPublicId((String) result.get("public_id"));
    }

    public ImageUpdateResponse uploadImage(String phone, ImageCreateRequest imageCreateRequest) {
        User user = userService.findUserOrThrow(phone);

        Image image = new Image();
        image.setUser(user);
        image.setAlternativeText(imageCreateRequest.alternativeText());
        image.setPageType(imageCreateRequest.pageType());
        image.setSectionType(imageCreateRequest.sectionType());
        handleUploadImage(image, imageCreateRequest.imageFile());

        imageRepository.save(image);
        return new ImageUpdateResponse("Đã lưu ảnh thành công");
    }

    private Image findImageOrThrow(Long imageId) {
        return imageRepository.findById(imageId).orElseThrow(() -> new ImageNotFoundException(imageId));
    }

    public ImageUpdateResponse deleteImage(Long imageId) {
        Image image = findImageOrThrow(imageId);
        cloudinaryService.deleteImage(image.getPublicId());
        imageRepository.delete(image);

        return new ImageUpdateResponse("Xóa ảnh thành công: " + imageId);
    }
}
