package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.entity.OTPCode;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.auth.PhoneAlreadyExistsException;
import com.example.fastfoodshop.exception.auth.InvalidOTPCodeException;
import com.example.fastfoodshop.exception.auth.InvalidPasswordException;
import com.example.fastfoodshop.exception.auth.InvalidUserStatusException;
import com.example.fastfoodshop.request.*;
import com.example.fastfoodshop.response.auth.OTPResponse;
import com.example.fastfoodshop.response.auth.SignInResponse;
import com.example.fastfoodshop.response.auth.VerifyResponse;
import com.example.fastfoodshop.security.JwtUtil;
import com.example.fastfoodshop.service.AuthService;
import com.example.fastfoodshop.service.OTPCodeService;
import com.example.fastfoodshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final OTPCodeService otpCodeService;

    public OTPResponse signUp(SignUpRequest signUpRequest) {
        Optional<User> optionalUser = userService.getUserByPhone(signUpRequest.getPhone());

        if (optionalUser.isPresent() && optionalUser.get().isActivated()) {
            throw new PhoneAlreadyExistsException(signUpRequest.getPhone());
        }

        if (optionalUser.isPresent()) {
            User dbUser = optionalUser.get();
            User updatedUser = userService.updateUser(dbUser, signUpRequest);

            List<OTPCode> otpCodes = otpCodeService.getOTPCodeByUserAndIsUsedFalse(updatedUser);
            LocalDateTime now = LocalDateTime.now();
            for (OTPCode otpCode : otpCodes) {
                if (now.isBefore(otpCode.getExpiredAt())) {
                    return new OTPResponse(updatedUser.getPhone(), otpCode.getExpiredAt());
                }
            }
            OTPCode otp = otpCodeService.sendOTP(
                    updatedUser,
                    "Đăng ký tài khoản mới",
                    "Mã OTP đăng ký tài khoản mới của bạn là: "
            );
            return new OTPResponse(updatedUser.getPhone(), otp.getExpiredAt());
        }

        User user = userService.createUser(signUpRequest);
        OTPCode otp = otpCodeService.sendOTP(
                user,
                "Đăng ký tài khoản mới",
                "Mã OTP đăng ký tài khoản mới của bạn là: "
        );
        return new OTPResponse(user.getPhone(), otp.getExpiredAt());
    }

    public VerifyResponse verifySignUpOTP(VerifySignUpRequest verifySignUpRequest) {
        User user = userService.findUserOrThrow(verifySignUpRequest.getPhone());
        List<OTPCode> otpCodes = otpCodeService.getOTPCodeByUserAndIsUsedFalse(user);
        LocalDateTime now = LocalDateTime.now();

        for (OTPCode otpCode : otpCodes) {
            if (now.isBefore(otpCode.getExpiredAt()) && verifySignUpRequest.getOtp().equals(otpCode.getCode())) {
                user.setActivated(true);
                userService.updateUser(user);
                otpCodeService.updateOTPCode(otpCode, true);
                return new VerifyResponse("Xác thực OTP đăng ký thành công");
            }
        }
        throw new InvalidOTPCodeException();
    }

    public SignInResponse signIn(SignInRequest signInRequest) {
        User dbUser = userService.findUserOrThrow(signInRequest.getPhone());

        if (dbUser.isDeleted() || !dbUser.isActivated()) {
            throw new InvalidUserStatusException();
        }

        if (!passwordEncoder.matches(signInRequest.getPassword(), dbUser.getPasswordHash())) {
            throw new InvalidPasswordException();
        }

        String token = jwtUtil.generateToken(dbUser);
        return new SignInResponse(token, UserDTO.from(dbUser));
    }

    public OTPResponse forgetPassword(ForgetPasswordRequest forgetPasswordRequest) {
        User user = userService.findUserOrThrow(forgetPasswordRequest.phone());
        if (!user.isActivated()) {
            throw new InvalidUserStatusException();
        }

        List<OTPCode> otpCodes = otpCodeService.getOTPCodeByUserAndIsUsedFalse(user);
        LocalDateTime now = LocalDateTime.now();
        for (OTPCode otpCode : otpCodes) {
            if (now.isBefore(otpCode.getExpiredAt())) {
                return new OTPResponse(user.getPhone(), otpCode.getExpiredAt());
            }
        }
        OTPCode otpCode = otpCodeService.sendOTP(
                user,
                "Lấy lại mật khẩu",
                "Mã OTP để lấy lại mật khẩu của bạn là: "
        );
        return new OTPResponse(user.getPhone(), otpCode.getExpiredAt());
    }

    public VerifyResponse verifyForgetPasswordOTP(VerifyForgetPasswordRequest verifyForgetPasswordRequest) {

        User user = userService.findUserOrThrow(verifyForgetPasswordRequest.getPhone());
        List<OTPCode> otpCodes = otpCodeService.getOTPCodeByUserAndIsUsedFalse(user);
        LocalDateTime now = LocalDateTime.now();
        for (OTPCode otpCode : otpCodes) {
            if (now.isBefore(otpCode.getExpiredAt()) && verifyForgetPasswordRequest.getOtp().equals(otpCode.getCode())) {
                userService.updateUser(user, verifyForgetPasswordRequest.getNewPassword());
                otpCodeService.updateOTPCode(otpCode, true);
                return new VerifyResponse("Xác thực OTP quên mật khẩu thành công");
            }
        }
        throw new InvalidOTPCodeException();
    }

    public SignInResponse verify(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String phone = userDetails.getUsername();
        User user = userService.findUserOrThrow(phone);
        String newToken = jwtUtil.generateToken(user);
        return new SignInResponse(newToken, UserDTO.from(user));
    }
}
