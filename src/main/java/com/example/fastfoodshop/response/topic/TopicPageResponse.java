package com.example.fastfoodshop.response.topic;

import com.example.fastfoodshop.dto.TopicDTO;
import com.example.fastfoodshop.entity.Topic;
import org.springframework.data.domain.Page;

import java.util.List;

public record TopicPageResponse(
        List<TopicDTO> topics,
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages
) {
    public static TopicPageResponse from(Page<Topic> page) {
        List<TopicDTO> topics = page.getContent().stream().map(TopicDTO::from).toList();
        return new TopicPageResponse(
                topics,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
