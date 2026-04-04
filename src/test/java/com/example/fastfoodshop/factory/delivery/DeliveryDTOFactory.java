package com.example.fastfoodshop.factory.delivery;

import com.example.fastfoodshop.dto.DeliveryDTO;

public class DeliveryDTOFactory {
    private static final Double DISTANCE_KM = 3.6D;
    private static final int DURATION_MINUTES = 8;
    private static final int FEE = 17000;

    private static final String REJECTED_MESSAGE = "Vui lòng cung cấp địa chỉ giao hàng";

    public static DeliveryDTO createAcceptedDelivery() {
        return DeliveryDTO.accept(DISTANCE_KM, DURATION_MINUTES, FEE);
    }

    public static DeliveryDTO createRejectedDelivery() {
        return DeliveryDTO.reject(REJECTED_MESSAGE);
    }
}
