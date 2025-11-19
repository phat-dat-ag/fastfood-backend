package com.example.fastfoodshop.service;

import com.example.fastfoodshop.constant.QuizConstants;
import com.example.fastfoodshop.entity.*;
import com.example.fastfoodshop.repository.AnswerRepository;
import com.example.fastfoodshop.repository.QuizRepository;
import com.example.fastfoodshop.request.QuizQuestionSubmitRequest;
import com.example.fastfoodshop.response.QuizFeedbackResponse;
import com.example.fastfoodshop.response.QuizHistoryResponse;
import com.example.fastfoodshop.response.QuizResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {
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
            throw new RuntimeException("Không đủ câu hỏi để tạo thử thách");
        }

        Collections.shuffle(questions);
        return new ArrayList<>(questions.subList(0, requiredCount));
    }

    private Quiz findUncompletedQuizOrThrow(Long quizId, User user, TopicDifficulty topicDifficulty) {
        return quizRepository.findByIdAndUserAndTopicDifficultyAndCompletedAtIsNull(quizId, user, topicDifficulty)
                .orElseThrow(() -> new RuntimeException("Bài kiểm tra này không hợp lệ"));
    }

    private Quiz findQuizHistoryOrThrow(Long quizId, User user) {
        return quizRepository.findByIdAndUserAndCompletedAtIsNotNull(quizId, user).orElseThrow(
                () -> new RuntimeException("Không tìm thấy lịch sử tham gia của người dùng")
        );
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
                return ResponseEntity.ok(ResponseWrapper.success(QuizResponse.createUserQuizResponse(existingOrPlayableQuiz)));
            }

            TopicDifficulty topicDifficulty = topicDifficultyService.findPlayableTopicDifficultyBySlug(topicDifficultySlug);

            Quiz savedQuiz = saveQuiz(topicDifficulty, user);

            List<Question> questions = questionService.getAllValidQuestionsByTopicDifficulty(topicDifficulty);
            ArrayList<Question> selectedQuestions = getSelectedQuestions(topicDifficulty, questions);

            quizQuestionService.createQuizQuestions(savedQuiz, selectedQuestions);

            return ResponseEntity.ok(ResponseWrapper.success(QuizResponse.createUserQuizResponse(savedQuiz, selectedQuestions)));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_QUIZ_FAILED",
                    "Lỗi lấy các câu hỏi của thử thách " + e.getMessage()
            ));
        }
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
    public ResponseEntity<ResponseWrapper<QuizResponse>> checkQuizSubmission(
            String phone, Long quizId, String topicDifficultySlug, List<QuizQuestionSubmitRequest> quizQuestionSubmits
    ) {
        try {
            User user = userService.findUserOrThrow(phone);
            TopicDifficulty topicDifficulty = topicDifficultyService.findValidTopicDifficultyOrThrow(topicDifficultySlug);
            Quiz quiz = findUncompletedQuizOrThrow(quizId, user, topicDifficulty);

            LocalDateTime now = LocalDateTime.now();
            long totalDuration = quiz.getTopicDifficulty().getDuration() + QuizConstants.SUBMIT_TIME_BUFFER_SECONDS;
            LocalDateTime expiredAt = quiz.getStartedAt().plusSeconds(totalDuration);

            if (now.isAfter(expiredAt)) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                        "CHECK_QUIZ_FAILED",
                        "Đã hết thời gian làm bài!"
                ));
            }

            Map<Long, Long> submittedMap = generateSubmittedMap(quizQuestionSubmits);

            Map<Long, Answer> answerMap = generateAnswerMap(submittedMap);

            List<QuizQuestion> quizQuestions = quiz.getQuizQuestions();

            quiz.setCompletedAt(LocalDateTime.now());

            int score = updateQuizAnswerAndCalculateScore(quizQuestions, submittedMap, answerMap);
            if (score >= topicDifficulty.getMinCorrectToReward()) {
                Promotion promotion = promotionService.grantPromotion(user, quiz);
                quiz.setPromotion(promotion);
            }

            Quiz checkedQuiz = quizRepository.save(quiz);

            return ResponseEntity.ok(ResponseWrapper.success(QuizResponse.createUserQuizResponse(checkedQuiz)));
        } catch (RuntimeException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "CHECK_QUIZ_FAILED",
                    "Lỗi khi chấm bài kiểm tra " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<String>> addFeedbackToCompletedQuiz(String phone, Long quizId, String feedback) {
        try {
            User user = userService.findUserOrThrow(phone);
            Quiz quiz = findQuizHistoryOrThrow(quizId, user);

            if (quiz.getFeedbackAt() != null) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                        "ADD_FEEDBACK_TO_QUIZ_FAILED",
                        "Trò chơi này đã được đánh giá"
                ));
            }

            LocalDateTime completedAt = quiz.getCompletedAt();
            boolean canGiveFeedback = completedAt != null
                    && completedAt.plusDays(QuizConstants.FEEDBACK_ALLOWED_DURATION_DAYS)
                    .isAfter(LocalDateTime.now());

            if (canGiveFeedback) {
                quiz.setFeedback(feedback);
                quiz.setFeedbackAt(LocalDateTime.now());
            }
            quizRepository.save(quiz);

            return ResponseEntity.ok(ResponseWrapper.success("Đã thêm đánh giá cho trò chơi"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "ADD_FEEDBACK_TO_QUIZ_FAILED",
                    "Lỗi thêm đánh giá cho trò chơi " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<QuizHistoryResponse>> getAllHistoryQuizzesByUser(String phone, int page, int size) {
        try {
            User user = userService.findUserOrThrow(phone);
            Pageable pageable = PageRequest.of(page, size);
            Page<Quiz> quizPage = quizRepository.findByUserAndCompletedAtIsNotNull(user, pageable);

            return ResponseEntity.ok(ResponseWrapper.success(new QuizHistoryResponse(quizPage)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_REVIEW_QUIZZES_BY_USER_FAILED",
                    "Lỗi lấy lịch sử thách thức " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<QuizResponse>> getQuizHistoryDetailByUser(String phone, Long quizId) {
        try {
            User user = userService.findUserOrThrow(phone);
            Quiz quiz = findQuizHistoryOrThrow(quizId, user);
            return ResponseEntity.ok(ResponseWrapper.success(QuizResponse.createReviewQuizResponse(quiz)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_QUIZ_HISTORY_DETAIL_FAILED",
                    "Lỗi lấy chi tiết lịch sử thử thách " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<QuizFeedbackResponse>> getAllFeedbacksByAdmin(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Quiz> quizPage = quizRepository.findByFeedbackAtIsNotNull(pageable);

            return ResponseEntity.ok(ResponseWrapper.success(new QuizFeedbackResponse(quizPage)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ALL_FEEDBACKS_FAILED",
                    "Lỗi lấy các góp ý trò chơi " + e.getMessage()
            ));
        }
    }
}
