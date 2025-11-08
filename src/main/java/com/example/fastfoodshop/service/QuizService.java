package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.Quiz;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.repository.QuizRepository;
import com.example.fastfoodshop.response.QuizResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final UserService userService;
    private final TopicDifficultyService topicDifficultyService;
    private final QuestionService questionService;
    private final QuizQuestionService quizQuestionService;
    private final QuizRepository quizRepository;

    private Quiz checkPlayableQuiz(User user) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<Quiz> quizzes = quizRepository.findByUserAndStartedAtBetween(user, startOfDay, endOfDay);

        int completedCount = 0;

        for (Quiz quiz : quizzes) {
            LocalDateTime expiredAt = quiz.getStartedAt().plusSeconds(quiz.getTopicDifficulty().getDuration());
            boolean isExpired = LocalDateTime.now().isAfter(expiredAt);

            if (!isExpired && quiz.getCompletedAt() == null) {
                return quiz;
            }

            if (quiz.getCompletedAt() != null || isExpired) completedCount++;
        }

        if (completedCount >= 3) return null;

        return new Quiz();
    }

    private Quiz saveQuiz(TopicDifficulty topicDifficulty, User user) {
        Quiz quiz = new Quiz();

        quiz.setUser(user);
        quiz.setTopicDifficulty(topicDifficulty);
        quiz.setStartedAt(LocalDateTime.now());

        return quizRepository.save(quiz);
    }

    private ArrayList<Question> getSelectedQuestions(TopicDifficulty topicDifficulty, List<Question> questions) {
        int requiredCount = topicDifficulty.getQuestionCount();
        if (questions.size() < requiredCount) {
            throw new RuntimeException("Không đủ câu hỏi để tạo thử thách");
        }

        Collections.shuffle(questions);
        return new ArrayList<>(questions.subList(0, requiredCount));
    }

    @Transactional
    public ResponseEntity<ResponseWrapper<QuizResponse>> getQuiz(String phone, String topicDifficultySlug) {
        try {
            User user = userService.findUserOrThrow(phone);
            Quiz existingOrPlayableQuiz = checkPlayableQuiz(user);

            if (existingOrPlayableQuiz == null) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                        "GET_QUIZ_FAILED",
                        "Đã hết lượt tham gia hôm nay"
                ));
            }

            if (existingOrPlayableQuiz.getId() != null) {
                return ResponseEntity.ok(ResponseWrapper.success(new QuizResponse(existingOrPlayableQuiz)));
            }

            TopicDifficulty topicDifficulty = topicDifficultyService.findPlayableTopicDifficultyBySlug(topicDifficultySlug);

            Quiz savedQuiz = saveQuiz(topicDifficulty, user);

            List<Question> questions = questionService.getAllValidQuestionsByTopicDifficulty(topicDifficulty);
            ArrayList<Question> selectedQuestions = getSelectedQuestions(topicDifficulty, questions);

            quizQuestionService.createQuizQuestions(savedQuiz, selectedQuestions);

            return ResponseEntity.ok(ResponseWrapper.success(new QuizResponse(savedQuiz, selectedQuestions)));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_QUIZ_FAILED",
                    "Lỗi lấy các câu hỏi của thử thách " + e.getMessage()
            ));
        }
    }
}
