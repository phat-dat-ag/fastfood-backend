package com.example.fastfoodshop.service;

import com.example.fastfoodshop.request.ImageCreateRequest;
import com.example.fastfoodshop.response.image.ImageUpdateResponse;

public interface ImageService {
    ImageUpdateResponse uploadImage(String phone, ImageCreateRequest imageCreateRequest);

    ImageUpdateResponse deleteImage(Long imageId);
}
