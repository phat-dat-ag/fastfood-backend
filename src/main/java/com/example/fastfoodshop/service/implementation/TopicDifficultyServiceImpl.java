package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.TopicDifficultyDTO;
import com.example.fastfoodshop.entity.Topic;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.exception.topic_difficulty.DeletedTopicDifficultyException;
import com.example.fastfoodshop.exception.topic_difficulty.InvalidTopicDifficultyStatusException;
import com.example.fastfoodshop.exception.topic_difficulty.TopicDifficultyNotFoundException;
import com.example.fastfoodshop.exception.topic_difficulty.UnavailableTopicDifficultyException;
import com.example.fastfoodshop.repository.TopicDifficultyRepository;
import com.example.fastfoodshop.request.TopicDifficultyCreateRequest;
import com.example.fastfoodshop.request.TopicDifficultyUpdateRequest;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyPageResponse;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyResponse;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyUpdateResponse;
import com.example.fastfoodshop.service.TopicDifficultyService;
import com.example.fastfoodshop.service.TopicService;
import com.example.fastfoodshop.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopicDifficultyServiceImpl implements TopicDifficultyService {
    private final TopicService topicService;
    private final TopicDifficultyRepository topicDifficultyRepository;

    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtils.toSlug(name);
        String uniqueSlug = baseSlug;
        int counter = 1;

        while (topicDifficultyRepository.existsBySlug((uniqueSlug))) {
            uniqueSlug = baseSlug + "-" + counter++;
        }
        return uniqueSlug;
    }

    private TopicDifficulty buildTopicDifficulty(
            Topic topic, TopicDifficultyCreateRequest request
    ) {
        TopicDifficulty topicDifficulty = new TopicDifficulty();
        topicDifficulty.setTopic(topic);
        topicDifficulty.setName(request.name());
        topicDifficulty.setDescription(request.description());
        topicDifficulty.setDuration(request.duration());
        topicDifficulty.setQuestionCount(request.questionCount());
        topicDifficulty.setMinCorrectToReward(request.minCorrectToReward());
        topicDifficulty.setActivated(request.activated());
        topicDifficulty.setDeleted(false);

        String topicDifficultySlug = generateUniqueSlug(request.name());
        topicDifficulty.setSlug(topicDifficultySlug);

        return topicDifficulty;
    }

    public TopicDifficultyUpdateResponse createTopicDifficultyFromTopic(
            String topicSlug, TopicDifficultyCreateRequest request
    ) {
        Topic topic = topicService.findValidTopicOrThrow(topicSlug);

        TopicDifficulty topicDifficulty = buildTopicDifficulty(topic, request);

        TopicDifficulty savedTopicDifficulty = topicDifficultyRepository.save(topicDifficulty);
        return new TopicDifficultyUpdateResponse("Đã thêm độ khó: " + savedTopicDifficulty.getId());
    }

    public TopicDifficultyPageResponse getAllTopicDifficultiesByTopic(
            String topicSlug, int page, int size
    ) {
        Topic topic = topicService.findValidTopicOrThrow(topicSlug);
        Pageable pageable = PageRequest.of(page, size);
        Page<TopicDifficulty> topicDifficultyPage =
                topicDifficultyRepository.findByTopicAndIsDeletedFalse(topic, pageable);

        return TopicDifficultyPageResponse.from(topicDifficultyPage);
    }

    private TopicDifficulty findTopicDifficultyOrThrow(Long topicDifficultyId) {
        return topicDifficultyRepository.findById(topicDifficultyId).orElseThrow(
                () -> new TopicDifficultyNotFoundException(topicDifficultyId)
        );
    }

    private void updateTopicDifficultyFields(
            TopicDifficulty topicDifficulty, TopicDifficultyUpdateRequest request
    ) {
        topicDifficulty.setName(request.name());
        topicDifficulty.setDescription(request.description());
        topicDifficulty.setActivated(request.activated());
    }

    public TopicDifficultyUpdateResponse updateTopicDifficulty(
            Long topicDifficultyId, TopicDifficultyUpdateRequest request
    ) {
        TopicDifficulty topicDifficulty = findTopicDifficultyOrThrow(topicDifficultyId);

        updateTopicDifficultyFields(topicDifficulty, request);

        topicDifficultyRepository.save(topicDifficulty);
        return new TopicDifficultyUpdateResponse("Đã cập nhật độ khó: " + topicDifficultyId);
    }

    public TopicDifficultyResponse getTopicDifficultyBySlug(String topicDifficultySlug) {
        TopicDifficulty topicDifficulty = findValidTopicDifficultyOrThrow(topicDifficultySlug);
        return new TopicDifficultyResponse(TopicDifficultyDTO.from(topicDifficulty));
    }

    public TopicDifficultyUpdateResponse updateTopicDifficultyActivation(
            Long topicDifficultyId, boolean activated
    ) {
        TopicDifficulty topicDifficulty = findTopicDifficultyOrThrow(topicDifficultyId);
        if (topicDifficulty.isActivated() == activated) {
            throw new InvalidTopicDifficultyStatusException();
        }

        topicDifficulty.setActivated(activated);
        topicDifficultyRepository.save(topicDifficulty);

        String message = activated
                ? "Đã kích hoạt độ khó: " + topicDifficultyId
                : "Đã hủy kích hoạt độ khó: " + topicDifficultyId;

        return new TopicDifficultyUpdateResponse(message);
    }

    public TopicDifficultyUpdateResponse deleteTopicDifficulty(Long topicDifficultyId) {
        TopicDifficulty topicDifficulty = findTopicDifficultyOrThrow(topicDifficultyId);
        if (topicDifficulty.isDeleted()) {
            throw new DeletedTopicDifficultyException();
        }

        topicDifficulty.setDeleted(true);

        topicDifficultyRepository.save(topicDifficulty);
        return new TopicDifficultyUpdateResponse("Đã xóa độ khó: " + topicDifficultyId);
    }

    public TopicDifficulty findValidTopicDifficultyOrThrow(String topicDifficultySlug) {
        return topicDifficultyRepository.findBySlugAndIsDeletedFalse(topicDifficultySlug).orElseThrow(
                () -> new TopicDifficultyNotFoundException(topicDifficultySlug)
        );
    }

    public TopicDifficulty findPlayableTopicDifficultyBySlug(String topicDifficultySlug) {
        return topicDifficultyRepository.findPlayableBySlug(topicDifficultySlug).orElseThrow(
                UnavailableTopicDifficultyException::new
        );
    }
}