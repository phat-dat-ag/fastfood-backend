package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Topic;
import com.example.fastfoodshop.request.TopicCreateRequest;
import com.example.fastfoodshop.response.topic.TopicDisplayResponse;
import com.example.fastfoodshop.response.topic.TopicPageResponse;
import com.example.fastfoodshop.response.topic.TopicResponse;
import com.example.fastfoodshop.response.topic.TopicUpdateResponse;

public interface TopicService {
    Topic findValidTopicOrThrow(String topicSlug);

    TopicResponse createTopic(TopicCreateRequest topicCreateRequest);

    TopicResponse updateTopic(Long topicId, TopicCreateRequest topicCreateRequest);

    TopicResponse getTopicBySlug(String topicSlug);

    TopicPageResponse getAllTopics(int page, int size);

    TopicDisplayResponse getDisplayableTopics();

    TopicUpdateResponse activateTopic(Long topicId);

    TopicUpdateResponse deactivateTopic(Long topicId);

    TopicUpdateResponse deleteTopic(Long topicId);
}
