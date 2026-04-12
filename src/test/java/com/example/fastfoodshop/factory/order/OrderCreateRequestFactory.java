package com.example.fastfoodshop.factory.order;

import com.example.fastfoodshop.enums.PaymentMethod;
import com.example.fastfoodshop.request.OrderCreateRequest;

public class OrderCreateRequestFactory {
    private static final String ORDER_NOTE = "HELLO MAY CUNG NHA";

    private static final String BLANK_ORDER_NOTE = "               ";

    public static OrderCreateRequest createCODOrderRequest(Long addressId) {
        return new OrderCreateRequest(
                PaymentMethod.CASH_ON_DELIVERY,
                null,
                null,
                addressId
        );
    }

    public static OrderCreateRequest createCODOrderRequestWithNote(Long addressId) {
        return new OrderCreateRequest(
                PaymentMethod.CASH_ON_DELIVERY,
                ORDER_NOTE,
                null,
                addressId
        );
    }

    public static OrderCreateRequest createCODOrderRequestWithBlankNote(Long addressId) {
        return new OrderCreateRequest(
                PaymentMethod.CASH_ON_DELIVERY,
                BLANK_ORDER_NOTE,
                null,
                addressId
        );
    }

    public static OrderCreateRequest createOnlineOrderRequest(Long addressId) {
        return new OrderCreateRequest(
                PaymentMethod.BANK_TRANSFER,
                null,
                null,
                addressId
        );
    }
}