package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Answer;
import com.example.fastfoodshop.entity.Question;
import lombok.Data;

import java.util.ArrayList;

@Data
public class QuestionDTO {
    private Long id;
    private String content;
    private String imageUrl;
    private String audioUrl;
    private boolean isActivated;
    private ArrayList<AnswerDTO> answers = new ArrayList<>();

    public QuestionDTO(Question question) {
        this.id = question.getId();
        this.content = question.getContent();
        this.imageUrl = question.getImageUrl() != null ? question.getImageUrl() : "";
        this.audioUrl = question.getAudioUrl() != null ? question.getAudioUrl() : "";
        this.isActivated = question.isActivated();
        for (Answer answer : question.getAnswers()) {
            this.answers.add(new AnswerDTO(answer));
        }
    }
}
