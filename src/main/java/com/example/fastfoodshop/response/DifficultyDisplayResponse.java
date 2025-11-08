package com.example.fastfoodshop.response;

import lombok.Data;

@Data
public class DifficultyDisplayResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private int duration;
    private int questionCount;
    private int minCorrectToReward;

    public DifficultyDisplayResponse(Long id, String name, String slug, String description,
                                     int duration, int questionCount, int minCorrectToReward
    ) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.duration = duration;
        this.questionCount = questionCount;
        this.minCorrectToReward = minCorrectToReward;
    }
}
