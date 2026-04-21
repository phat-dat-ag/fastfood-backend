package com.example.fastfoodshop.factory.otp;

import com.example.fastfoodshop.entity.OTPCode;
import com.example.fastfoodshop.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public class OTPCodeFactory {
    private static final Long OTP_CODE_ID = 1789L;
    private static final String CODE = "898276";
    private static final int OTP_CODE_DURATION_MINUTES = 5;

    public static OTPCode createUnusedOTPCode(User user) {
        OTPCode otpCode = new OTPCode();

        otpCode.setUser(user);
        otpCode.setId(OTP_CODE_ID);
        otpCode.setCode(CODE);
        otpCode.setUsed(false);
        otpCode.setExpiredAt(LocalDateTime.now().plusMinutes(OTP_CODE_DURATION_MINUTES));

        return otpCode;
    }

    public static List<OTPCode> createUnusedOTPCodeList(User user) {
        return List.of(
                createUnusedOTPCode(user)
        );
    }

    private static OTPCode createUnusedAndExpiredOTPCode(User user) {
        OTPCode otpCode = createUnusedOTPCode(user);

        otpCode.setExpiredAt(LocalDateTime.now().minusMinutes(OTP_CODE_DURATION_MINUTES));

        return otpCode;
    }

    public static List<OTPCode> createUnusedAndExpiredOTPCodeList(User user) {
        return List.of(
                createUnusedAndExpiredOTPCode(user)
        );
    }

    public static OTPCode createUnusedWithCode(User user, String code) {
        OTPCode otpCode = createUnusedOTPCode(user);

        otpCode.setCode(code);

        return otpCode;
    }

    public static OTPCode createUnusedAndExpiredWithCode(User user, String code) {
        OTPCode otpCode = createUnusedOTPCode(user);

        otpCode.setCode(code);
        otpCode.setExpiredAt(LocalDateTime.now().minusMinutes(OTP_CODE_DURATION_MINUTES));

        return otpCode;
    }
}