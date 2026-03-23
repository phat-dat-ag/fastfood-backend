package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.request.TopicDifficultyCreateRequest;
import com.example.fastfoodshop.request.TopicDifficultyUpdateRequest;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyPageResponse;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyResponse;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyUpdateResponse;

public interface TopicDifficultyService {
    TopicDifficultyUpdateResponse createTopicDifficultyFromTopic(
            String topicSlug, TopicDifficultyCreateRequest topicDifficultyCreateRequest
    );

    TopicDifficultyPageResponse getAllTopicDifficultiesByTopic(String topicSlug, int page, int size);

    TopicDifficultyUpdateResponse updateTopicDifficulty(
            Long topicDifficultyId, TopicDifficultyUpdateRequest topicDifficultyUpdateRequest
    );

    TopicDifficultyUpdateResponse updateTopicDifficultyActivation(Long topicDifficultyId, boolean activated);

    TopicDifficultyResponse getTopicDifficultyBySlug(String topicDifficultySlug);

    TopicDifficultyUpdateResponse deleteTopicDifficulty(Long topicDifficultyId);

    TopicDifficulty findValidTopicDifficultyOrThrow(String topicDifficultySlug);

    TopicDifficulty findPlayableTopicDifficultyBySlug(String topicDifficultySlug);
}
