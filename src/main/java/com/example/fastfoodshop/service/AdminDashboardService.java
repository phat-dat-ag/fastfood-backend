package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.CategoryStatsDTO;
import com.example.fastfoodshop.dto.OrderStatsDTO;
import com.example.fastfoodshop.dto.ProductStatsDTO;
import com.example.fastfoodshop.dto.UserStatsDTO;
import com.example.fastfoodshop.dto.TopicStatsDTO;
import com.example.fastfoodshop.dto.TopicDifficultyStatsDTO;
import com.example.fastfoodshop.response.ResponseWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdminDashboardService {
    ResponseEntity<ResponseWrapper<UserStatsDTO>> getUserStats();

    ResponseEntity<ResponseWrapper<OrderStatsDTO>> getOrderStats();

    ResponseEntity<ResponseWrapper<List<CategoryStatsDTO>>> getCategoryStats();

    ResponseEntity<ResponseWrapper<List<ProductStatsDTO>>> getProductStats();

    ResponseEntity<ResponseWrapper<List<TopicStatsDTO>>> getTopicStats();

    ResponseEntity<ResponseWrapper<List<TopicDifficultyStatsDTO>>> getTopicDifficultyStats();
}
