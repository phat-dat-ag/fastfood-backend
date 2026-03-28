package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.TopicCreateRequest;
import com.example.fastfoodshop.request.TopicDifficultyCreateRequest;
import com.example.fastfoodshop.request.UpdateActivationRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.topic.TopicPageResponse;
import com.example.fastfoodshop.response.topic.TopicResponse;
import com.example.fastfoodshop.response.topic.TopicStatsResponse;
import com.example.fastfoodshop.response.topic.TopicUpdateResponse;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyPageResponse;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyUpdateResponse;
import com.example.fastfoodshop.service.TopicDifficultyService;
import com.example.fastfoodshop.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/topics")
@RequiredArgsConstructor
public class AdminTopicController extends BaseController {
    private final TopicService topicService;
    private final TopicDifficultyService topicDifficultyService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<TopicResponse>> createTopic(
            @Valid @RequestBody TopicCreateRequest topicCreateRequest
    ) {
        return okResponse(topicService.createTopic(topicCreateRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<TopicResponse>> updateTopic(
            @PathVariable("id") Long topicId,
            @RequestBody TopicCreateRequest topicCreateRequest
    ) {
        return okResponse(topicService.updateTopic(topicId, topicCreateRequest));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ResponseWrapper<TopicResponse>> getTopicBySlug(
            @PathVariable("slug") String topicSlug
    ) {
        return okResponse(topicService.getTopicBySlug(topicSlug));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<TopicPageResponse>> getTopics(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(topicService.getTopics(request.getPage(), request.getSize()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseWrapper<TopicUpdateResponse>> updateTopicActivation(
            @PathVariable("id") Long topicId,
            @RequestBody UpdateActivationRequest updateActivationRequest
    ) {
        return okResponse(topicService.updateTopicActivation(topicId, updateActivationRequest.activated()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<TopicUpdateResponse>> deleteTopic(
            @PathVariable("id") Long topicId
    ) {
        return okResponse(topicService.deleteTopic(topicId));
    }

    @GetMapping("/stats")
    public ResponseEntity<ResponseWrapper<TopicStatsResponse>> getTopicStats() {
        return okResponse(topicService.getTopicStats());
    }

    @PostMapping("/{slug}/difficulties")
    public ResponseEntity<ResponseWrapper<TopicDifficultyUpdateResponse>> createTopicDifficulty(
            @Valid @RequestBody TopicDifficultyCreateRequest topicDifficultyCreateRequest,
            @PathVariable("slug") String topicSlug
    ) {
        return okResponse(topicDifficultyService.createTopicDifficultyFromTopic(topicSlug, topicDifficultyCreateRequest));
    }

    @GetMapping("/{slug}/difficulties")
    public ResponseEntity<ResponseWrapper<TopicDifficultyPageResponse>> getAllTopicDifficultiesByTopic(
            @PathVariable("slug") String topicSlug,
            @Valid @ModelAttribute PageRequest pageRequest
    ) {
        return okResponse(topicDifficultyService.getAllTopicDifficultiesByTopic(
                topicSlug, pageRequest.getPage(), pageRequest.getSize()
        ));
    }
}
