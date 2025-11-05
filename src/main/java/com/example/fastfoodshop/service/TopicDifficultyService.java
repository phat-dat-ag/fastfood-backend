package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.TopicDifficultyDTO;
import com.example.fastfoodshop.entity.Topic;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.repository.TopicDifficultyRepository;
import com.example.fastfoodshop.request.TopicDifficultyCreateRequest;
import com.example.fastfoodshop.request.TopicDifficultyUpdateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicDifficultyService {
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
        return topicDifficultyRepository.findById(topicDifficultyId).orElseThrow(() -> new RuntimeException("Không tìm thấy độ khó của chủ đề"));
    }

    public ResponseEntity<ResponseWrapper<TopicDifficultyDTO>> createTopicDifficulty(String topicSlug, TopicDifficultyCreateRequest request) {
        try {
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
            return ResponseEntity.ok(ResponseWrapper.success(new TopicDifficultyDTO(savedTopicDifficulty)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "CREATE_TOPIC_DIFFICULTY_FAILED",
                    "Lỗi khi tạo độ khó cho chủ đề " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<TopicDifficultyDTO>> updateTopicDifficulty(Long id, TopicDifficultyUpdateRequest request) {
        try {
            TopicDifficulty topicDifficulty = findTopicDifficultyOrThrow(id);

            topicDifficulty.setName(request.getName());
            topicDifficulty.setDescription(request.getDescription());
            topicDifficulty.setActivated(request.getIsActivated());

            TopicDifficulty updatedTopicDifficulty = topicDifficultyRepository.save(topicDifficulty);
            return ResponseEntity.ok(ResponseWrapper.success(new TopicDifficultyDTO(updatedTopicDifficulty)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "UPDATE_TOPIC_DIFFICULTY_FAILED",
                    "Lỗi khi cập nhật độ khó cho chủ đề " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<ArrayList<TopicDifficultyDTO>>> getAllTopicDifficultiesByTopic(String topicSlug) {
        try {
            Topic topic = topicService.findValidTopicOrThrow(topicSlug);
            List<TopicDifficulty> topicDifficulties = topicDifficultyRepository.findByTopicAndIsDeletedFalse(topic);

            ArrayList<TopicDifficultyDTO> topicDifficultyDTOs = new ArrayList<>();
            for (TopicDifficulty topicDifficulty : topicDifficulties) {
                topicDifficultyDTOs.add(new TopicDifficultyDTO(topicDifficulty));
            }

            return ResponseEntity.ok(ResponseWrapper.success(topicDifficultyDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ALL_TOPIC_DIFFICULTIES_FAILED",
                    "Lỗi khi lấy các độ khó cho chủ đề " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<TopicDifficultyDTO>> deleteTopicDifficulty(Long topicDifficultyId) {
        try {
            TopicDifficulty topicDifficulty = findTopicDifficultyOrThrow(topicDifficultyId);
            topicDifficulty.setDeleted(true);

            TopicDifficulty deletedTopicDifficulty = topicDifficultyRepository.save(topicDifficulty);
            return ResponseEntity.ok(ResponseWrapper.success(new TopicDifficultyDTO(deletedTopicDifficulty)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "DELETE_TOPIC_DIFFICULTY_FAILED",
                    "Lỗi khi xóa độ khó cho chủ đề " + e.getMessage()
            ));
        }
    }
}
