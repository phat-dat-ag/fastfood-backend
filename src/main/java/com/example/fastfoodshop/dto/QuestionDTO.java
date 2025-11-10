package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Answer;
import com.example.fastfoodshop.entity.Question;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuestionDTO {
    private Long id;
    private String content;
    private String imageUrl;
    private String audioUrl;
    private boolean isActivated;
    private List<AnswerDTO> answers;

    private QuestionDTO(Question question, List<AnswerDTO> answerDTOs) {
        this.id = question.getId();
        this.content = question.getContent();
        this.imageUrl = question.getImageUrl() != null ? question.getImageUrl() : "";
        this.audioUrl = question.getAudioUrl() != null ? question.getAudioUrl() : "";
        this.isActivated = question.isActivated();
        this.answers = answerDTOs;
    }

    public static QuestionDTO createAdminQuestion(Question question) {
        ArrayList<AnswerDTO> answerDTOs = new ArrayList<>();
        for (Answer answer : question.getAnswers()) {
            answerDTOs.add(AnswerDTO.createAdminAnswer(answer));
        }
        return new QuestionDTO(question, answerDTOs);
    }

    public static QuestionDTO createUserQuestion(Question question) {
        ArrayList<AnswerDTO> answerDTOs = new ArrayList<>();
        for (Answer answer : question.getAnswers()) {
            answerDTOs.add(AnswerDTO.createUserAnswer(answer));
        }
        return new QuestionDTO(question, answerDTOs);
    }

    public static QuestionDTO createReviewQuestion(Question question, Answer selectedAnswer) {
        ArrayList<AnswerDTO> answerDTOs = new ArrayList<>();
        Long selectedId = selectedAnswer != null ? selectedAnswer.getId() : null;
        for (Answer answer : question.getAnswers()) {
            boolean isSelected = selectedId != null && selectedId.equals(answer.getId());
            answerDTOs.add(AnswerDTO.createReviewAnswer(answer, isSelected));
        }
        return new QuestionDTO(question, answerDTOs);
    }
}
