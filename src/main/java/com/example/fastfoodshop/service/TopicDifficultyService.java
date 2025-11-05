package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.repository.TopicDifficultyRepository;
import com.example.fastfoodshop.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopicDifficultyService {
    private final TopicDifficultyRepository topicDifficultyRepository;

    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtils.toSlug(name);
        String uniqueSlug = baseSlug;
        int counter = 1;

        while (topicDifficultyRepository.existsBySlug((uniqueSlug))) {
            uniqueSlug = baseSlug + "-" + counter++;
        }
        return uniqueSlug;
    }

    private TopicDifficulty findTopicDifficultyOrThrow(Long topicDifficultyId) {
        return topicDifficultyRepository.findById(topicDifficultyId).orElseThrow(() -> new RuntimeException("Không tìm thấy độ khó của chủ đề"));
    }
}
