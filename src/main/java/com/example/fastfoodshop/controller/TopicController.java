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
public class TopicController extends BaseController {
    private final TopicService topicService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<TopicDTO>> createTopic(
            @RequestBody TopicCreateRequest topicCreateRequest
    ) {
        return okResponse(topicService.createTopic(topicCreateRequest));
    }

    @PutMapping()
    public ResponseEntity<ResponseWrapper<TopicDTO>> updateTopic(
            @RequestParam("topicId") Long topicId,
            @RequestBody TopicCreateRequest topicCreateRequest
    ) {
        return okResponse(topicService.updateTopic(topicId, topicCreateRequest));
    }

    @GetMapping("/by-slug")
    public ResponseEntity<ResponseWrapper<TopicDTO>> getTopicBySlug(@RequestParam("slug") String topicSlug) {
        return okResponse(topicService.getTopicBySlug(topicSlug));
    }

    @GetMapping()
    public ResponseEntity<ResponseWrapper<TopicResponse>> getAllTopics(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(topicService.getAllTopics(request.getPage(), request.getSize()));
    }

    @GetMapping("/display")
    public ResponseEntity<ResponseWrapper<List<TopicDisplayResponse>>> getDisplayableTopics() {
        return okResponse(topicService.getDisplayableTopics());
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseWrapper<String>> activateTopic(@RequestParam("topicId") Long topicId) {
        return okResponse(topicService.activateTopic(topicId));
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ResponseWrapper<String>> deactivateTopic(@RequestParam("topicId") Long topicId) {
        return okResponse(topicService.deactivateTopic(topicId));
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<TopicDTO>> deleteTopic(@RequestParam("topicId") Long topicId) {
        return okResponse(topicService.deleteTopic(topicId));
    }
}
