package com.example.fastfoodshop.service;

import com.example.fastfoodshop.response.image.ImageAboutUsResponse;
import com.example.fastfoodshop.response.image.ImageChallengeIntroductionResponse;

public interface PageImageService {
    ImageAboutUsResponse getAboutUsPageImages();

    ImageChallengeIntroductionResponse getChallengeIntroductionImages();
}
