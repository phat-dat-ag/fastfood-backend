package com.example.fastfoodshop.request;

import com.example.fastfoodshop.enums.PromotionType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AwardCreateRequest {
    @NotNull(message = "Loại khuyến mãi không được để trống")
    private PromotionType type;

    @NotNull(message = "Giá trị khuyến mãi nhỏ nhất không được để trống")
    @Min(value = 1, message = "Giá trị khuyến mãi nhỏ nhất phải lớn hơn 1")
    private Integer minValue;

    @NotNull(message = "Giá trị khuyến mãi lớn nhất không được để trống")
    @Min(value = 1, message = "Giá trị khuyến mãi lớn nhất phải lớn hơn 1")
    private Integer maxValue;

    @NotNull(message = "Số lượng khuyến mãi không được để trống")
    @Min(value = 1, message = "Số lượng khuyến mãi phải lớn hơn 1")
    private Integer quantity;

    @NotNull(message = "Giá trị giảm tối đa không được để trống")
    @Min(value = 1, message = "Giá trị giảm tối đa phải lớn hơn 1")
    private Integer maxDiscountAmount;

    @NotNull(message = "Giá trị chi tiêu ít nhất để được khuyến mãi không được để trống")
    @Min(value = 1, message = "Giá trị chi tiêu ít nhất để được khuyến mãi phải lớn hơn 1")
    private Integer minSpendAmount;

    @NotNull(message = "Không được để trống trạng thái của phần thưởng")
    private Boolean isActivated;

    @AssertTrue(message = "Giá trị khuyến mãi không hợp lệ với loại khuyến mãi đã chọn")
    public Boolean isValueValidByType() {
        if (type == null) return true;

        if (type == PromotionType.FIXED_AMOUNT) {
            return minValue != null && minValue % 10000 == 0
                    && maxValue != null && maxValue % 10000 == 0
                    && minValue < maxValue;
        }

        if (type == PromotionType.PERCENTAGE) {
            return minValue != null && minValue >= 1 && minValue <= 100
                    && maxValue != null && maxValue >= 1 && maxValue <= 100
                    && minValue < maxValue;
        }

        return true;
    }

    @AssertTrue(message = "Giá trị giảm tối đa phải chia hết cho 1000")
    public boolean isMaxDiscountAmountValid() {
        return maxDiscountAmount == null || maxDiscountAmount % 1000 == 0;
    }

    @AssertTrue(message = "Giá trị chi tiêu ít nhất để được khuyến mãi phải chia hết cho 1000")
    public boolean isMinSpendAmountValid() {
        return minSpendAmount == null || minSpendAmount % 1000 == 0;
    }
}
