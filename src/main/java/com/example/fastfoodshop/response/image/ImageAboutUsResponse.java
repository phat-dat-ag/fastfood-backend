package com.example.fastfoodshop.response.image;

import com.example.fastfoodshop.dto.ImageDTO;
import com.example.fastfoodshop.enums.SectionType;

import java.util.ArrayList;
import java.util.List;

public record ImageAboutUsResponse(
        List<ImageDTO> carouselImages,
        List<ImageDTO> showcaseImages,
        List<ImageDTO> missionImages
) {
    public static ImageAboutUsResponse from(List<ImageDTO> imageDTOs) {
        List<ImageDTO> carouselImages = new ArrayList<>();
        List<ImageDTO> showcaseImages = new ArrayList<>();
        List<ImageDTO> missionImages = new ArrayList<>();

        for (ImageDTO imageDTO : imageDTOs) {
            if (imageDTO.sectionType() == SectionType.CAROUSEL) {
                carouselImages.add(imageDTO);
            } else if (imageDTO.sectionType() == SectionType.SHOWCASE) {
                showcaseImages.add(imageDTO);
            } else if (imageDTO.sectionType() == SectionType.MISSION) {
                missionImages.add(imageDTO);
            }
        }

        return new ImageAboutUsResponse(carouselImages, showcaseImages, missionImages);
    }
}
