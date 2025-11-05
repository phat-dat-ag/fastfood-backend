package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.TopicDifficultyDTO;
import com.example.fastfoodshop.request.TopicDifficultyCreateRequest;
import com.example.fastfoodshop.request.TopicDifficultyUpdateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.TopicDifficultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/api/topic-difficulty")
@RequiredArgsConstructor
public class TopicDifficultyController {
    private final TopicDifficultyService topicDifficultyService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<TopicDifficultyDTO>> createTopicDifficulty(
            @RequestBody TopicDifficultyCreateRequest topicDifficultyCreateRequest,
            @RequestParam("topicSlug") String topicSlug
    ) {
        return topicDifficultyService.createTopicDifficulty(topicSlug, topicDifficultyCreateRequest);
    }

    @PutMapping()
    public ResponseEntity<ResponseWrapper<TopicDifficultyDTO>> updateTopicDifficulty(
            @RequestParam("topicDifficultyId") Long topicDifficultyId,
            @RequestBody TopicDifficultyUpdateRequest request
    ) {
        return topicDifficultyService.updateTopicDifficulty(topicDifficultyId, request);
    }

    @GetMapping("/by-slug")
    public ResponseEntity<ResponseWrapper<TopicDifficultyDTO>> getTopicDifficultyBySlug(@RequestParam("topicDifficultySlug") String topicDifficultySlug) {
        return topicDifficultyService.getTopicDifficultyBySlug(topicDifficultySlug);
    }

    @GetMapping("/by-topic-slug")
    public ResponseEntity<ResponseWrapper<ArrayList<TopicDifficultyDTO>>> getAllTopicDifficultiesByTopic(@RequestParam("topicSlug") String topicSlug) {
        return topicDifficultyService.getAllTopicDifficultiesByTopic(topicSlug);
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<TopicDifficultyDTO>> deleteTopicDifficulty(@RequestParam("topicDifficultyId") Long topicDifficultyId) {
        return topicDifficultyService.deleteTopicDifficulty(topicDifficultyId);
    }
}
