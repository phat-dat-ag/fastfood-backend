package com.example.fastfoodshop.projection;

public interface UserStatsProjection {
    Long getTotalStaff();

    Long getTotalActivatedStaff();

    Long getStaffJoinedThisMonth();

    Long getTotalUser();

    Long getTotalActivatedUser();

    Long getUserJoinedThisMonth();
}
