package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.CategoryStatsDTO;
import com.example.fastfoodshop.dto.OrderStatsDTO;
import com.example.fastfoodshop.dto.ProductStatsDTO;
import com.example.fastfoodshop.dto.UserStatsDTO;
import com.example.fastfoodshop.projection.CategoryStatsProjection;
import com.example.fastfoodshop.projection.OrderStatsProjection;
import com.example.fastfoodshop.projection.ProductStatsProjection;
import com.example.fastfoodshop.projection.UserStatsProjection;
import com.example.fastfoodshop.repository.CategoryRepository;
import com.example.fastfoodshop.repository.OrderRepository;
import com.example.fastfoodshop.repository.ProductRepository;
import com.example.fastfoodshop.repository.UserRepository;
import com.example.fastfoodshop.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public ResponseEntity<ResponseWrapper<UserStatsDTO>> getUserStats() {
        try {
            UserStatsProjection statsProjection = userRepository.getStats();
            UserStatsDTO statsDTO = new UserStatsDTO(
                    statsProjection.getTotalStaff(), statsProjection.getTotalActivatedStaff(), statsProjection.getStaffJoinedThisMonth(),
                    statsProjection.getTotalUser(), statsProjection.getTotalActivatedUser(), statsProjection.getUserJoinedThisMonth()
            );
            return ResponseEntity.ok(ResponseWrapper.success(statsDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_USER_STATS_FAILED",
                    "Lỗi lấy dữ liệu thống kê các tài khoản " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<OrderStatsDTO>> getOrderStats() {
        try {
            OrderStatsProjection statsProjection = orderRepository.getStats();
            OrderStatsDTO statsDTO = new OrderStatsDTO(
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
            return ResponseEntity.ok(ResponseWrapper.success(statsDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ORDER_STATS_FAILED",
                    "Lỗi lấy dữ liệu thống kê các đơn hàng " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<List<CategoryStatsDTO>>> getCategoryStats() {
        try {
            List<CategoryStatsProjection> statsProjections = categoryRepository.getStats();
            List<CategoryStatsDTO> categoryStatsDTOs = new ArrayList<>();
            for (CategoryStatsProjection statsProjection : statsProjections) {
                categoryStatsDTOs.add(new CategoryStatsDTO(
                        statsProjection.getName(),
                        statsProjection.getTotalRevenue(),
                        statsProjection.getTotalQuantitySold()
                ));
            }
            return ResponseEntity.ok(ResponseWrapper.success(categoryStatsDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_CATEGORY_STATS_FAILED",
                    "Lỗi lấy dữ liệu thống kê các danh mục " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<List<ProductStatsDTO>>> getProductStats() {
        try {
            List<ProductStatsProjection> statsProjections = productRepository.getStats();
            List<ProductStatsDTO> productStatsDTOs = new ArrayList<>();
            for (ProductStatsProjection statsProjection : statsProjections) {
                productStatsDTOs.add(new ProductStatsDTO(
                        statsProjection.getName(),
                        statsProjection.getTotalRevenue(),
                        statsProjection.getTotalQuantitySold()
                ));
            }
            return ResponseEntity.ok(ResponseWrapper.success(productStatsDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_PRODUCT_STATS_FAILED",
                    "Lỗi lấy dữ liệu thống kê các sản phẩm " + e.getMessage()
            ));
        }
    }
}
