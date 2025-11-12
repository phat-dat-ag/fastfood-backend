package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.ReviewDTO;
import com.example.fastfoodshop.entity.Review;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReviewResponse {
    private List<ReviewDTO> reviews = new ArrayList<>();
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public ReviewResponse(Page<Review> page) {
        for (Review review : page.getContent()) {
            this.reviews.add(new ReviewDTO(review));
        }
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
        this.totalItems = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
