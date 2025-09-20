package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.OTPCode;
import com.example.fastfoodshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OTPCodeRepository extends JpaRepository<OTPCode, Long> {
    List<OTPCode> findByUserAndIsUsedFalse(User user);
}
