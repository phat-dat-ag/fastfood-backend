package com.example.fastfoodshop.response.image;

import com.example.fastfoodshop.dto.ImageDTO;

import java.util.List;

public record ImageAboutUsResponse(
        List<ImageDTO> carouselImages,
        List<ImageDTO> showcaseImages,
        List<ImageDTO> missionImages
) {
}
