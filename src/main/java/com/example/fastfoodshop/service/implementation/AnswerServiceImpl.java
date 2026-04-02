package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.constant.FolderNameConstants;
import com.example.fastfoodshop.constant.QuizConstants;
import com.example.fastfoodshop.entity.Answer;
import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.exception.question.InvalidAnswerCountException;
import com.example.fastfoodshop.exception.question.InvalidCorrectAnswerCountException;
import com.example.fastfoodshop.repository.AnswerRepository;
import com.example.fastfoodshop.request.AnswerCreateRequest;
import com.example.fastfoodshop.service.AnswerService;
import com.example.fastfoodshop.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final CloudinaryService cloudinaryService;
    private final AnswerRepository answerRepository;

    private void handleAnswerImage(Answer answer, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty())
            return;

        String oldPublicId = answer.getImagePublicId();
        Map<?, ?> result = cloudinaryService.uploadImage(imageFile, FolderNameConstants.answerFolderName);

        answer.setImageUrl((String) result.get("secure_url"));
        answer.setImagePublicId((String) result.get("public_id"));

        if (oldPublicId != null && !oldPublicId.isEmpty()) {
            try {
                if (cloudinaryService.deleteImage(oldPublicId)) {
                    log.info("[AnswerService] Deleted old answer image successfully oldPublicId={}", oldPublicId);
                } else {
                    log.warn("[AnswerService] Deleted old answer image failed oldPublicId={}", oldPublicId);
                }
            } catch (Exception e) {
                log.warn("[AnswerService] Occurred exception when deleting old image answer oldPublicId={}"
                        , oldPublicId, e
                );
            }
        }
    }

    private void validateAnswerCount(List<AnswerCreateRequest> answerCreateRequests) {
        if (answerCreateRequests == null) {
            throw new InvalidAnswerCountException(0);
        }

        if (answerCreateRequests.size() != QuizConstants.VALID_ANSWER_COUNT) {
            throw new InvalidAnswerCountException(answerCreateRequests.size());
        }
    }

    private void validateHavingCorrectAnswer(List<AnswerCreateRequest> answerCreateRequests) {
        long correctAnswerCount = answerCreateRequests.stream().filter(AnswerCreateRequest::correct).count();

        if (correctAnswerCount != QuizConstants.VALID_CORRECT_ANSWER_COUNT) {
            throw new InvalidCorrectAnswerCountException(correctAnswerCount);
        }
    }

    private Answer createAnswer(AnswerCreateRequest answerCreateRequest, Question question) {
        Answer answer = new Answer();

        answer.setQuestion(question);
        answer.setContent(answerCreateRequest.content());
        answer.setCorrect(answerCreateRequest.correct());

        return answer;
    }

    @Transactional
    public void createAnswers(List<AnswerCreateRequest> answerCreateRequests, Question question) {
        validateAnswerCount(answerCreateRequests);

        validateHavingCorrectAnswer(answerCreateRequests);

        for (AnswerCreateRequest answerCreateRequest : answerCreateRequests) {
            Answer answer = createAnswer(answerCreateRequest, question);

            handleAnswerImage(answer, answerCreateRequest.imageUrl());

            Answer savedAnswer = answerRepository.save(answer);

            log.debug(
                    "[AnswerService] Successfully created answer {} for questionId={}",
                    savedAnswer.getId(), question.getId()
            );
        }

        log.info(
                "[AnswerService] Successfully created {} answers for questionId={}",
                answerCreateRequests.size(), question.getId()
        );
    }
}
