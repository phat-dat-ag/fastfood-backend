package com.example.fastfoodshop.response.topic_difficulty;

import com.example.fastfoodshop.dto.TopicDifficultyDTO;
import com.example.fastfoodshop.entity.TopicDifficulty;
import org.springframework.data.domain.Page;

import java.util.List;

public record TopicDifficultyPageResponse(
        List<TopicDifficultyDTO> topicDifficulties,
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages
) {
    public static TopicDifficultyPageResponse from(Page<TopicDifficulty> page) {
        List<TopicDifficultyDTO> topicDifficulties = page.getContent()
                .stream().map(TopicDifficultyDTO::from).toList();

        return new TopicDifficultyPageResponse(
                topicDifficulties,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
