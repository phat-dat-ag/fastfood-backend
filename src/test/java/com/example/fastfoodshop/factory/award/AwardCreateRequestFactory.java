package com.example.fastfoodshop.factory.award;

import com.example.fastfoodshop.enums.PromotionType;
import com.example.fastfoodshop.request.AwardCreateRequest;

public class AwardCreateRequestFactory {
    public static AwardCreateRequest createValid() {
        return new AwardCreateRequest(
                PromotionType.PERCENTAGE,
                3,
                7,
                100,
                1000000,
                300000,
                true
        );
    }
}
