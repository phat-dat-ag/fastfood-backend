package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.UserStatsDTO;
import com.example.fastfoodshop.projection.UserStatsProjection;
import com.example.fastfoodshop.repository.UserRepository;
import com.example.fastfoodshop.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {
    private final UserRepository userRepository;

    public ResponseEntity<ResponseWrapper<UserStatsDTO>> getStats() {
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
}
