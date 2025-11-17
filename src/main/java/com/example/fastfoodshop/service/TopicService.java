package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.TopicDTO;
import com.example.fastfoodshop.dto.TopicDifficultyFullDTO;
import com.example.fastfoodshop.entity.Topic;
import com.example.fastfoodshop.repository.TopicRepository;
import com.example.fastfoodshop.response.DifficultyDisplayResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.TopicDisplayResponse;
import com.example.fastfoodshop.response.TopicResponse;
import com.example.fastfoodshop.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TopicService {
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
        return topicRepository.findById(topicId).orElseThrow(() -> new RuntimeException("Chủ đề không tồn tại"));
    }

    public Topic findValidTopicOrThrow(String topicSlug) {
        return topicRepository.findBySlugAndIsDeletedFalse(topicSlug).orElseThrow(() -> new RuntimeException("Chủ đề không tồn tại hoặc đã bị xóa"));
    }

    private Topic findActivatedTopic(Long topicId) {
        return topicRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(topicId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy chủ đề này đang được kích hoạt")
        );
    }

    private Topic findDeactivatedTopic(Long topicId) {
        return topicRepository.findByIdAndIsDeletedFalseAndIsActivatedFalse(topicId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy chủ đề này đang bị hủy kích hoạt")
        );
    }

    public ResponseEntity<ResponseWrapper<TopicDTO>> createTopic(String name, String description, boolean isActivated) {
        try {
            Topic topic = new Topic();
            topic.setName(name);
            topic.setDescription(description);
            topic.setActivated(isActivated);
            topic.setDeleted(false);

            String slug = generateUniqueSlug(name);
            topic.setSlug(slug);

            Topic savedTopic = topicRepository.save(topic);
            return ResponseEntity.ok(ResponseWrapper.success(new TopicDTO(savedTopic)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "CREATE_TOPIC_FAILED",
                    "Lỗi tạo chủ đề mới " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<TopicDTO>> updateTopic(Long topicId, String name, String description, boolean isActivated) {
        try {
            Topic topic = findTopicOrThrow(topicId);
            topic.setName(name);
            topic.setDescription(description);
            topic.setActivated(isActivated);

            Topic updatedTopic = topicRepository.save(topic);
            return ResponseEntity.ok(ResponseWrapper.success(new TopicDTO(updatedTopic)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "UPDATE_TOPIC_FAILED",
                    "Lỗi cập nhật chủ đề  " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<TopicDTO>> getTopicBySlug(String slug) {
        try {
            Topic topic = findValidTopicOrThrow(slug);
            return ResponseEntity.ok(ResponseWrapper.success(new TopicDTO(topic)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_TOPIC_FAILED",
                    "Lỗi lấy chủ đề theo slug " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<TopicResponse>> getAllTopics(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Topic> topicPage = topicRepository.findByIsDeletedFalse(pageable);


            return ResponseEntity.ok(ResponseWrapper.success(new TopicResponse(topicPage)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_TOPICS_FAILED",
                    "Lỗi lấy tất cả các chủ đề " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<List<TopicDisplayResponse>>> getDisplayableTopics() {
        try {
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
            return ResponseEntity.ok(ResponseWrapper.success(new ArrayList<>(grouped.values())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_DISPLAYABLE_TOPICS_FAILED",
                    "Lỗi lấy các chủ đề hiển thị cho người dùng"
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<String>> activateTopic(Long topicId) {
        try {
            Topic topic = findDeactivatedTopic(topicId);
            topic.setActivated(true);
            topicRepository.save(topic);

            return ResponseEntity.ok(ResponseWrapper.success("Đã kích hoạt chủ đề"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "ACTIVATE_TOPIC_FAILED",
                    "Lỗi kích hoạt chủ đề " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<String>> deactivateTopic(Long topicId) {
        try {
            Topic topic = findActivatedTopic(topicId);
            topic.setActivated(false);
            topicRepository.save(topic);

            return ResponseEntity.ok(ResponseWrapper.success("Đã hủy kích hoạt chủ đề"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "DEACTIVATE_TOPIC_FAILED",
                    "Lỗi hủy kích hoạt chủ đề " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<TopicDTO>> deleteTopic(Long topicId) {
        try {
            Topic topic = findTopicOrThrow(topicId);
            topic.setDeleted(true);

            Topic deletedTopic = topicRepository.save(topic);
            return ResponseEntity.ok(ResponseWrapper.success(new TopicDTO(deletedTopic)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "DELETE_TOPICS_FAILED",
                    "Lỗi xóa chủ đề " + e.getMessage()
            ));
        }
    }
}
