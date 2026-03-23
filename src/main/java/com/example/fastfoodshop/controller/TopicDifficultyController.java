package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.TopicDifficultyUpdateRequest;
import com.example.fastfoodshop.request.UpdateActivationRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyResponse;
import com.example.fastfoodshop.response.topic_difficulty.TopicDifficultyUpdateResponse;
import com.example.fastfoodshop.service.TopicDifficultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/admin/topic-difficulties")
@RequiredArgsConstructor
public class TopicDifficultyController extends BaseController {
    private final TopicDifficultyService topicDifficultyService;

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<TopicDifficultyUpdateResponse>> updateTopicDifficulty(
            @PathVariable("id") Long topicDifficultyId,
            @Valid @RequestBody TopicDifficultyUpdateRequest topicDifficultyUpdateRequest
    ) {
        return okResponse(topicDifficultyService.updateTopicDifficulty(
                topicDifficultyId, topicDifficultyUpdateRequest
        ));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ResponseWrapper<TopicDifficultyResponse>> getTopicDifficultyBySlug(
            @PathVariable("slug") String topicDifficultySlug
    ) {
        return okResponse(topicDifficultyService.getTopicDifficultyBySlug(topicDifficultySlug));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseWrapper<TopicDifficultyUpdateResponse>> updateTopicDifficultyActivation(
            @PathVariable("id") Long topicDifficultyId,
            @Valid @RequestBody UpdateActivationRequest updateActivationRequest
    ) {
        return okResponse(topicDifficultyService.updateTopicDifficultyActivation(
                topicDifficultyId, updateActivationRequest.activated()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<TopicDifficultyUpdateResponse>> deleteTopicDifficulty(
            @PathVariable("id") Long topicDifficultyId
    ) {
        return okResponse(topicDifficultyService.deleteTopicDifficulty(topicDifficultyId));
    }
}
