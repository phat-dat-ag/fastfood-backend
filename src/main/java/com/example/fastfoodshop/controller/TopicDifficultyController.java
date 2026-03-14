package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.TopicDifficultyCreateRequest;
import com.example.fastfoodshop.request.TopicDifficultyGetByTopicRequest;
import com.example.fastfoodshop.request.TopicDifficultyUpdateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyPageResponse;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyResponse;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyUpdateResponse;
import com.example.fastfoodshop.service.TopicDifficultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequestMapping("/api/topic-difficulty")
@RequiredArgsConstructor
public class TopicDifficultyController extends BaseController {
    private final TopicDifficultyService topicDifficultyService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<TopicDifficultyUpdateResponse>> createTopicDifficulty(
            @RequestBody TopicDifficultyCreateRequest topicDifficultyCreateRequest,
            @RequestParam("topicSlug") String topicSlug
    ) {
        return okResponse(topicDifficultyService.createTopicDifficulty(topicSlug, topicDifficultyCreateRequest));
    }

    @PutMapping()
    public ResponseEntity<ResponseWrapper<TopicDifficultyUpdateResponse>> updateTopicDifficulty(
            @RequestParam("topicDifficultyId") Long topicDifficultyId,
            @RequestBody TopicDifficultyUpdateRequest topicDifficultyUpdateRequest
    ) {
        return okResponse(topicDifficultyService.updateTopicDifficulty(topicDifficultyId, topicDifficultyUpdateRequest));
    }

    @GetMapping("/by-slug")
    public ResponseEntity<ResponseWrapper<TopicDifficultyResponse>> getTopicDifficultyBySlug(
            @RequestParam("topicDifficultySlug") String topicDifficultySlug
    ) {
        return okResponse(topicDifficultyService.getTopicDifficultyBySlug(topicDifficultySlug));
    }

    @GetMapping("/by-topic-slug")
    public ResponseEntity<ResponseWrapper<TopicDifficultyPageResponse>> getAllTopicDifficultiesByTopic(
            @Valid @ModelAttribute TopicDifficultyGetByTopicRequest topicDifficultyGetByTopicRequest
    ) {
        return okResponse(topicDifficultyService.getAllTopicDifficultiesByTopic(topicDifficultyGetByTopicRequest));
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseWrapper<TopicDifficultyUpdateResponse>> activateTopicDifficulty(
            @RequestParam("topicDifficultyId") Long topicDifficultyId
    ) {
        return okResponse(topicDifficultyService.activateTopicDifficulty(topicDifficultyId));
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ResponseWrapper<TopicDifficultyUpdateResponse>> deactivateTopicDifficulty(
            @RequestParam("topicDifficultyId") Long topicDifficultyId
    ) {
        return okResponse(topicDifficultyService.deactivateTopicDifficulty(topicDifficultyId));
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<TopicDifficultyUpdateResponse>> deleteTopicDifficulty(
            @RequestParam("topicDifficultyId") Long topicDifficultyId
    ) {
        return okResponse(topicDifficultyService.deleteTopicDifficulty(topicDifficultyId));
    }
}
