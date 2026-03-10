package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.TopicDifficultyDTO;
import com.example.fastfoodshop.entity.Topic;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.exception.topic_difficulty.InvalidTopicDifficultyStatusException;
import com.example.fastfoodshop.exception.topic_difficulty.TopicDifficultyNotFoundException;
import com.example.fastfoodshop.exception.topic_difficulty.UnavailableTopicDifficultyException;
import com.example.fastfoodshop.repository.TopicDifficultyRepository;
import com.example.fastfoodshop.request.TopicDifficultyCreateRequest;
import com.example.fastfoodshop.request.TopicDifficultyGetByTopicRequest;
import com.example.fastfoodshop.request.TopicDifficultyUpdateRequest;
import com.example.fastfoodshop.response.TopicDifficultyResponse;
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

    private TopicDifficulty findTopicDifficultyOrThrow(Long topicDifficultyId) {
        return topicDifficultyRepository.findById(topicDifficultyId).orElseThrow(
                () -> new TopicDifficultyNotFoundException(topicDifficultyId)
        );
    }

    private TopicDifficulty findActivatedTopicDifficulty(Long topicDifficultyId) {
        return topicDifficultyRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(topicDifficultyId).orElseThrow(
                InvalidTopicDifficultyStatusException::new
        );
    }

    private TopicDifficulty findDeactivatedTopicDifficulty(Long topicDifficultyId) {
        return topicDifficultyRepository.findByIdAndIsDeletedFalseAndIsActivatedFalse(topicDifficultyId).orElseThrow(
                InvalidTopicDifficultyStatusException::new
        );
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

    public TopicDifficultyDTO createTopicDifficulty(
            String topicSlug, TopicDifficultyCreateRequest request
    ) {
        Topic topic = topicService.findValidTopicOrThrow(topicSlug);

        TopicDifficulty topicDifficulty = new TopicDifficulty();
        topicDifficulty.setTopic(topic);
        topicDifficulty.setName(request.getName());
        topicDifficulty.setDescription(request.getDescription());
        topicDifficulty.setDuration(request.getDuration());
        topicDifficulty.setQuestionCount(request.getQuestionCount());
        topicDifficulty.setMinCorrectToReward(request.getMinCorrectToReward());
        topicDifficulty.setActivated(request.getIsActivated());
        topicDifficulty.setDeleted(false);

        String slug = generateUniqueSlug(request.getName());
        topicDifficulty.setSlug(slug);

        TopicDifficulty savedTopicDifficulty = topicDifficultyRepository.save(topicDifficulty);
        return TopicDifficultyDTO.from(savedTopicDifficulty);
    }

    public TopicDifficultyDTO updateTopicDifficulty(
            Long id, TopicDifficultyUpdateRequest request
    ) {
        TopicDifficulty topicDifficulty = findTopicDifficultyOrThrow(id);

        topicDifficulty.setName(request.getName());
        topicDifficulty.setDescription(request.getDescription());
        topicDifficulty.setActivated(request.getIsActivated());

        TopicDifficulty updatedTopicDifficulty = topicDifficultyRepository.save(topicDifficulty);
        return TopicDifficultyDTO.from(updatedTopicDifficulty);
    }

    public TopicDifficultyDTO getTopicDifficultyBySlug(String topicDifficultySlug) {
        TopicDifficulty topicDifficulty = findValidTopicDifficultyOrThrow(topicDifficultySlug);
        return TopicDifficultyDTO.from(topicDifficulty);
    }

    public TopicDifficultyResponse getAllTopicDifficultiesByTopic(
            TopicDifficultyGetByTopicRequest topicDifficultyGetByTopicRequest
    ) {
        Topic topic = topicService.findValidTopicOrThrow(topicDifficultyGetByTopicRequest.getTopicSlug());
        Pageable pageable = PageRequest.of(
                topicDifficultyGetByTopicRequest.getPage(), topicDifficultyGetByTopicRequest.getSize()
        );
        Page<TopicDifficulty> topicDifficultyPage = topicDifficultyRepository.findByTopicAndIsDeletedFalse(topic, pageable);

        return new TopicDifficultyResponse(topicDifficultyPage);
    }

    public String activateTopicDifficulty(Long topicDifficultyId) {
        TopicDifficulty topicDifficulty = findDeactivatedTopicDifficulty(topicDifficultyId);
        topicDifficulty.setActivated(true);
        topicDifficultyRepository.save(topicDifficulty);

        return "Đã kích hoạt độ khó";
    }

    public String deactivateTopicDifficulty(Long topicDifficultyId) {
        TopicDifficulty topicDifficulty = findActivatedTopicDifficulty(topicDifficultyId);
        topicDifficulty.setActivated(false);
        topicDifficultyRepository.save(topicDifficulty);

        return "Đã hủy kích hoạt độ khó";
    }

    public TopicDifficultyDTO deleteTopicDifficulty(Long topicDifficultyId) {
        TopicDifficulty topicDifficulty = findTopicDifficultyOrThrow(topicDifficultyId);
        topicDifficulty.setDeleted(true);

        TopicDifficulty deletedTopicDifficulty = topicDifficultyRepository.save(topicDifficulty);
        return TopicDifficultyDTO.from(deletedTopicDifficulty);
    }
}