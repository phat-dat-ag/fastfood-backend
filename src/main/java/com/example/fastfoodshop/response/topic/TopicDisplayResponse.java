package com.example.fastfoodshop.response.topic;

import com.example.fastfoodshop.dto.TopicDisplayDTO;

import java.util.List;

public record TopicDisplayResponse(
        List<TopicDisplayDTO> displayableTopics
) {
}
