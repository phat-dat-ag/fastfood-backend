package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.request.TopicDifficultyCreateRequest;
import com.example.fastfoodshop.request.TopicDifficultyGetByTopicRequest;
import com.example.fastfoodshop.request.TopicDifficultyUpdateRequest;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyPageResponse;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyResponse;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyUpdateResponse;

public interface TopicDifficultyService {
    TopicDifficulty findValidTopicDifficultyOrThrow(String topicDifficultySlug);

    TopicDifficulty findPlayableTopicDifficultyBySlug(String topicDifficultySlug);

    TopicDifficultyUpdateResponse createTopicDifficulty(
            String topicSlug, TopicDifficultyCreateRequest topicDifficultyCreateRequest
    );

    TopicDifficultyUpdateResponse updateTopicDifficulty(
            Long topicDifficultyId, TopicDifficultyUpdateRequest topicDifficultyUpdateRequest
    );

    TopicDifficultyResponse getTopicDifficultyBySlug(String topicDifficultySlug);

    TopicDifficultyPageResponse getAllTopicDifficultiesByTopic(
            TopicDifficultyGetByTopicRequest topicDifficultyGetByTopicRequest
    );

    TopicDifficultyUpdateResponse activateTopicDifficulty(Long topicDifficultyId);

    TopicDifficultyUpdateResponse deactivateTopicDifficulty(Long topicDifficultyId);

    TopicDifficultyUpdateResponse deleteTopicDifficulty(Long topicDifficultyId);
}
