package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Answer;
import com.example.fastfoodshop.entity.Question;
import com.example.fastfoodshop.repository.AnswerRepository;
import com.example.fastfoodshop.request.AnswerCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final CloudinaryService cloudinaryService;
    private final AnswerRepository answerRepository;

    private void handleAnswerImage(Answer answer, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty())
            return;

        String oldPublicId = answer.getImagePublicId();
        Map<?, ?> result = cloudinaryService.uploadImage(imageFile, "answer");

        answer.setImageUrl((String) result.get("secure_url"));
        answer.setImagePublicId((String) result.get("public_id"));

        if (oldPublicId != null && !oldPublicId.isEmpty()) {
            try {
                boolean deleted = cloudinaryService.deleteImage(oldPublicId);
            } catch (Exception e) {
                System.out.println("Ngoai lệ khi dọn ảnh câu trả lời cũ: " + e.getMessage());
            }
        }
    }

    public ArrayList<Answer> createAnswers(List<AnswerCreateRequest> answerCreateRequests, Question question) {
        if (answerCreateRequests == null || answerCreateRequests.size() != 4) {
            throw new IllegalArgumentException("Mỗi câu hỏi phải có đúng 4 đáp án");
        }

        boolean hasCorrect = answerCreateRequests.stream()
                .anyMatch(AnswerCreateRequest::getIsCorrect);

        if (!hasCorrect) {
            throw new IllegalArgumentException("Phải có ít nhất 1 đáp án đúng");
        }

        ArrayList<Answer> answers = new ArrayList<>();

        for (AnswerCreateRequest answerCreateRequest : answerCreateRequests) {
            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setContent(answerCreateRequest.getContent());
            answer.setCorrect(answerCreateRequest.getIsCorrect());
            handleAnswerImage(answer, answerCreateRequest.getImageUrl());

            Answer savedAnswer = answerRepository.save(answer);
            answers.add(savedAnswer);
        }
        return answers;
    }
}
