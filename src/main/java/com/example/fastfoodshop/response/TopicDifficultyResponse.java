package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.TopicDifficultyDTO;
import com.example.fastfoodshop.entity.TopicDifficulty;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class TopicDifficultyResponse {
    List<TopicDifficultyDTO> topicDifficulties = new ArrayList<>();
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public TopicDifficultyResponse(Page<TopicDifficulty> page) {
        for (TopicDifficulty topicDifficulty : page.getContent()) {
            this.topicDifficulties.add(new TopicDifficultyDTO(topicDifficulty));
        }
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
        this.totalItems = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
