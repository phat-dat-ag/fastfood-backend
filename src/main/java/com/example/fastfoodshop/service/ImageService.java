package com.example.fastfoodshop.service;

import com.example.fastfoodshop.request.ImageCreateRequest;
import com.example.fastfoodshop.response.AboutUsImageResponse;
import com.example.fastfoodshop.response.ChallengeIntroductionImageResponse;
import com.example.fastfoodshop.response.ItemPromotionResponse;

public interface ImageService {
    String uploadImage(String phone, ImageCreateRequest imageCreateRequest);

    AboutUsImageResponse getAboutUsPageImages();

    ChallengeIntroductionImageResponse getChallengeIntroductionImages();

    ItemPromotionResponse getItemPromotionImages();

    String deleteImage(Long imageId);
}
