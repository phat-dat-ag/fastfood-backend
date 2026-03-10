package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.TopicDifficultyDTO;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.request.TopicDifficultyCreateRequest;
import com.example.fastfoodshop.request.TopicDifficultyGetByTopicRequest;
import com.example.fastfoodshop.request.TopicDifficultyUpdateRequest;
import com.example.fastfoodshop.response.TopicDifficultyResponse;

public interface TopicDifficultyService {
    TopicDifficulty findValidTopicDifficultyOrThrow(String topicDifficultySlug);

    TopicDifficulty findPlayableTopicDifficultyBySlug(String topicDifficultySlug);

    TopicDifficultyDTO createTopicDifficulty(String topicSlug, TopicDifficultyCreateRequest topicDifficultyCreateRequest);

    TopicDifficultyDTO updateTopicDifficulty(Long topicDifficultyId, TopicDifficultyUpdateRequest topicDifficultyUpdateRequest);

    TopicDifficultyDTO getTopicDifficultyBySlug(String topicDifficultySlug);

    TopicDifficultyResponse getAllTopicDifficultiesByTopic(
            TopicDifficultyGetByTopicRequest topicDifficultyGetByTopicRequest
    );

    String activateTopicDifficulty(Long topicDifficultyId);

    String deactivateTopicDifficulty(Long topicDifficultyId);

    TopicDifficultyDTO deleteTopicDifficulty(Long topicDifficultyId);
}
