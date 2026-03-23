package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.topic.TopicDisplayResponse;
import com.example.fastfoodshop.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController extends BaseController {
    private final TopicService topicService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<TopicDisplayResponse>> getDisplayableTopics() {
        return okResponse(topicService.getDisplayableTopics());
    }
}
