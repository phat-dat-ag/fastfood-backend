package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.dto.QuestionDTO;
import com.example.fastfoodshop.dto.TopicDifficultyDTO;
import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.Quiz;
import com.example.fastfoodshop.entity.QuizQuestion;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Data
public class QuizResponse {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime startedAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime completedAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime expiredAt;
    private UserDTO user;
    private TopicDifficultyDTO topicDifficulty;
    private PromotionDTO promotion;
    private List<QuestionDTO> questions;
    private String feedback;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime feedbackAt;

    private QuizResponse(Quiz quiz, List<QuestionDTO> questionDTOs) {
        this.id = quiz.getId();
        this.startedAt = quiz.getStartedAt()
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toOffsetDateTime();

        this.completedAt = quiz.getCompletedAt() != null
                ? quiz.getCompletedAt().atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toOffsetDateTime()
                : null;

        this.expiredAt = this.startedAt.plusSeconds(quiz.getTopicDifficulty().getDuration());
        this.user = new UserDTO(quiz.getUser());
        this.topicDifficulty = new TopicDifficultyDTO(quiz.getTopicDifficulty());
        this.promotion = quiz.getPromotion() != null ? new PromotionDTO(quiz.getPromotion()) : null;
        this.questions = questionDTOs;
        this.feedback = quiz.getFeedback();
        this.feedbackAt = quiz.getFeedbackAt() != null
                ? quiz.getFeedbackAt().atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toOffsetDateTime()
                : null;
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
