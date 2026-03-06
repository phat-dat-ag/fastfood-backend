package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.TopicDifficultyDTO;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.request.TopicDifficultyCreateRequest;
import com.example.fastfoodshop.request.TopicDifficultyUpdateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.TopicDifficultyResponse;
import org.springframework.http.ResponseEntity;

public interface TopicDifficultyService {
    TopicDifficulty findValidTopicDifficultyOrThrow(String topicDifficultySlug);

    TopicDifficulty findPlayableTopicDifficultyBySlug(String topicDifficultySlug);

    ResponseEntity<ResponseWrapper<TopicDifficultyDTO>> createTopicDifficulty(String topicSlug, TopicDifficultyCreateRequest request);

    ResponseEntity<ResponseWrapper<TopicDifficultyDTO>> updateTopicDifficulty(Long id, TopicDifficultyUpdateRequest request);

    ResponseEntity<ResponseWrapper<TopicDifficultyDTO>> getTopicDifficultyBySlug(String slug);

    ResponseEntity<ResponseWrapper<TopicDifficultyResponse>> getAllTopicDifficultiesByTopic(String topicSlug, int page, int size);

    ResponseEntity<ResponseWrapper<String>> activateTopicDifficulty(Long topicDifficultyId);

    ResponseEntity<ResponseWrapper<String>> deactivateTopicDifficulty(Long topicDifficultyId);

    ResponseEntity<ResponseWrapper<TopicDifficultyDTO>> deleteTopicDifficulty(Long topicDifficultyId);
}
