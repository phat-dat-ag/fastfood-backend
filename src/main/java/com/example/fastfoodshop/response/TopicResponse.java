package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.TopicDTO;
import com.example.fastfoodshop.entity.Topic;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class TopicResponse {
    List<TopicDTO> topics = new ArrayList<>();
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public TopicResponse(Page<Topic> page) {
        for (Topic topic : page.getContent()) {
            this.topics.add(new TopicDTO(topic));
        }
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
        this.totalItems = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
