package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.entity.OTPCode;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.auth.InvalidOTPCodeException;
import com.example.fastfoodshop.exception.auth.InvalidUserStatusException;
import com.example.fastfoodshop.request.ForgetPasswordRequest;
import com.example.fastfoodshop.request.VerifyForgetPasswordRequest;
import com.example.fastfoodshop.response.auth.OTPResponse;
import com.example.fastfoodshop.response.auth.VerifyResponse;
import com.example.fastfoodshop.service.OTPCodeService;
import com.example.fastfoodshop.service.PasswordResetService;
import com.example.fastfoodshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {
    private final UserService userService;
    private final OTPCodeService otpCodeService;

    private void validateActiveAccount(User user) {
        if (!user.isActivated()) {
            throw new InvalidUserStatusException("User is not activated");
        }
    }

    private OTPCode getOrCreateValidOTP(User user, List<OTPCode> otpCodes) {
        LocalDateTime now = LocalDateTime.now();

        for (OTPCode otpCode : otpCodes) {
            if (now.isBefore(otpCode.getExpiredAt())) {
                return otpCode;
            }
        }

        return otpCodeService.sendOTP(
                user,
                "Lấy lại mật khẩu",
                "Mã OTP để lấy lại mật khẩu của bạn là: "
        );
    }

    public OTPResponse forgetPassword(ForgetPasswordRequest forgetPasswordRequest) {
        User user = userService.findUserOrThrow(forgetPasswordRequest.phone());

        validateActiveAccount(user);

        List<OTPCode> otpCodes = otpCodeService.getOTPCodeByUserAndIsUsedFalse(user);

        OTPCode otpCode = getOrCreateValidOTP(user, otpCodes);

        return new OTPResponse(user.getPhone(), otpCode.getExpiredAt());
    }

    public VerifyResponse verifyForgetPasswordOTP(VerifyForgetPasswordRequest verifyForgetPasswordRequest) {
        User user = userService.findUserOrThrow(verifyForgetPasswordRequest.phone());

        List<OTPCode> otpCodes = otpCodeService.getOTPCodeByUserAndIsUsedFalse(user);

        LocalDateTime now = LocalDateTime.now();

        for (OTPCode otpCode : otpCodes) {
            boolean isValid = now.isBefore(otpCode.getExpiredAt());

            boolean isCorrect = verifyForgetPasswordRequest.otp().equals(otpCode.getCode());

            if (isValid && isCorrect) {
                userService.saveUserPassword(user, verifyForgetPasswordRequest.newPassword());
                otpCodeService.markOTPAsUsed(otpCode);
                return new VerifyResponse("Xác thực OTP quên mật khẩu thành công");
            }
        }

        throw new InvalidOTPCodeException();
    }
}
