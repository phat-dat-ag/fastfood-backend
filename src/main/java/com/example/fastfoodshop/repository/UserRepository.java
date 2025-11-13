package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.UserRole;
import com.example.fastfoodshop.projection.UserStatsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);

    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    Page<User> findByRoleAndIsDeletedFalse(UserRole role, Pageable pageable);

    @Query(value = """
            SELECT
            	COUNT(CASE WHEN role = 'STAFF' THEN 1 END) as total_staff,
                COUNT(CASE WHEN role = 'STAFF' AND is_activated = true THEN 1 END) as total_activated_staff,
                COUNT(CASE WHEN role = 'USER' THEN 1 END) as total_user,
                COUNT(CASE WHEN role = 'USER' AND is_activated = true THEN 1 END) as total_activated_user,
                COUNT(CASE
                        WHEN role = 'STAFF'
                             AND is_activated = true
                             AND created_at >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
                        THEN 1
                     END) AS staff_joined_this_month,
                COUNT(CASE
                        WHEN role = 'USER'
                             AND is_activated = true
                             AND created_at >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
                        THEN 1
                     END) AS user_joined_this_month
            FROM users
            WHERE is_deleted = false;
            """, nativeQuery = true)
    UserStatsProjection getStats();
}
