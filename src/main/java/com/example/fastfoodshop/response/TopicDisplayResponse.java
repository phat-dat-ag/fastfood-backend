package com.example.fastfoodshop.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TopicDisplayResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;

    private List<DifficultyDisplayResponse> difficulties = new ArrayList<>();

    public TopicDisplayResponse(Long id, String name, String slug, String description) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
    }
}
