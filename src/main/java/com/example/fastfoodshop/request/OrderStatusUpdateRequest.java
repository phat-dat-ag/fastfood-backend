package com.example.fastfoodshop.request;

import com.example.fastfoodshop.enums.OrderStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
        @NotNull(message = "Trạng thái không được để trống")
        OrderStatus status,

        String reason
) {
    @AssertTrue(message = "Không được bỏ trống lý do khi hủy đơn")
    public boolean isValidReason() {
        if (status == null) return true;

        if (status == OrderStatus.CANCELLED) {
            return reason != null && !reason.isBlank();
        }

        return true;
    }
}
