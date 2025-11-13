package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.UserStatsDTO;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;

    @GetMapping("/user")
    public ResponseEntity<ResponseWrapper<UserStatsDTO>> getStats() {
        return adminDashboardService.getStats();
    }
}
