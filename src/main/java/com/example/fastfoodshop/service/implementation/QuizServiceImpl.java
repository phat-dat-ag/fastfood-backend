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
import com.example.fastfoodshop.exception.quiz.NotAllowFeedbackException;
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

    private List<Quiz> getQuizzesToday(User user) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return quizRepository.findByUserAndStartedAtBetween(user, startOfDay, endOfDay);
    }

    private Quiz findPlayableQuizOrThrow(List<Quiz> quizzes) {
        int completedCount = 0;

        for (Quiz quiz : quizzes) {
            int durationSeconds = quiz.getTopicDifficulty().getDuration();

            LocalDateTime expiredAt = quiz.getStartedAt().plusSeconds(durationSeconds);

            boolean isExpired = LocalDateTime.now().isAfter(expiredAt);

            if (!isExpired && quiz.getCompletedAt() == null) {
                return quiz;
            }

            completedCount++;
        }

        if (completedCount >= 3) {
            throw new NoAttemptsRemainingException();
        }

        return new Quiz();
    }

    private Quiz createNewQuiz(TopicDifficulty topicDifficulty, User user) {
        Quiz quiz = new Quiz();

        quiz.setUser(user);
        quiz.setTopicDifficulty(topicDifficulty);
        quiz.setStartedAt(LocalDateTime.now());

        return quizRepository.save(quiz);
    }

    private ArrayList<Question> selectRandomQuestions(TopicDifficulty topicDifficulty, List<Question> questions) {
        int requiredCount = topicDifficulty.getQuestionCount();
        if (questions.size() < requiredCount) {
            throw new NotEnoughQuestionsException();
        }

        Collections.shuffle(questions);
        return new ArrayList<>(questions.subList(0, requiredCount));
    }

    private Quiz findQuizHistoryOrThrow(Long quizId, User user) {
        return quizRepository.findByIdAndUserAndCompletedAtIsNotNull(quizId, user).orElseThrow(
                () -> new QuizHistoryNotFoundException(quizId)
        );
    }

    @Transactional
    public QuizResponse getQuiz(String phone, String topicDifficultySlug) {
        User user = userService.findUserOrThrow(phone);

        List<Quiz> quizzesToday = getQuizzesToday(user);

        Quiz existingOrPlayableQuiz = findPlayableQuizOrThrow(quizzesToday);

        if (existingOrPlayableQuiz.getId() != null) {
            return new QuizResponse(QuizDTO.createUserQuizResponse(existingOrPlayableQuiz));
        }

        TopicDifficulty topicDifficulty =
                topicDifficultyService.findPlayableTopicDifficultyBySlug(topicDifficultySlug);

        Quiz createdQuiz = createNewQuiz(topicDifficulty, user);

        List<Question> questions = questionService.getAllValidQuestionsByTopicDifficulty(topicDifficulty);

        ArrayList<Question> selectedQuestions = selectRandomQuestions(topicDifficulty, questions);

        quizQuestionService.createQuizQuestions(createdQuiz, selectedQuestions);

        return new QuizResponse(QuizDTO.createUserQuizResponse(createdQuiz, selectedQuestions));
    }

    private void validateQuizNotExpired(Quiz quiz) {
        LocalDateTime now = LocalDateTime.now();
        long totalDuration = quiz.getTopicDifficulty().getDuration() + QuizConstants.SUBMIT_TIME_BUFFER_SECONDS;
        LocalDateTime expiredAt = quiz.getStartedAt().plusSeconds(totalDuration);

        if (now.isAfter(expiredAt)) {
            throw new GameTimeExpiredException();
        }
    }

    private Map<Long, Long> mapQuestionToSubmittedAnswerIds(
            List<QuizQuestionSubmitRequest> quizQuestionSubmits
    ) {
        return quizQuestionSubmits
                .stream()
                .collect(
                        Collectors.toMap(
                                QuizQuestionSubmitRequest::questionId,
                                QuizQuestionSubmitRequest::answerId,
                                (oldKey, newKey) -> newKey
                        )
                );
    }

    private Map<Long, Answer> mapAnswersById(Map<Long, Long> submittedMap) {
        List<Long> answerIds = submittedMap.values().stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return answerRepository
                .findAllById(answerIds)
                .stream()
                .collect(
                        Collectors.toMap(Answer::getId, Function.identity())
                );
    }

    private boolean isValidAnswer(Long questionId, Answer answer) {
        return answer != null && answer.getQuestion().getId().equals(questionId);
    }

    private int applyAnswersAndCalculateScore(
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

            if (!isValidAnswer(questionId, answer)) {
                quizQuestion.setAnswer(null);
                continue;
            }

            quizQuestion.setAnswer(answer);
            if (answer.isCorrect()) score++;
        }

        return score;
    }

    private Quiz findUncompletedQuizOrThrow(Long quizId, User user, TopicDifficulty topicDifficulty) {
        return quizRepository.findByIdAndUserAndTopicDifficultyAndCompletedAtIsNull(quizId, user, topicDifficulty)
                .orElseThrow(() -> new InvalidQuizException(quizId));
    }

    @Transactional
    public QuizResponse submitQuiz(String phone, QuizSubmitRequest quizSubmitRequest) {
        User user = userService.findUserOrThrow(phone);

        TopicDifficulty topicDifficulty = topicDifficultyService
                .findValidTopicDifficultyOrThrow(quizSubmitRequest.topicDifficultySlug());

        Quiz quiz = findUncompletedQuizOrThrow(quizSubmitRequest.quizId(), user, topicDifficulty);

        validateQuizNotExpired(quiz);

        Map<Long, Long> submittedMap = mapQuestionToSubmittedAnswerIds(quizSubmitRequest.quizQuestions());

        Map<Long, Answer> answerMap = mapAnswersById(submittedMap);

        List<QuizQuestion> quizQuestions = quiz.getQuizQuestions();

        quiz.setCompletedAt(LocalDateTime.now());

        int score = applyAnswersAndCalculateScore(quizQuestions, submittedMap, answerMap);

        if (score >= topicDifficulty.getMinCorrectToReward()) {
            Promotion promotion = promotionService.grantPromotion(user, quiz);
            quiz.setPromotion(promotion);
        }

        Quiz checkedQuiz = quizRepository.save(quiz);

        return new QuizResponse(QuizDTO.createUserQuizResponse(checkedQuiz));
    }

    private void checkFeedbackCompletion(Quiz quiz) {
        if (quiz.getFeedbackAt() != null) {
            throw new QuizAlreadyFeedbackException();
        }
    }

    private void checkFeedbackPermission(Quiz quiz) {
        LocalDateTime completedAt = quiz.getCompletedAt();

        boolean isCompleted = completedAt != null;

        if (!isCompleted) {
            throw new NotAllowFeedbackException();
        }

        LocalDateTime allowedDuration = completedAt.plusDays(QuizConstants.FEEDBACK_ALLOWED_DURATION_DAYS);

        boolean isWithinAllowedDuration = allowedDuration.isAfter(LocalDateTime.now());

        if (!isWithinAllowedDuration) {
            throw new NotAllowFeedbackException();
        }
    }

    private void setFeedback(Quiz quiz, String feedbackContent) {
        quiz.setFeedback(feedbackContent);
        quiz.setFeedbackAt(LocalDateTime.now());
    }

    public QuizUpdateResponse addFeedbackToQuiz(
            String phone, Long quizId, QuizAddFeedbackRequest quizAddFeedbackRequest
    ) {
        User user = userService.findUserOrThrow(phone);
        Quiz quiz = findQuizHistoryOrThrow(quizId, user);

        checkFeedbackCompletion(quiz);

        checkFeedbackPermission(quiz);

        setFeedback(quiz, quizAddFeedbackRequest.feedback());

        quizRepository.save(quiz);

        return new QuizUpdateResponse("Đã thêm đánh giá cho trò chơi");
    }

    public QuizHistoryPageResponse getQuizHistories(String phone, int page, int size) {
        User user = userService.findUserOrThrow(phone);

        Pageable pageable = PageRequest.of(page, size);
        Page<Quiz> quizPage = quizRepository.findByUserAndCompletedAtIsNotNull(user, pageable);

        return QuizHistoryPageResponse.from(quizPage);
    }

    public QuizResponse getQuizHistory(String phone, Long quizId) {
        User user = userService.findUserOrThrow(phone);

        Quiz quiz = findQuizHistoryOrThrow(quizId, user);

        return new QuizResponse(QuizDTO.createReviewQuizResponse(quiz));
    }

    public QuizFeedbackPageResponse getAllQuizFeedbacks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Quiz> quizPage = quizRepository.findByFeedbackAtIsNotNull(pageable);

        return QuizFeedbackPageResponse.from(quizPage);
    }
}
