package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.TopicDTO;
import com.example.fastfoodshop.entity.Topic;
import com.example.fastfoodshop.request.TopicCreateRequest;
import com.example.fastfoodshop.response.TopicDisplayResponse;
import com.example.fastfoodshop.response.TopicResponse;

import java.util.List;

public interface TopicService {
    Topic findValidTopicOrThrow(String topicSlug);

    TopicDTO createTopic(TopicCreateRequest topicCreateRequest);

    TopicDTO updateTopic(Long topicId, TopicCreateRequest topicCreateRequest);

    TopicDTO getTopicBySlug(String topicSlug);

    TopicResponse getAllTopics(int page, int size);

    List<TopicDisplayResponse> getDisplayableTopics();

    String activateTopic(Long topicId);

    String deactivateTopic(Long topicId);

    TopicDTO deleteTopic(Long topicId);
}
