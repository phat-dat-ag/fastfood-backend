package com.example.fastfoodshop.dto;

public record UserStatsDTO(
        Long totalStaff,
        Long totalActivatedStaff,
        Long totalUser,
        Long totalActivatedUser,
        Long staffJoinedThisMonth,
        Long userJoinedThisMonth
) {
}
