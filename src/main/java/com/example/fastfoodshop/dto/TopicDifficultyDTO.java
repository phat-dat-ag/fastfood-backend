package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.TopicDifficulty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class TopicDifficultyDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private int duration;
    private int questionCount;
    private int minCorrectToReward;
    private boolean isActivated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TopicDifficultyDTO(TopicDifficulty topicDifficulty) {
        this.id = topicDifficulty.getId();
        this.name = topicDifficulty.getName();
        this.slug = topicDifficulty.getSlug();
        this.description = topicDifficulty.getDescription();
        this.duration = topicDifficulty.getDuration();
        this.questionCount = topicDifficulty.getQuestionCount();
        this.minCorrectToReward = topicDifficulty.getMinCorrectToReward();
        this.isActivated = topicDifficulty.isActivated();
        this.createdAt = topicDifficulty.getCreatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        ;
        this.updatedAt = topicDifficulty.getUpdatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        ;
    }
}
