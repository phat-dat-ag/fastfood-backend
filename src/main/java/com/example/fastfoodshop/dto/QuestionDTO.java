package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Answer;
import com.example.fastfoodshop.entity.Question;

import java.util.ArrayList;
import java.util.List;

public record QuestionDTO(
        Long id,
        String content,
        String imageUrl,
        String audioUrl,
        boolean activated,
        List<AnswerDTO> answers
) {
    private static QuestionDTO create(Question question, List<AnswerDTO> answerDTOs) {
        return new QuestionDTO(
                question.getId(),
                question.getContent(),
                question.getImageUrl() != null ? question.getImageUrl() : "",
                question.getAudioUrl() != null ? question.getAudioUrl() : "",
                question.isActivated(),
                answerDTOs
        );
    }

    public static QuestionDTO createAdminQuestion(Question question) {
        List<AnswerDTO> answerDTOs = question.getAnswers()
                .stream().map(AnswerDTO::createAdminAnswer).toList();
        return create(question, answerDTOs);
    }

    public static QuestionDTO createUserQuestion(Question question) {
        List<AnswerDTO> answerDTOs = question.getAnswers()
                .stream().map(AnswerDTO::createUserAnswer).toList();
        return create(question, answerDTOs);
    }

    public static QuestionDTO createReviewQuestion(Question question, Answer selectedAnswer) {
        List<AnswerDTO> answerDTOs = new ArrayList<>();
        Long selectedId = selectedAnswer != null ? selectedAnswer.getId() : null;
        for (Answer answer : question.getAnswers()) {
            boolean isSelected = selectedId != null && selectedId.equals(answer.getId());
            answerDTOs.add(AnswerDTO.createReviewAnswer(answer, isSelected));
        }
        return create(question, answerDTOs);
    }
}
