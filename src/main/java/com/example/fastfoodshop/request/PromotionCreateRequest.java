package com.example.fastfoodshop.request;

import com.example.fastfoodshop.enums.PromotionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PromotionCreateRequest {
    private Long categoryId;
    private Long productId;
    private Long userId;

    @NotNull(message = "Loại khuyến mãi không được để trống")
    private PromotionType type;

    @NotNull(message = "Không được để trống giá trị khuyến mãi")
    @Min(value = 1, message = "Giá trị khuyến mãi từ 1 trở lên")
    private Integer value;

    @NotNull(message = "Không được để trống ngày bắt đầu khuyến mãi")
    private LocalDateTime startAt;

    @NotNull(message = "Không được để trống ngày kết thúc khuyến mãi")
    private LocalDateTime endAt;

    @NotNull(message = "Không được để trống số lượng mã khuyến mãi")
    @Min(value = 1, message = "Số lượng mã khuyến mãi phải từ 1 trở lên")
    private int quantity;

    @NotNull(message = "Không được để trống số tiền khuyến mãi tối đa")
    @Min(value = 1, message = "Số tiền khuyến mãi tối đa từ 1 trở lên")
    private Integer maxDiscountAmount;

    @NotNull(message = "Không được để trống số tiền ít nhất để được khuyến mãi")
    @Min(value = 0, message = "Số tiền ít nhất để được khuyến mãi phải từ 0 trở lên")
    private Integer minSpendAmount;

    @NotNull(message = "Không được để trống phạm vi mã khuyến mãi")
    private Boolean isGlobal;

    @NotNull(message = "Không được để trống trạng thái mã khuyến mãi")
    private Boolean isActivated;

    @NotBlank(message = "Mã khuyến mãi không được để trống")
    @Size(min = 2, max = 40, message = "Mã khuyến mãi dài từ 2 đến 40 ký tự")
    private String code;

    @AssertTrue(message = "Ngày bắt đầu khuyến mãi phải từ ngày mai trở đi")
    public boolean isStartAtValid() {
        if (startAt == null) return true;
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime tomorrowStart = today.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return !startAt.isBefore(tomorrowStart);
    }

    @AssertTrue(message = "Ngày kết thúc phải sau ngày bắt đầu")
    public boolean isEndAfterStart() {
        if (startAt == null || endAt == null) return true;
        return endAt.isAfter(startAt);
    }

    @AssertTrue(message = "Giá trị khuyến mãi không hợp lệ với loại khuyến mãi đã chọn")
    public boolean isValueValidByType() {
        if (type == null) return true;

        if (type == PromotionType.FIXED_AMOUNT) {
            return value != null && value % 10000 == 0;
        }

        if (type == PromotionType.PERCENTAGE) {
            return value != null && value >= 1 && value <= 100;
        }

        return true;
    }
}
