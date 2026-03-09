package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.OTPCode;
import com.example.fastfoodshop.entity.User;

import java.util.List;

public interface OTPCodeService {
    List<OTPCode> getOTPCodeByUserAndIsUsedFalse(User user);

    OTPCode sendOTP(User user, String emailTitle, String emailMessage);

    void updateOTPCode(OTPCode otpCode, boolean isUsed);
}
