package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.TopicDTO;
import com.example.fastfoodshop.entity.Topic;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.TopicDisplayResponse;
import com.example.fastfoodshop.response.TopicResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TopicService {
    Topic findValidTopicOrThrow(String topicSlug);

    ResponseEntity<ResponseWrapper<TopicDTO>> createTopic(String name, String description, boolean isActivated);

    ResponseEntity<ResponseWrapper<TopicDTO>> updateTopic(Long topicId, String name, String description, boolean isActivated);

    ResponseEntity<ResponseWrapper<TopicDTO>> getTopicBySlug(String slug);

    ResponseEntity<ResponseWrapper<TopicResponse>> getAllTopics(int page, int size);

    ResponseEntity<ResponseWrapper<List<TopicDisplayResponse>>> getDisplayableTopics();

    ResponseEntity<ResponseWrapper<String>> activateTopic(Long topicId);

    ResponseEntity<ResponseWrapper<String>> deactivateTopic(Long topicId);

    ResponseEntity<ResponseWrapper<TopicDTO>> deleteTopic(Long topicId);
}
