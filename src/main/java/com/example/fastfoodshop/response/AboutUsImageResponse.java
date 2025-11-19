package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.ImageDTO;
import com.example.fastfoodshop.entity.Image;
import com.example.fastfoodshop.enums.SectionType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AboutUsImageResponse {
    private List<ImageDTO> carouselImages = new ArrayList<>();
    private List<ImageDTO> showcaseImages = new ArrayList<>();
    private List<ImageDTO> missionImages = new ArrayList<>();

    public AboutUsImageResponse(List<Image> images) {
        for (Image image : images) {
            if (image.getSectionType() == SectionType.CAROUSEL) {
                this.carouselImages.add(new ImageDTO(image));
            } else if (image.getSectionType() == SectionType.SHOWCASE) {
                this.showcaseImages.add(new ImageDTO(image));
            } else if (image.getSectionType() == SectionType.MISSION) {
                this.missionImages.add(new ImageDTO(image));
            }
        }
    }
}
