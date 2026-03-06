package com.example.fastfoodshop.service;

import com.example.fastfoodshop.request.ImageCreateRequest;
import com.example.fastfoodshop.response.AboutUsImageResponse;
import com.example.fastfoodshop.response.ChallengeIntroductionImageResponse;
import com.example.fastfoodshop.response.ItemPromotionResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import org.springframework.http.ResponseEntity;

public interface ImageService {
    ResponseEntity<ResponseWrapper<String>> uploadImage(String phone, ImageCreateRequest imageCreateRequest);

    ResponseEntity<ResponseWrapper<AboutUsImageResponse>> getAboutUsPageImages();

    ResponseEntity<ResponseWrapper<ChallengeIntroductionImageResponse>> getChallengeIntroductionImages();

    ResponseEntity<ResponseWrapper<ItemPromotionResponse>> getItemPromotionImages();

    ResponseEntity<ResponseWrapper<String>> deleteImage(Long imageId);
}
