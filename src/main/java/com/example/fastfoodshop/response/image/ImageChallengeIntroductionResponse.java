package com.example.fastfoodshop.response.image;

import com.example.fastfoodshop.dto.ImageDTO;
import com.example.fastfoodshop.enums.SectionType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImageChallengeIntroductionResponse {
    private List<ImageDTO> carouselImages = new ArrayList<>();

    public ImageChallengeIntroductionResponse(List<ImageDTO> imageDTOs) {
        for (ImageDTO imageDTO : imageDTOs) {
            if (imageDTO.sectionType() == SectionType.CAROUSEL) {
                this.carouselImages.add(imageDTO);
            }
        }
    }
}
