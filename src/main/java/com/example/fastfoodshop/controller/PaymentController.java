package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.PaymentDTO;
import com.example.fastfoodshop.request.DeliveryRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-payment-intent")
    public ResponseEntity<ResponseWrapper<PaymentDTO>> createPaymentIntent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("code") String code,
            @RequestBody(required = false) DeliveryRequest deliveryRequest) {
        return paymentService.createPaymentIntent(userDetails.getUsername(), code, deliveryRequest);
    }
}
