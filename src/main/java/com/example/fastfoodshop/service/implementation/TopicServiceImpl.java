package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.TopicDTO;
import com.example.fastfoodshop.dto.TopicDifficultyFullDTO;
import com.example.fastfoodshop.dto.TopicDisplayDTO;
import com.example.fastfoodshop.entity.Topic;
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

    public TopicResponse createTopic(TopicCreateRequest topicCreateRequest) {
        Topic topic = new Topic();
        topic.setName(topicCreateRequest.getName());
        topic.setDescription(topicCreateRequest.getDescription());
        topic.setActivated(topicCreateRequest.getIsActivated());
        topic.setDeleted(false);

        String slug = generateUniqueSlug(topicCreateRequest.getName());
        topic.setSlug(slug);

        Topic savedTopic = topicRepository.save(topic);
        return new TopicResponse(TopicDTO.from(savedTopic));
    }

    public TopicResponse updateTopic(Long topicId, TopicCreateRequest topicCreateRequest) {
        Topic topic = findTopicOrThrow(topicId);
        topic.setName(topicCreateRequest.getName());
        topic.setDescription(topicCreateRequest.getDescription());
        topic.setActivated(topicCreateRequest.getIsActivated());

        Topic updatedTopic = topicRepository.save(topic);
        return new TopicResponse(TopicDTO.from(updatedTopic));
    }

    public TopicResponse getTopicBySlug(String slug) {
        Topic topic = findValidTopicOrThrow(slug);
        return new TopicResponse(TopicDTO.from(topic));
    }

    public TopicPageResponse getAllTopics(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Topic> topicPage = topicRepository.findByIsDeletedFalse(pageable);


        return TopicPageResponse.from(topicPage);
    }

    public TopicDisplayResponse getDisplayableTopics() {

        List<TopicDifficultyFullDTO> flatList = topicRepository.findDisplayableTopicsFull();

        Map<Long, List<TopicDifficultyFullDTO>> grouped =
                flatList.stream()
                        .collect(Collectors.groupingBy(
                                TopicDifficultyFullDTO::topicId,
                                LinkedHashMap::new,
                                Collectors.toList()
                        ));

        List<TopicDisplayDTO> topics = grouped.values().stream()
                .map(list -> {

                    TopicDifficultyFullDTO first = list.get(0);

                    List<TopicDifficultyDisplayDTO> difficulties =
                            list.stream()
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

                    return TopicDisplayDTO.from(first, difficulties);
                })
                .toList();

        return new TopicDisplayResponse(topics);
    }

    public TopicUpdateResponse activateTopic(Long topicId) {
        Topic topic = findDeactivatedTopic(topicId);
        topic.setActivated(true);
        topicRepository.save(topic);

        return new TopicUpdateResponse("Đã kích hoạt chủ đề: " + topicId);
    }

    public TopicUpdateResponse deactivateTopic(Long topicId) {
        Topic topic = findActivatedTopic(topicId);
        topic.setActivated(false);
        topicRepository.save(topic);

        return new TopicUpdateResponse("Đã hủy kích hoạt chủ đề: " + topicId);
    }

    public TopicUpdateResponse deleteTopic(Long topicId) {
        Topic topic = findTopicOrThrow(topicId);
        topic.setDeleted(true);

        Topic deletedTopic = topicRepository.save(topic);
        return new TopicUpdateResponse("Đã xóa chủ đề: " + topicId);
    }
}
