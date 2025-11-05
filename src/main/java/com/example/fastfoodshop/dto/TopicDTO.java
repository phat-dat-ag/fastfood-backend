package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Topic;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class TopicDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private boolean isActivated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TopicDTO(Topic topic) {
        this.id = topic.getId();
        this.name = topic.getName();
        this.slug = topic.getSlug();
        this.description = topic.getDescription();
        this.isActivated = topic.isActivated();
        this.createdAt = topic.getCreatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        ;
        this.updatedAt = topic.getUpdatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        ;
    }
}
