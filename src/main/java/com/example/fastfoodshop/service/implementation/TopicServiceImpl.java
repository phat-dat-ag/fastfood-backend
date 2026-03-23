package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.TopicDTO;
import com.example.fastfoodshop.dto.TopicDifficultyFullDTO;
import com.example.fastfoodshop.dto.TopicDisplayDTO;
import com.example.fastfoodshop.entity.Topic;
import com.example.fastfoodshop.exception.topic.DeletedTopicException;
import com.example.fastfoodshop.exception.topic.InvalidTopicStatusException;
import com.example.fastfoodshop.exception.topic.TopicNotFoundException;
import com.example.fastfoodshop.repository.TopicRepository;
import com.example.fastfoodshop.request.TopicCreateRequest;
import com.example.fastfoodshop.dto.TopicDifficultyDisplayDTO;
import com.example.fastfoodshop.response.topic.TopicDisplayResponse;
import com.example.fastfoodshop.response.topic.TopicPageResponse;
import com.example.fastfoodshop.response.topic.TopicResponse;
import com.example.fastfoodshop.response.topic.TopicUpdateResponse;
import com.example.fastfoodshop.service.TopicService;
import com.example.fastfoodshop.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private Topic buildTopic(TopicCreateRequest topicCreateRequest) {
        Topic topic = new Topic();

        String slug = generateUniqueSlug(topicCreateRequest.name());

        topic.setName(topicCreateRequest.name());
        topic.setDescription(topicCreateRequest.description());
        topic.setActivated(topicCreateRequest.activated());
        topic.setDeleted(false);
        topic.setSlug(slug);

        return topic;
    }

    public TopicResponse createTopic(TopicCreateRequest topicCreateRequest) {
        Topic topic = buildTopic(topicCreateRequest);

        Topic savedTopic = topicRepository.save(topic);
        return new TopicResponse(TopicDTO.from(savedTopic));
    }

    private void updateTopicFields(Topic topic, TopicCreateRequest topicCreateRequest) {
        topic.setName(topicCreateRequest.name());
        topic.setDescription(topicCreateRequest.description());
        topic.setActivated(topicCreateRequest.activated());
    }

    public TopicResponse updateTopic(Long topicId, TopicCreateRequest topicCreateRequest) {
        Topic topic = findTopicOrThrow(topicId);

        updateTopicFields(topic, topicCreateRequest);

        Topic updatedTopic = topicRepository.save(topic);
        return new TopicResponse(TopicDTO.from(updatedTopic));
    }

    public TopicResponse getTopicBySlug(String slug) {
        Topic topic = findValidTopicOrThrow(slug);
        return new TopicResponse(TopicDTO.from(topic));
    }

    public TopicPageResponse getTopics(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Topic> topicPage = topicRepository.findByIsDeletedFalse(pageable);

        return TopicPageResponse.from(topicPage);
    }

    private Map<Long, List<TopicDifficultyFullDTO>> groupByTopic(List<TopicDifficultyFullDTO> flatList) {
        return flatList.stream()
                .collect(Collectors.groupingBy(
                        TopicDifficultyFullDTO::topicId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    private List<TopicDifficultyDisplayDTO> mapToDifficulties(
            List<TopicDifficultyFullDTO> topicDifficultyFullDTOs
    ) {
        return topicDifficultyFullDTOs
                .stream()
                .map(dto -> new TopicDifficultyDisplayDTO(
                        dto.difficultyId(),
                        dto.difficultyName(),
                        dto.difficultySlug(),
                        dto.difficultyDescription(),
                        dto.duration(),
                        dto.questionCount(),
                        dto.minCorrectToReward()
                ))
                .toList();
    }

    private TopicDisplayDTO mapSingleTopic(List<TopicDifficultyFullDTO> topicDifficultyFullDTOs) {
        TopicDifficultyFullDTO topicDifficultyFullDTO = topicDifficultyFullDTOs.get(0);

        List<TopicDifficultyDisplayDTO> difficulties = mapToDifficulties(topicDifficultyFullDTOs);

        return TopicDisplayDTO.from(topicDifficultyFullDTO, difficulties);
    }

    private List<TopicDisplayDTO> mapToTopicDisplay(
            Map<Long, List<TopicDifficultyFullDTO>> grouped
    ) {
        return grouped.values().stream()
                .map(this::mapSingleTopic)
                .toList();
    }

    public TopicDisplayResponse getDisplayableTopics() {

        List<TopicDifficultyFullDTO> flatList = topicRepository.findDisplayableTopicsFull();

        Map<Long, List<TopicDifficultyFullDTO>> grouped = groupByTopic(flatList);

        List<TopicDisplayDTO> topics = mapToTopicDisplay(grouped);

        return new TopicDisplayResponse(topics);
    }

    public TopicUpdateResponse updateTopicActivation(Long topicId, boolean activated) {
        Topic topic = findTopicOrThrow(topicId);
        if (topic.isActivated() == activated) {
            throw new InvalidTopicStatusException(topicId);
        }

        topic.setActivated(activated);
        topicRepository.save(topic);

        String message = activated ? "Đã kích hoạt chủ đề: " + topicId : "Đã hủy kích hoạt chủ đề: " + topicId;

        return new TopicUpdateResponse(message);
    }

    public TopicUpdateResponse deleteTopic(Long topicId) {
        Topic topic = findTopicOrThrow(topicId);
        if (topic.isDeleted()) {
            throw new DeletedTopicException();
        }

        topic.setDeleted(true);

        topicRepository.save(topic);
        return new TopicUpdateResponse("Đã xóa chủ đề: " + topicId);
    }
}
