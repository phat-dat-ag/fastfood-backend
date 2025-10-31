package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Review;
import com.example.fastfoodshop.entity.ReviewImage;
import com.example.fastfoodshop.repository.ReviewImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewImageService {
    private final CloudinaryService cloudinaryService;
    private final ReviewImageRepository reviewImageRepository;

    private void handleReviewImage(ReviewImage reviewImage, MultipartFile imageFile) {
        Map<?, ?> result = cloudinaryService.uploadImage(imageFile, "review");

        reviewImage.setImageUrl((String) result.get("secure_url"));
        reviewImage.setImagePublicId((String) result.get("public_id"));
    }

    public ArrayList<ReviewImage> createReviewImages(List<MultipartFile> images, Review review) {
        ArrayList<ReviewImage> reviewImages = new ArrayList<>();

        if (images == null || images.isEmpty()) return reviewImages;

        for (MultipartFile imageFile : images) {
            ReviewImage reviewImage = new ReviewImage();
            reviewImage.setReview(review);
            handleReviewImage(reviewImage, imageFile);
            reviewImages.add(reviewImage);
        }

        return reviewImages;
    }
}
