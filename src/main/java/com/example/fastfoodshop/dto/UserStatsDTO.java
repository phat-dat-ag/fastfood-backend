package com.example.fastfoodshop.dto;

import lombok.Data;

@Data
public class UserStatsDTO {
    private Long totalStaff;
    private Long totalActivatedStaff;
    private Long totalUser;
    private Long totalActivatedUser;
    private Long staffJoinedThisMonth;
    private Long userJoinedThisMonth;

    public UserStatsDTO(
            Long totalStaff, Long totalActivatedStaff, Long staffJoinedThisMonth,
            Long totalUser, Long totalActivatedUser, Long userJoinedThisMonth
    ) {
        this.totalStaff = totalStaff;
        this.totalActivatedStaff = totalActivatedStaff;
        this.totalUser = totalUser;
        this.totalActivatedUser = totalActivatedUser;
        this.staffJoinedThisMonth = staffJoinedThisMonth;
        this.userJoinedThisMonth = userJoinedThisMonth;
    }
}
