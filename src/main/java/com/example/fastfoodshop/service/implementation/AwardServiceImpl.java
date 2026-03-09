package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.AwardDTO;
import com.example.fastfoodshop.entity.Award;
import com.example.fastfoodshop.entity.TopicDifficulty;
import com.example.fastfoodshop.exception.award.AwardNotFoundException;
import com.example.fastfoodshop.exception.award.DeletedAwardException;
import com.example.fastfoodshop.repository.AwardRepository;
import com.example.fastfoodshop.request.AwardCreateRequest;
import com.example.fastfoodshop.request.AwardGetByTopicDifficultyRequest;
import com.example.fastfoodshop.response.AwardResponse;
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
        throw new AwardNotFoundException(awardId);
    }

    private Award findActivatedAward(Long awardId) {
        return awardRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(awardId).orElseThrow(
                () -> new AwardNotFoundException(awardId)
        );
    }

    private Award findDeactivatedAward(Long awardId) {
        return awardRepository.findByIdAndIsDeletedFalseAndIsActivatedFalse(awardId).orElseThrow(
                () -> new AwardNotFoundException(awardId)
        );
    }

    private void buildAward(Award award, AwardCreateRequest request) {
        award.setType(request.getType());
        award.setMinValue(request.getMinValue());
        award.setMaxValue(request.getMaxValue());
        award.setUsedQuantity(0);
        award.setQuantity(request.getQuantity());
        award.setMaxDiscountAmount(request.getMaxDiscountAmount());
        award.setMinSpendAmount(request.getMinSpendAmount());
        award.setActivated(request.getIsActivated());
        award.setDeleted(false);
    }

    public Award getRandomAwardByTopicDifficulty(TopicDifficulty topicDifficulty) {
        List<Award> availableAwards = awardRepository.findAvailableByTopicDifficulty(topicDifficulty.getId());

        if (availableAwards.isEmpty()) {
            Optional<Award> optionalAward = awardRepository.findAnyAvailableAwardAsFallback(topicDifficulty.getId());
            return optionalAward.orElseThrow(AwardNotFoundException::new);
        }

        int index = NumberUtils.randomNumber(0, availableAwards.size() - 1);
        return availableAwards.get(index);
    }

    public AwardDTO createAward(String topicDifficultySlug, AwardCreateRequest request) {
        TopicDifficulty topicDifficulty = topicDifficultyService.findValidTopicDifficultyOrThrow(topicDifficultySlug);
        Award award = new Award();
        award.setTopicDifficulty(topicDifficulty);
        buildAward(award, request);

        Award savedAward = awardRepository.save(award);
        return new AwardDTO(savedAward);
    }

    public AwardResponse getAllAwardsByTopicDifficulty(AwardGetByTopicDifficultyRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        TopicDifficulty topicDifficulty = topicDifficultyService.findValidTopicDifficultyOrThrow(request.getTopicDifficultySlug());
        Page<Award> awardPage = awardRepository.findByTopicDifficultyAndIsDeletedFalse(topicDifficulty, pageable);

        return new AwardResponse(awardPage);
    }

    public String activateAward(Long awardId) {
        Award award = findDeactivatedAward(awardId);
        award.setActivated(true);
        awardRepository.save(award);

        return "Kích hoạt phần thưởng thành công";
    }

    public String deactivateAward(Long awardId) {
        Award award = findActivatedAward(awardId);
        award.setActivated(false);
        awardRepository.save(award);

        return "Hủy kích hoạt phần thưởng thành công";
    }

    public AwardDTO deleteAward(Long awardId) {
        Award award = findAwardOrThrow(awardId);
        if (award.isDeleted()) {
            throw new DeletedAwardException();
        }
        award.setDeleted(true);

        Award deletedAward = awardRepository.save(award);
        return new AwardDTO(deletedAward);
    }
}

