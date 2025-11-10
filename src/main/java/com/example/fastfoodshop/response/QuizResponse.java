package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.dto.QuestionDTO;
import com.example.fastfoodshop.dto.TopicDifficultyDTO;
import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.Quiz;
import com.example.fastfoodshop.entity.QuizQuestion;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class QuizResponse {
    private Long id;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime expiredAt;
    private TopicDifficultyDTO topicDifficulty;
    private PromotionDTO promotion;
    private List<QuestionDTO> questions;

    private QuizResponse(Quiz quiz, List<QuestionDTO> questionDTOs) {
        this.id = quiz.getId();
        this.startedAt = quiz.getStartedAt();
        this.completedAt = quiz.getCompletedAt();
        this.expiredAt = quiz.getStartedAt().plusSeconds(quiz.getTopicDifficulty().getDuration());
        this.topicDifficulty = new TopicDifficultyDTO(quiz.getTopicDifficulty());
        this.promotion = quiz.getPromotion() != null ? new PromotionDTO(quiz.getPromotion()) : null;
        this.questions = questionDTOs;
    }

    public static QuizResponse createUserQuizResponse(Quiz quiz, ArrayList<Question> questions) {
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (Question question : questions) {
            questionDTOs.add(QuestionDTO.createUserQuestion(question));
        }
        return new QuizResponse(quiz, questionDTOs);
    }

    public static QuizResponse createUserQuizResponse(Quiz quiz) {
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (QuizQuestion quizQuestion : quiz.getQuizQuestions()) {
            questionDTOs.add(QuestionDTO.createUserQuestion(quizQuestion.getQuestion()));
        }
        return new QuizResponse(quiz, questionDTOs);
    }

    public static QuizResponse createReviewQuizResponse(Quiz quiz) {
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (QuizQuestion quizQuestion : quiz.getQuizQuestions()) {
            questionDTOs.add(QuestionDTO.createReviewQuestion(quizQuestion.getQuestion(), quizQuestion.getAnswer()));
        }
        return new QuizResponse(quiz, questionDTOs);
    }
}
