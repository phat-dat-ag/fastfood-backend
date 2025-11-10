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

@Data
public class QuizResponse {
    private Long id;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime expiredAt;
    private TopicDifficultyDTO topicDifficulty;
    private PromotionDTO promotion;
    private ArrayList<QuestionDTO> questions = new ArrayList<>();

    public QuizResponse(Quiz quiz, ArrayList<Question> questions) {
        this.id = quiz.getId();
        this.startedAt = quiz.getStartedAt();
        this.completedAt = quiz.getCompletedAt();
        this.expiredAt = quiz.getStartedAt().plusSeconds(quiz.getTopicDifficulty().getDuration());
        this.topicDifficulty = new TopicDifficultyDTO(quiz.getTopicDifficulty());
        this.promotion = quiz.getPromotion() != null ? new PromotionDTO(quiz.getPromotion()) : null;
        for (Question question : questions) {
            this.questions.add(QuestionDTO.createUserQuestion(question));
        }
    }

    public QuizResponse(Quiz quiz) {
        this.id = quiz.getId();
        this.startedAt = quiz.getStartedAt();
        this.completedAt = quiz.getCompletedAt();
        this.expiredAt = quiz.getStartedAt().plusSeconds(quiz.getTopicDifficulty().getDuration());
        this.topicDifficulty = new TopicDifficultyDTO(quiz.getTopicDifficulty());
        this.promotion = quiz.getPromotion() != null ? new PromotionDTO(quiz.getPromotion()) : null;
        for (QuizQuestion quizQuestion : quiz.getQuizQuestions()) {
            this.questions.add(QuestionDTO.createUserQuestion(quizQuestion.getQuestion()));
        }
    }
}
