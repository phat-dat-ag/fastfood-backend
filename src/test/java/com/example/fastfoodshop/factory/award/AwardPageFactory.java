package com.example.fastfoodshop.factory.award;

import com.example.fastfoodshop.entity.Award;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public class AwardPageFactory {
    public static Page<Award> createAwardPage() {
        return new PageImpl<>(
                AwardFactory.createAvailableAwards()
        );
    }
}
