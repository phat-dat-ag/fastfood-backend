package com.example.fastfoodshop.service;

import com.example.fastfoodshop.request.ImageCreateRequest;
import com.example.fastfoodshop.response.image.ImageAboutUsResponse;
import com.example.fastfoodshop.response.image.ImageChallengeIntroductionResponse;
import com.example.fastfoodshop.response.image.ImageUpdateResponse;

public interface ImageService {
    ImageUpdateResponse uploadImage(String phone, ImageCreateRequest imageCreateRequest);

    ImageAboutUsResponse getAboutUsPageImages();

    ImageChallengeIntroductionResponse getChallengeIntroductionImages();

    ImageUpdateResponse deleteImage(Long imageId);
}
