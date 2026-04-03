package com.example.fastfoodshop.factory.award;

import com.example.fastfoodshop.entity.Award;
import com.example.fastfoodshop.enums.PromotionType;

import java.time.Instant;
import java.util.List;

public class AwardFactory {
    private static Award createAward() {
        Award award = new Award();

        award.setType(PromotionType.PERCENTAGE);
        award.setMinValue(20);
        award.setMaxValue(80);
        award.setMaxDiscountAmount(30000);
        award.setMinSpendAmount(100000);
        award.setUsedQuantity(0);
        award.setQuantity(100);

        award.setCreatedAt(Instant.now());
        award.setUpdatedAt(Instant.now());

        return award;
    }

    public static Award createActivatedAward(Long awardId) {
        Award award = createAward();

        award.setId(awardId);
        award.setActivated(true);
        award.setDeleted(false);

        return award;
    }

    public static Award createDeactivatedAward(Long awardId) {
        Award award = createAward();

        award.setId(awardId);
        award.setActivated(false);
        award.setDeleted(false);

        return award;
    }

    public static Award createDeletedAward(Long awardId) {
        Award award = createAward();

        award.setId(awardId);
        award.setActivated(false);
        award.setDeleted(true);

        return award;
    }

    public static List<Award> createAvailableAwards() {
        return List.of(
                createActivatedAward(1L),
                createActivatedAward(2L),
                createActivatedAward(3L)
        );
    }
}
