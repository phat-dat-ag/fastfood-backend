package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.QuestionDTO;
import com.example.fastfoodshop.entity.Question;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuestionResponse {
    private List<QuestionDTO> questions = new ArrayList<>();
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public QuestionResponse(Page<Question> page) {
        for (Question question : page.getContent()) {
            this.questions.add(QuestionDTO.createAdminQuestion(question));
        }
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
        this.totalItems = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
