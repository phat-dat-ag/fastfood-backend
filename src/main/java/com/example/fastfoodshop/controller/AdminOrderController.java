package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.order.OrderStatsResponse;
import com.example.fastfoodshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController extends BaseController {
    private final OrderService orderService;

    @GetMapping("/stats")
    public ResponseEntity<ResponseWrapper<OrderStatsResponse>> getOrderStats() {
        return okResponse(orderService.getOrderStats());
    }
}
