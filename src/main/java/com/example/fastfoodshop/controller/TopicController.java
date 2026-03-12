package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.TopicCreateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.topic.TopicDisplayResponse;
import com.example.fastfoodshop.response.topic.TopicPageResponse;
import com.example.fastfoodshop.response.topic.TopicResponse;
import com.example.fastfoodshop.response.topic.TopicUpdateResponse;
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

@RestController
@RequestMapping("/api/topic")
@RequiredArgsConstructor
public class TopicController extends BaseController {
    private final TopicService topicService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<TopicResponse>> createTopic(
            @RequestBody TopicCreateRequest topicCreateRequest
    ) {
        return okResponse(topicService.createTopic(topicCreateRequest));
    }

    @PutMapping()
    public ResponseEntity<ResponseWrapper<TopicResponse>> updateTopic(
            @RequestParam("topicId") Long topicId,
            @RequestBody TopicCreateRequest topicCreateRequest
    ) {
        return okResponse(topicService.updateTopic(topicId, topicCreateRequest));
    }

    @GetMapping("/by-slug")
    public ResponseEntity<ResponseWrapper<TopicResponse>> getTopicBySlug(@RequestParam("slug") String topicSlug) {
        return okResponse(topicService.getTopicBySlug(topicSlug));
    }

    @GetMapping()
    public ResponseEntity<ResponseWrapper<TopicPageResponse>> getAllTopics(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(topicService.getAllTopics(request.getPage(), request.getSize()));
    }

    @GetMapping("/display")
    public ResponseEntity<ResponseWrapper<TopicDisplayResponse>> getDisplayableTopics() {
        return okResponse(topicService.getDisplayableTopics());
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseWrapper<TopicUpdateResponse>> activateTopic(@RequestParam("topicId") Long topicId) {
        return okResponse(topicService.activateTopic(topicId));
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ResponseWrapper<TopicUpdateResponse>> deactivateTopic(@RequestParam("topicId") Long topicId) {
        return okResponse(topicService.deactivateTopic(topicId));
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<TopicUpdateResponse>> deleteTopic(@RequestParam("topicId") Long topicId) {
        return okResponse(topicService.deleteTopic(topicId));
    }
}
