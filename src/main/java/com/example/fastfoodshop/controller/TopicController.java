package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.TopicDTO;
import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.TopicCreateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.TopicDisplayResponse;
import com.example.fastfoodshop.response.TopicResponse;
import com.example.fastfoodshop.service.TopicService;
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

import java.util.List;

@RestController
@RequestMapping("/api/topic")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<TopicDTO>> createTopic(@RequestBody TopicCreateRequest request) {
        return topicService.createTopic(request.getName(), request.getDescription(), request.getIsActivated());
    }

    @PutMapping()
    public ResponseEntity<ResponseWrapper<TopicDTO>> updateTopic(
            @RequestParam("topicId") Long topicId,
            @RequestBody TopicCreateRequest request
    ) {
        return topicService.updateTopic(topicId, request.getName(), request.getDescription(), request.getIsActivated());
    }

    @GetMapping("/by-slug")
    public ResponseEntity<ResponseWrapper<TopicDTO>> getTopicBySlug(@RequestParam("slug") String slug) {
        return topicService.getTopicBySlug(slug);
    }

    @GetMapping()
    public ResponseEntity<ResponseWrapper<TopicResponse>> getAllTopics(@Valid @ModelAttribute PageRequest request) {
        return topicService.getAllTopics(request.getPage(), request.getSize());
    }

    @GetMapping("/display")
    public ResponseEntity<ResponseWrapper<List<TopicDisplayResponse>>> getDisplayableTopics() {
        return topicService.getDisplayableTopics();
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseWrapper<String>> activateTopic(@RequestParam("topicId") Long topicId) {
        return topicService.activateTopic(topicId);
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ResponseWrapper<String>> deactivateTopic(@RequestParam("topicId") Long topicId) {
        return topicService.deactivateTopic(topicId);
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<TopicDTO>> deleteTopic(@RequestParam("topicId") Long topicId) {
        return topicService.deleteTopic(topicId);
    }
}
