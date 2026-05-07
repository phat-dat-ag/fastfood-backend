package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.exception.question.DeletedQuestionException;
import com.example.fastfoodshop.exception.question.InvalidQuestionStatusException;
import com.example.fastfoodshop.exception.question.QuestionNotFoundException;
import com.example.fastfoodshop.repository.QuestionRepository;
import com.example.fastfoodshop.request.QuestionCreateRequest;
import com.example.fastfoodshop.response.question.QuestionPageResponse;
import com.example.fastfoodshop.response.question.QuestionUpdateResponse;
import com.example.fastfoodshop.service.AnswerService;
import com.example.fastfoodshop.service.CloudinaryService;
import com.example.fastfoodshop.service.QuestionService;
import com.example.fastfoodshop.service.TopicDifficultyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final CloudinaryService cloudinaryService;
    private final TopicDifficultyService topicDifficultyService;
    private final QuestionRepository questionRepository;
    private final AnswerService answerService;

    private Question findQuestionOrThrow(Long questionId) {
        return questionRepository.findById(questionId).orElseThrow(
                () -> new QuestionNotFoundException(questionId)
        );
    }

    private void handleQuestionImage(Question question, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty())
            return;

        String oldPublicId = question.getImagePublicId();
        Map<?, ?> result = cloudinaryService.uploadImage(imageFile, "question-image");

        question.setImageUrl((String) result.get("secure_url"));
        question.setImagePublicId((String) result.get("public_id"));

        if (oldPublicId != null && !oldPublicId.isEmpty()) {
            try {
                cloudinaryService.deleteImage(oldPublicId);
                log.debug("[Question Service] Successfully deleted old question image {}", oldPublicId);
            } catch (Exception e) {
                log.warn("[Question Service] Delete old question image failed: {}", oldPublicId, e);
            }
        }
    }

    private void handleQuestionAudio(Question question, MultipartFile audioFile) {
        if (audioFile == null || audioFile.isEmpty())
            return;

        String oldAudioPublicId = question.getAudioPublicId();
        Map<?, ?> result = cloudinaryService.uploadAudio(audioFile, "question-audio");

        question.setAudioUrl((String) result.get("secure_url"));
        question.setAudioPublicId((String) result.get("public_id"));

        if (oldAudioPublicId != null && !oldAudioPublicId.isEmpty()) {
            try {
                cloudinaryService.deleteAudio(oldAudioPublicId);
                log.debug("[Question Service] Successfully deleted old question audio {}", oldAudioPublicId);
            } catch (Exception e) {
                log.warn("[Question Service] Delete old question audio failed: {}", oldAudioPublicId, e);
            }
        }
    }

    private Question buildQuestion(TopicDifficulty topicDifficulty, QuestionCreateRequest questionCreateRequest) {
        Question question = new Question();

        question.setTopicDifficulty(topicDifficulty);
        question.setContent(questionCreateRequest.content());
        question.setActivated(questionCreateRequest.activated());

        handleQuestionImage(question, questionCreateRequest.imageUrl());
        handleQuestionAudio(question, questionCreateRequest.audioUrl());

        log.debug("[Question Service] Successfully built question entity");

        return question;
    }

    @Transactional
    public QuestionUpdateResponse createQuestions(
            List<QuestionCreateRequest> questionCreateRequests, String topicDifficultySlug
    ) {
        TopicDifficulty topicDifficulty = topicDifficultyService
                .findValidTopicDifficultyOrThrow(topicDifficultySlug);

        for (QuestionCreateRequest questionCreateRequest : questionCreateRequests) {
            Question question = buildQuestion(topicDifficulty, questionCreateRequest);

            questionRepository.save(question);

            answerService.createAnswers(questionCreateRequest.answers(), question);
        }

        log.info("[Question Service] Successfully created questions");

        return new QuestionUpdateResponse("Đã lưu các câu hỏi");
    }

    public QuestionPageResponse getAllQuestionsByTopicDifficulty(
            String topicDifficultySlug, int page, int size
    ) {
        TopicDifficulty topicDifficulty = topicDifficultyService
                .findValidTopicDifficultyOrThrow(topicDifficultySlug);

        Pageable pageable = PageRequest.of(page, size);

        Page<Question> questionPage = questionRepository
                .findByTopicDifficultyAndIsDeletedFalse(topicDifficulty, pageable);

        log.info(
                "[Question Service] Successfully got question page by topic difficulty: {}",
                topicDifficultySlug
        );

        return QuestionPageResponse.from(questionPage);
    }

    public QuestionUpdateResponse updateQuestionActivation(Long questionId, boolean activated) {
        Question question = findQuestionOrThrow(questionId);
        if (question.isActivated() == activated) {
            throw new InvalidQuestionStatusException(questionId);
        }

        question.setActivated(activated);
        questionRepository.save(question);

        String message = activated
                ? "Đã kích hoạt câu hỏi: " + questionId
                : "Đã hủy kích hoạt câu hỏi: " + questionId;

        log.info(
                "[Question Service] Successfully update activation for question id={}, new status: {}",
                questionId, activated
        );

        return new QuestionUpdateResponse(message);
    }

    public QuestionUpdateResponse deleteQuestion(Long questionId) {
        Question question = findQuestionOrThrow(questionId);
        if (question.isDeleted()) {
            throw new DeletedQuestionException(questionId);
        }

        question.setDeleted(true);
        questionRepository.save(question);

        log.info("[Question Service] Successfully deleted question id={}", questionId);

        return new QuestionUpdateResponse("Xóa câu hỏi thành công: " + questionId);
    }

    public List<Question> getAllValidQuestionsByTopicDifficulty(TopicDifficulty topicDifficulty) {
        return questionRepository.findByTopicDifficultyAndIsActivatedTrueAndIsDeletedFalse(topicDifficulty);
    }
}
