package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.TopicDTO;
import com.example.fastfoodshop.dto.TopicDifficultyFullDTO;
import com.example.fastfoodshop.entity.Topic;
import com.example.fastfoodshop.exception.topic.InvalidTopicStatusException;
import com.example.fastfoodshop.exception.topic.TopicNotFoundException;
import com.example.fastfoodshop.repository.TopicRepository;
import com.example.fastfoodshop.request.TopicCreateRequest;
import com.example.fastfoodshop.response.DifficultyDisplayResponse;
import com.example.fastfoodshop.response.TopicDisplayResponse;
import com.example.fastfoodshop.response.TopicResponse;
import com.example.fastfoodshop.service.TopicService;
import com.example.fastfoodshop.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;

    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtils.toSlug(name);
        String uniqueSlug = baseSlug;
        int counter = 1;

        while (topicRepository.existsBySlug((uniqueSlug))) {
            uniqueSlug = baseSlug + "-" + counter++;
        }
        return uniqueSlug;
    }

    private Topic findTopicOrThrow(Long topicId) {
        return topicRepository.findById(topicId).orElseThrow(() -> new TopicNotFoundException(topicId));
    }

    public Topic findValidTopicOrThrow(String topicSlug) {
        return topicRepository.findBySlugAndIsDeletedFalse(topicSlug).orElseThrow(
                () -> new TopicNotFoundException(topicSlug)
        );
    }

    private Topic findActivatedTopic(Long topicId) {
        return topicRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(topicId).orElseThrow(
                () -> new InvalidTopicStatusException(topicId)
        );
    }

    private Topic findDeactivatedTopic(Long topicId) {
        return topicRepository.findByIdAndIsDeletedFalseAndIsActivatedFalse(topicId).orElseThrow(
                () -> new InvalidTopicStatusException(topicId)
        );
    }

    public TopicDTO createTopic(TopicCreateRequest topicCreateRequest) {
        Topic topic = new Topic();
        topic.setName(topicCreateRequest.getName());
        topic.setDescription(topicCreateRequest.getDescription());
        topic.setActivated(topicCreateRequest.getIsActivated());
        topic.setDeleted(false);

        String slug = generateUniqueSlug(topicCreateRequest.getName());
        topic.setSlug(slug);

        Topic savedTopic = topicRepository.save(topic);
        return new TopicDTO(savedTopic);
    }

    public TopicDTO updateTopic(Long topicId, TopicCreateRequest topicCreateRequest) {
        Topic topic = findTopicOrThrow(topicId);
        topic.setName(topicCreateRequest.getName());
        topic.setDescription(topicCreateRequest.getDescription());
        topic.setActivated(topicCreateRequest.getIsActivated());

        Topic updatedTopic = topicRepository.save(topic);
        return new TopicDTO(updatedTopic);
    }

    public TopicDTO getTopicBySlug(String slug) {
        Topic topic = findValidTopicOrThrow(slug);
        return new TopicDTO(topic);
    }

    public TopicResponse getAllTopics(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Topic> topicPage = topicRepository.findByIsDeletedFalse(pageable);


        return new TopicResponse(topicPage);
    }

    public List<TopicDisplayResponse> getDisplayableTopics() {
        List<TopicDifficultyFullDTO> flatList = topicRepository.findDisplayableTopicsFull();
        Map<Long, TopicDisplayResponse> grouped = new LinkedHashMap<>();

        for (TopicDifficultyFullDTO dto : flatList) {
            grouped.computeIfAbsent(dto.topicId(), id ->
                    new TopicDisplayResponse(
                            dto.topicId(),
                            dto.topicName(),
                            dto.topicSlug(),
                            dto.topicDescription()
                    )
            ).getDifficulties().add(new DifficultyDisplayResponse(
                    dto.difficultyId(),
                    dto.difficultyName(),
                    dto.difficultySlug(),
                    dto.difficultyDescription(),
                    dto.duration(),
                    dto.questionCount(),
                    dto.minCorrectToReward()
            ));
        }
        return new ArrayList<>(grouped.values());
    }

    public String activateTopic(Long topicId) {
        Topic topic = findDeactivatedTopic(topicId);
        topic.setActivated(true);
        topicRepository.save(topic);

        return "Đã kích hoạt chủ đề";
    }

    public String deactivateTopic(Long topicId) {
        Topic topic = findActivatedTopic(topicId);
        topic.setActivated(false);
        topicRepository.save(topic);

        return "Đã hủy kích hoạt chủ đề";
    }

    public TopicDTO deleteTopic(Long topicId) {
        Topic topic = findTopicOrThrow(topicId);
        topic.setDeleted(true);

        Topic deletedTopic = topicRepository.save(topic);
        return new TopicDTO(deletedTopic);
    }
}
