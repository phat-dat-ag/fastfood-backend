package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.PaymentDTO;
import com.example.fastfoodshop.request.DeliveryRequest;
import com.example.fastfoodshop.response.CartResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final CartService cartService;

    public ResponseEntity<ResponseWrapper<PaymentDTO>> createPaymentIntent(String phone, String promotionCode, DeliveryRequest deliveryRequest) {
        try {
            CartResponse cartResponse = cartService.getCartResponse(phone, promotionCode, deliveryRequest);
            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount((long) cartResponse.getTotalPrice())
                            .setCurrency("vnd")
                            .setDescription("Thanh toán đơn hàng")
                            .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            PaymentDTO paymentDTO = new PaymentDTO(paymentIntent.getClientSecret());
            return ResponseEntity.ok(ResponseWrapper.success(paymentDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "CREATE_PAYMENT_INTENT_FAILED",
                            "Lỗi khi tạo thanh toán trực tuyến " + e.getMessage()
                    )
            );
        }
    }
}

