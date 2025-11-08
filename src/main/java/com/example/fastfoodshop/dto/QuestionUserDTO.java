package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Answer;
import com.example.fastfoodshop.entity.Question;
import lombok.Data;

import java.util.ArrayList;

@Data
public class QuestionUserDTO {
    private Long id;
    private String content;
    private String imageUrl;
    private String audioUrl;
    private boolean isActivated;
    private ArrayList<AnswerUserDTO> answers = new ArrayList<>();

    public QuestionUserDTO(Question question) {
        this.id = question.getId();
        this.content = question.getContent();
        this.imageUrl = question.getImageUrl() != null ? question.getImageUrl() : "";
        this.audioUrl = question.getAudioUrl() != null ? question.getAudioUrl() : "";
        this.isActivated = question.isActivated();
        for (Answer answer : question.getAnswers()) {
            this.answers.add(new AnswerUserDTO(answer));
        }
    }
}
