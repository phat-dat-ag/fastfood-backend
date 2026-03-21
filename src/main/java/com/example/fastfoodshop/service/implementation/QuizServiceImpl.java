package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.constant.QuizConstants;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.entity.Quiz;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.QuizQuestion;
import com.example.fastfoodshop.entity.Answer;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.exception.quiz.QuizAlreadyFeedbackException;
import com.example.fastfoodshop.exception.quiz.GameTimeExpiredException;
import com.example.fastfoodshop.exception.quiz.InvalidQuizException;
import com.example.fastfoodshop.exception.quiz.NotEnoughQuestionsException;
import com.example.fastfoodshop.exception.quiz.NoAttemptsRemainingException;
import com.example.fastfoodshop.exception.quiz.QuizHistoryNotFoundException;
import com.example.fastfoodshop.repository.AnswerRepository;
import com.example.fastfoodshop.repository.QuizRepository;
import com.example.fastfoodshop.request.QuizAddFeedbackRequest;
import com.example.fastfoodshop.request.QuizQuestionSubmitRequest;
import com.example.fastfoodshop.request.QuizSubmitRequest;
import com.example.fastfoodshop.response.quiz.QuizFeedbackPageResponse;
import com.example.fastfoodshop.response.quiz.QuizHistoryPageResponse;
import com.example.fastfoodshop.dto.QuizDTO;
import com.example.fastfoodshop.response.quiz.QuizResponse;
import com.example.fastfoodshop.response.quiz.QuizUpdateResponse;
import com.example.fastfoodshop.service.QuestionService;
import com.example.fastfoodshop.service.QuizQuestionService;
import com.example.fastfoodshop.service.TopicDifficultyService;
import com.example.fastfoodshop.service.UserService;
import com.example.fastfoodshop.service.PromotionService;
import com.example.fastfoodshop.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final UserService userService;
    private final TopicDifficultyService topicDifficultyService;
    private final QuestionService questionService;
    private final QuizQuestionService quizQuestionService;
    private final PromotionService promotionService;
    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;

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
            throw new NotEnoughQuestionsException();
        }

        Collections.shuffle(questions);
        return new ArrayList<>(questions.subList(0, requiredCount));
    }

    private Quiz findUncompletedQuizOrThrow(Long quizId, User user, TopicDifficulty topicDifficulty) {
        return quizRepository.findByIdAndUserAndTopicDifficultyAndCompletedAtIsNull(quizId, user, topicDifficulty)
                .orElseThrow(() -> new InvalidQuizException(quizId));
    }

    private Quiz findQuizHistoryOrThrow(Long quizId, User user) {
        return quizRepository.findByIdAndUserAndCompletedAtIsNotNull(quizId, user).orElseThrow(
                () -> new QuizHistoryNotFoundException(quizId)
        );
    }

    @Transactional
    public QuizResponse getQuiz(String phone, String topicDifficultySlug) {
        User user = userService.findUserOrThrow(phone);
        Quiz existingOrPlayableQuiz = checkPlayableQuiz(user);

        if (existingOrPlayableQuiz == null) {
            throw new NoAttemptsRemainingException();
        }

        if (existingOrPlayableQuiz.getId() != null) {
            return new QuizResponse(QuizDTO.createUserQuizResponse(existingOrPlayableQuiz));
        }

        TopicDifficulty topicDifficulty = topicDifficultyService.findPlayableTopicDifficultyBySlug(topicDifficultySlug);

        Quiz savedQuiz = saveQuiz(topicDifficulty, user);

        List<Question> questions = questionService.getAllValidQuestionsByTopicDifficulty(topicDifficulty);
        ArrayList<Question> selectedQuestions = getSelectedQuestions(topicDifficulty, questions);

        quizQuestionService.createQuizQuestions(savedQuiz, selectedQuestions);

        return new QuizResponse(QuizDTO.createUserQuizResponse(savedQuiz, selectedQuestions));
    }

    private Map<Long, Long> generateSubmittedMap(List<QuizQuestionSubmitRequest> quizQuestionSubmits) {
        Map<Long, Long> submittedMap = new HashMap<>();
        for (QuizQuestionSubmitRequest questionSubmitRequest : quizQuestionSubmits) {
            submittedMap.put(questionSubmitRequest.getQuestionId(), questionSubmitRequest.getAnswerId());
        }
        return submittedMap;
    }

    private Map<Long, Answer> generateAnswerMap(Map<Long, Long> submittedMap) {
        List<Long> answerIds = submittedMap.values().stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        return answerRepository.findAllById(answerIds).stream()
                .collect(Collectors.toMap(Answer::getId, Function.identity()));
    }

    private int updateQuizAnswerAndCalculateScore(
            List<QuizQuestion> quizQuestions,
            Map<Long, Long> submittedMap,
            Map<Long, Answer> answerMap
    ) {
        int score = 0;

        for (QuizQuestion quizQuestion : quizQuestions) {
            Long questionId = quizQuestion.getQuestion().getId();
            Long answerId = submittedMap.get(questionId);

            if (answerId == null) {
                quizQuestion.setAnswer(null);
                continue;
            }

            Answer answer = answerMap.get(answerId);
            if (answer == null || !answer.getQuestion().getId().equals(questionId)) {
                quizQuestion.setAnswer(null);
                continue;
            }

            quizQuestion.setAnswer(answer);
            if (answer.isCorrect()) score++;
        }

        return score;
    }

    @Transactional
    public QuizResponse checkQuizSubmission(String phone, QuizSubmitRequest quizSubmitRequest) {
        User user = userService.findUserOrThrow(phone);
        TopicDifficulty topicDifficulty = topicDifficultyService
                .findValidTopicDifficultyOrThrow(quizSubmitRequest.getTopicDifficultySlug());
        Quiz quiz = findUncompletedQuizOrThrow(quizSubmitRequest.getQuizId(), user, topicDifficulty);

        LocalDateTime now = LocalDateTime.now();
        long totalDuration = quiz.getTopicDifficulty().getDuration() + QuizConstants.SUBMIT_TIME_BUFFER_SECONDS;
        LocalDateTime expiredAt = quiz.getStartedAt().plusSeconds(totalDuration);

        if (now.isAfter(expiredAt)) {
            throw new GameTimeExpiredException();
        }

        Map<Long, Long> submittedMap = generateSubmittedMap(quizSubmitRequest.getQuizQuestions());

        Map<Long, Answer> answerMap = generateAnswerMap(submittedMap);

        List<QuizQuestion> quizQuestions = quiz.getQuizQuestions();

        quiz.setCompletedAt(LocalDateTime.now());

        int score = updateQuizAnswerAndCalculateScore(quizQuestions, submittedMap, answerMap);
        if (score >= topicDifficulty.getMinCorrectToReward()) {
            Promotion promotion = promotionService.grantPromotion(user, quiz);
            quiz.setPromotion(promotion);
        }

        Quiz checkedQuiz = quizRepository.save(quiz);

        return new QuizResponse(QuizDTO.createUserQuizResponse(checkedQuiz));
    }

    public QuizUpdateResponse addFeedbackToCompletedQuiz(String phone, QuizAddFeedbackRequest quizAddFeedbackRequest) {
        User user = userService.findUserOrThrow(phone);
        Quiz quiz = findQuizHistoryOrThrow(quizAddFeedbackRequest.quizId(), user);

        if (quiz.getFeedbackAt() != null) {
            throw new QuizAlreadyFeedbackException();
        }

        LocalDateTime completedAt = quiz.getCompletedAt();
        boolean canGiveFeedback = completedAt != null
                && completedAt.plusDays(QuizConstants.FEEDBACK_ALLOWED_DURATION_DAYS)
                .isAfter(LocalDateTime.now());

        if (canGiveFeedback) {
            quiz.setFeedback(quizAddFeedbackRequest.feedback());
            quiz.setFeedbackAt(LocalDateTime.now());
        }
        quizRepository.save(quiz);

        return new QuizUpdateResponse("Đã thêm đánh giá cho trò chơi");
    }

    public QuizHistoryPageResponse getAllHistoryQuizzesByUser(String phone, int page, int size) {
        User user = userService.findUserOrThrow(phone);
        Pageable pageable = PageRequest.of(page, size);
        Page<Quiz> quizPage = quizRepository.findByUserAndCompletedAtIsNotNull(user, pageable);

        return QuizHistoryPageResponse.from(quizPage);
    }

    public QuizResponse getQuizHistoryDetailByUser(String phone, Long quizId) {
        User user = userService.findUserOrThrow(phone);
        Quiz quiz = findQuizHistoryOrThrow(quizId, user);
        return new QuizResponse(QuizDTO.createReviewQuizResponse(quiz));
    }

    public QuizFeedbackPageResponse getAllFeedbacksByAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Quiz> quizPage = quizRepository.findByFeedbackAtIsNotNull(pageable);

        return QuizFeedbackPageResponse.from(quizPage);
    }
}
