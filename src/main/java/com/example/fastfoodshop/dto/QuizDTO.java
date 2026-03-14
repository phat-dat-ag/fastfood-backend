package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.Quiz;
import com.example.fastfoodshop.entity.QuizQuestion;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public record QuizDTO(
        Long id,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        OffsetDateTime startedAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        OffsetDateTime completedAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        OffsetDateTime expiredAt,
        UserDTO user,
        TopicDifficultyDTO topicDifficulty,
        PromotionDTO promotion,
        List<QuestionDTO> questions,
        String feedback,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        OffsetDateTime feedbackAt
) {
    private static QuizDTO create(Quiz quiz, List<QuestionDTO> questionDTOs) {
        OffsetDateTime startedAt = quiz.getStartedAt()
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toOffsetDateTime();

        OffsetDateTime completedAt = quiz.getCompletedAt() != null
                ? quiz.getCompletedAt().atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toOffsetDateTime()
                : null;

        OffsetDateTime expiredAt = startedAt.plusSeconds(quiz.getTopicDifficulty().getDuration());

        OffsetDateTime feedbackAt = quiz.getFeedbackAt() != null
                ? quiz.getFeedbackAt().atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toOffsetDateTime()
                : null;

        return new QuizDTO(
                quiz.getId(),
                startedAt,
                completedAt,
                expiredAt,
                UserDTO.from(quiz.getUser()),
                TopicDifficultyDTO.from(quiz.getTopicDifficulty()),
                quiz.getPromotion() != null ? PromotionDTO.from(quiz.getPromotion()) : null,
                questionDTOs,
                quiz.getFeedback(),
                feedbackAt
        );
    }

    public static QuizDTO createUserQuizResponse(Quiz quiz, ArrayList<Question> questions) {
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (Question question : questions) {
            questionDTOs.add(QuestionDTO.createUserQuestion(question));
        }
        return create(quiz, questionDTOs);
    }

    public static QuizDTO createUserQuizResponse(Quiz quiz) {
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (QuizQuestion quizQuestion : quiz.getQuizQuestions()) {
            questionDTOs.add(QuestionDTO.createUserQuestion(quizQuestion.getQuestion()));
        }
        return create(quiz, questionDTOs);
    }

    public static QuizDTO createReviewQuizResponse(Quiz quiz) {
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (QuizQuestion quizQuestion : quiz.getQuizQuestions()) {
            questionDTOs.add(QuestionDTO.createReviewQuestion(quizQuestion.getQuestion(), quizQuestion.getAnswer()));
        }
        return create(quiz, questionDTOs);
    }
}
