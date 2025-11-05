package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Topic;
import com.example.fastfoodshop.entity.TopicDifficulty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Data
public class TopicDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private boolean isActivated;
    private List<TopicDifficultyDTO> topicDifficulties = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TopicDTO(Topic topic) {
        this.id = topic.getId();
        this.name = topic.getName();
        this.slug = topic.getSlug();
        this.description = topic.getDescription();
        this.isActivated = topic.isActivated();
        for (TopicDifficulty topicDifficulty : topic.getTopicDifficulties()) {
            this.topicDifficulties.add(new TopicDifficultyDTO(topicDifficulty));
        }
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
