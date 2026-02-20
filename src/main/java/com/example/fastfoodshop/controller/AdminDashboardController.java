package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.UserStatsDTO;
import com.example.fastfoodshop.dto.OrderStatsDTO;
import com.example.fastfoodshop.dto.CategoryStatsDTO;
import com.example.fastfoodshop.dto.ProductStatsDTO;
import com.example.fastfoodshop.dto.TopicStatsDTO;
import com.example.fastfoodshop.dto.TopicDifficultyStatsDTO;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;

    @GetMapping("/user")
    public ResponseEntity<ResponseWrapper<UserStatsDTO>> getStats() {
        return adminDashboardService.getUserStats();
    }

    @GetMapping("/order")
    public ResponseEntity<ResponseWrapper<OrderStatsDTO>> getOrderStats() {
        return adminDashboardService.getOrderStats();
    }

    @GetMapping("/category")
    public ResponseEntity<ResponseWrapper<List<CategoryStatsDTO>>> getCategoryStats() {
        return adminDashboardService.getCategoryStats();
    }

    @GetMapping("/product")
    public ResponseEntity<ResponseWrapper<List<ProductStatsDTO>>> getProductStats() {
        return adminDashboardService.getProductStats();
    }

    @GetMapping("/topic")
    public ResponseEntity<ResponseWrapper<List<TopicStatsDTO>>> getTopicStats() {
        return adminDashboardService.getTopicStats();
    }

    @GetMapping("/topic-difficulty")
    public ResponseEntity<ResponseWrapper<List<TopicDifficultyStatsDTO>>> getTopicDifficultyStats() {
        return adminDashboardService.getTopicDifficultyStats();
    }
}
