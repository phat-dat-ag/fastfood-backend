package com.example.fastfoodshop.response.image;

import com.example.fastfoodshop.dto.ImageDTO;

import java.util.List;

public record ImageChallengeIntroductionResponse(
        List<ImageDTO> carouselImages
) {
}
