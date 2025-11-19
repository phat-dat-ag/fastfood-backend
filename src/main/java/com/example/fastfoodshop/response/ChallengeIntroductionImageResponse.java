package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.ImageDTO;
import com.example.fastfoodshop.entity.Image;
import com.example.fastfoodshop.enums.SectionType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChallengeIntroductionImageResponse {
    private List<ImageDTO> carouselImages = new ArrayList<>();

    public ChallengeIntroductionImageResponse(List<Image> images) {
        for (Image image : images) {
            if (image.getSectionType() == SectionType.CAROUSEL) {
                this.carouselImages.add(new ImageDTO(image));
            }
        }
    }
}
