package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.entity.Award;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.exception.award.AwardNotFoundException;
import com.example.fastfoodshop.exception.award.DeletedAwardException;
import com.example.fastfoodshop.exception.award.InvalidAwardStatusException;
import com.example.fastfoodshop.repository.AwardRepository;
import com.example.fastfoodshop.request.AwardCreateRequest;
import com.example.fastfoodshop.response.award.AwardPageResponse;
import com.example.fastfoodshop.response.award.AwardUpdateResponse;
import com.example.fastfoodshop.service.AwardService;
import com.example.fastfoodshop.service.TopicDifficultyService;
import com.example.fastfoodshop.util.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AwardServiceImpl implements AwardService {
    private final TopicDifficultyService topicDifficultyService;
    private final AwardRepository awardRepository;

    private Award findAwardOrThrow(Long awardId) {
        return awardRepository.findById(awardId).orElseThrow(
                () -> new AwardNotFoundException(awardId)
        );
    }

    private void buildAward(Award award, AwardCreateRequest request) {
        award.setType(request.type());
        award.setMinValue(request.minValue());
        award.setMaxValue(request.maxValue());
        award.setUsedQuantity(0);
        award.setQuantity(request.quantity());
        award.setMaxDiscountAmount(request.maxDiscountAmount());
        award.setMinSpendAmount(request.minSpendAmount());
        award.setActivated(request.activated());
        award.setDeleted(false);
    }

    public Award getRandomAwardByTopicDifficulty(TopicDifficulty topicDifficulty) {
        List<Award> availableAwards = awardRepository.findAvailableByTopicDifficulty(topicDifficulty.getId());

        if (availableAwards.isEmpty()) {
            Optional<Award> optionalAward = awardRepository.findAnyAvailableAwardAsFallback(topicDifficulty.getId());

            if (optionalAward.isEmpty()) {
                throw new AwardNotFoundException();
            }

            return optionalAward.get();
        }

        int index = NumberUtils.randomNumber(0, availableAwards.size() - 1);
        Award award = availableAwards.get(index);

        return award;
    }

    public AwardUpdateResponse createAward(String topicDifficultySlug, AwardCreateRequest awardCreateRequest) {
        TopicDifficulty topicDifficulty = topicDifficultyService
                .findValidTopicDifficultyOrThrow(topicDifficultySlug);

        Award award = new Award();
        award.setTopicDifficulty(topicDifficulty);
        buildAward(award, awardCreateRequest);

        Award savedAward = awardRepository.save(award);
        return new AwardUpdateResponse("Thêm phần thưởng thành công: " + savedAward.getId());
    }

    public AwardPageResponse getAllAwardsByTopicDifficulty(String topicDifficultySlug, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        TopicDifficulty topicDifficulty = topicDifficultyService
                .findValidTopicDifficultyOrThrow(topicDifficultySlug);

        Page<Award> awardPage = awardRepository
                .findByTopicDifficultyAndIsDeletedFalse(topicDifficulty, pageable);

        return AwardPageResponse.from(awardPage);
    }

    public AwardUpdateResponse updateAwardActivation(Long awardId, boolean activated) {
        Award award = findAwardOrThrow(awardId);
        if (award.isActivated() == activated) {
            throw new InvalidAwardStatusException(awardId);
        }

        award.setActivated(activated);
        awardRepository.save(award);

        String message = activated ? "Kích hoạt phần thưởng thành công: " + awardId
                : "Hủy kích hoạt phần thưởng thành công: " + awardId;

        return new AwardUpdateResponse(message);
    }

    public AwardUpdateResponse deleteAward(Long awardId) {
        Award award = findAwardOrThrow(awardId);
        if (award.isDeleted()) {
            throw new DeletedAwardException();
        }
        award.setDeleted(true);

        awardRepository.save(award);
        return new AwardUpdateResponse("Xóa phần thưởng thành công: " + awardId);
    }
}