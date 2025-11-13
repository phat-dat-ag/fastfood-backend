package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Image;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class ImageDTO {
    private Long id;
    private String imageUrl;
    private String alternativeText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ImageDTO(Image image) {
        this.id = image.getId();
        this.imageUrl = image.getUrl();
        this.alternativeText = image.getAlternativeText();
        this.createdAt = image.getCreatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        ;
        this.updatedAt = image.getUpdatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        ;
    }
}
