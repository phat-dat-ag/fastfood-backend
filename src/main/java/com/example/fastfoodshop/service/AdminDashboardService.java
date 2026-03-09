package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.CategoryStatsDTO;
import com.example.fastfoodshop.dto.OrderStatsDTO;
import com.example.fastfoodshop.dto.ProductStatsDTO;
import com.example.fastfoodshop.dto.UserStatsDTO;
import com.example.fastfoodshop.dto.TopicStatsDTO;
import com.example.fastfoodshop.dto.TopicDifficultyStatsDTO;

import java.util.List;

public interface AdminDashboardService {
    UserStatsDTO getUserStats();

    OrderStatsDTO getOrderStats();

    List<CategoryStatsDTO> getCategoryStats();

    List<ProductStatsDTO> getProductStats();

    List<TopicStatsDTO> getTopicStats();

    List<TopicDifficultyStatsDTO> getTopicDifficultyStats();
}
