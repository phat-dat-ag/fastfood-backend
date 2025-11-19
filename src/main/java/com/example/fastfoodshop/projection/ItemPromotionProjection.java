package com.example.fastfoodshop.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ItemPromotionProjection {
    String getName();

    String getImageUrl();

    String getType();

    BigDecimal getValue();

    String getCode();

    LocalDateTime getStartAt();

    LocalDateTime getEndAt();
}
