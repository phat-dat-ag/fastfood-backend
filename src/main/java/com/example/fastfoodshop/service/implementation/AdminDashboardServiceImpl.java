package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.CategoryStatsDTO;
import com.example.fastfoodshop.dto.ProductStatsDTO;
import com.example.fastfoodshop.dto.TopicStatsDTO;
import com.example.fastfoodshop.dto.TopicDifficultyStatsDTO;
import com.example.fastfoodshop.dto.OrderStatsDTO;
import com.example.fastfoodshop.dto.UserStatsDTO;
import com.example.fastfoodshop.projection.UserStatsProjection;
import com.example.fastfoodshop.projection.OrderStatsProjection;
import com.example.fastfoodshop.projection.CategoryStatsProjection;
import com.example.fastfoodshop.projection.ProductStatsProjection;
import com.example.fastfoodshop.projection.TopicStatsProjection;
import com.example.fastfoodshop.projection.TopicDifficultyStatsProjection;
import com.example.fastfoodshop.repository.CategoryRepository;
import com.example.fastfoodshop.repository.OrderRepository;
import com.example.fastfoodshop.repository.ProductRepository;
import com.example.fastfoodshop.repository.UserRepository;
import com.example.fastfoodshop.repository.TopicRepository;
import com.example.fastfoodshop.repository.TopicDifficultyRepository;
import com.example.fastfoodshop.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final TopicRepository topicRepository;
    private final TopicDifficultyRepository topicDifficultyRepository;

    public UserStatsDTO getUserStats() {
        UserStatsProjection statsProjection = userRepository.getStats();
        return new UserStatsDTO(
                statsProjection.getTotalStaff(), statsProjection.getTotalActivatedStaff(), statsProjection.getStaffJoinedThisMonth(),
                statsProjection.getTotalUser(), statsProjection.getTotalActivatedUser(), statsProjection.getUserJoinedThisMonth()
        );
    }

    public OrderStatsDTO getOrderStats() {
        OrderStatsProjection statsProjection = orderRepository.getStats();
        return new OrderStatsDTO(
                statsProjection.getPendingOrderAmount(),
                statsProjection.getConfirmedOrderAmount(),
                statsProjection.getDeliveringOrderAmount(),
                statsProjection.getDeliveredOrderAmount(),
                statsProjection.getCancelledOrderAmount(),
                statsProjection.getCashOnDeliveryOrderAmount(),
                statsProjection.getBankTransferOrderAmount(),
                statsProjection.getDiscountedOrderAmount(),
                statsProjection.getCashOnDeliveryRevenue(),
                statsProjection.getBankTransferRevenue(),
                statsProjection.getTotalRevenue()
        );
    }

    public List<CategoryStatsDTO> getCategoryStats() {
        List<CategoryStatsProjection> statsProjections = categoryRepository.getStats();
        List<CategoryStatsDTO> categoryStatsDTOs = new ArrayList<>();
        for (CategoryStatsProjection statsProjection : statsProjections) {
            categoryStatsDTOs.add(new CategoryStatsDTO(
                    statsProjection.getName(),
                    statsProjection.getTotalRevenue(),
                    statsProjection.getTotalQuantitySold()
            ));
        }
        return categoryStatsDTOs;
    }

    public List<ProductStatsDTO> getProductStats() {
        List<ProductStatsProjection> statsProjections = productRepository.getStats();
        List<ProductStatsDTO> productStatsDTOs = new ArrayList<>();
        for (ProductStatsProjection statsProjection : statsProjections) {
            productStatsDTOs.add(new ProductStatsDTO(
                    statsProjection.getName(),
                    statsProjection.getTotalRevenue(),
                    statsProjection.getTotalQuantitySold()
            ));
        }
        return productStatsDTOs;
    }

    public List<TopicStatsDTO> getTopicStats() {
        List<TopicStatsProjection> statsProjections = topicRepository.getStats();
        List<TopicStatsDTO> topicStatsDTOs = new ArrayList<>();
        for (TopicStatsProjection statsProjection : statsProjections) {
            topicStatsDTOs.add(new TopicStatsDTO(statsProjection));
        }
        return topicStatsDTOs;
    }

    public List<TopicDifficultyStatsDTO> getTopicDifficultyStats() {
        List<TopicDifficultyStatsProjection> statsProjections = topicDifficultyRepository.getStats();
        List<TopicDifficultyStatsDTO> topicDifficultyStatsDTOs = new ArrayList<>();
        for (TopicDifficultyStatsProjection statsProjection : statsProjections) {
            topicDifficultyStatsDTOs.add(new TopicDifficultyStatsDTO(statsProjection));
        }
        return topicDifficultyStatsDTOs;
    }
}
