package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);

    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    List<User> findByRoleAndIsDeletedFalse(UserRole role);
}
