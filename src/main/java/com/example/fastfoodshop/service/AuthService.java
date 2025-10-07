package com.example.fastfoodshop.service;

import com.example.fastfoodshop.response.SignInResponse;
import com.example.fastfoodshop.response.OTPResponse;
import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.entity.OTPCode;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final OTPCodeService otpCodeService;

    public ResponseEntity<ResponseWrapper<OTPResponse>> signUp(String name, String phone, String email, String rawPassword, String birthdayString) {
        Optional<User> optionalUser = userService.getUserByPhone(phone);

//        account has already existed
        if (optionalUser.isPresent() && optionalUser.get().isActivated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseWrapper.error("AUTH_INVALID_PHONE", "Số điện thoại đã được đăng ký"));
        }

//        phone is registered but not successful
        if (optionalUser.isPresent()) {
            User dbUser = optionalUser.get();

            User updatedUser = userService.updateUser(dbUser, name, phone, email, rawPassword, birthdayString);

            List<OTPCode> otpCodes = otpCodeService.getOTPCodeByUserAndIsUsedFalse(updatedUser);
            for (OTPCode otpCode : otpCodes) {
                LocalDateTime now = LocalDateTime.now();
                if (now.isBefore(otpCode.getExpiredAt())) {
//                    otp is not expired
                    return ResponseEntity.ok(ResponseWrapper.success(new OTPResponse(updatedUser.getPhone(), otpCode.getExpiredAt())));
                }
            }
//            resend otp
            OTPCode otp = otpCodeService.sendOTP(
                    updatedUser,
                    "Đăng ký tài khoản mới",
                    "Mã OTP đăng ký tài khoản mới của bạn là: "
            );
            return ResponseEntity.ok(ResponseWrapper.success(new OTPResponse(dbUser.getPhone(), otp.getExpiredAt())));
        }
//        phone hasn't been used before
        User user = userService.createUser(name, phone, email, rawPassword, birthdayString);
        OTPCode otp = otpCodeService.sendOTP(
                user,
                "Đăng ký tài khoản mới",
                "Mã OTP đăng ký tài khoản mới của bạn là: "
        );
        return ResponseEntity.ok(ResponseWrapper.success(new OTPResponse(user.getPhone(), otp.getExpiredAt())));
    }

    public ResponseEntity<ResponseWrapper<UserDTO>> verifySignUpOTP(String phone, String code) {
        Optional<User> optionalUser = userService.getUserByPhone(phone);

        if (optionalUser.isEmpty())
            return ResponseEntity.badRequest().body(ResponseWrapper.error("AUTH_INVALID_ACCOUNT", "Không tìm thấy tài khoản"));

        User user = optionalUser.get();
        List<OTPCode> otpCodes = otpCodeService.getOTPCodeByUserAndIsUsedFalse(user);
        LocalDateTime now = LocalDateTime.now();

        for (OTPCode otpCode : otpCodes) {
            if (now.isBefore(otpCode.getExpiredAt()) && code.equals(otpCode.getCode())) {
                user.setActivated(true);
                User dbUser = userService.updateUser(user);
                OTPCode dbOtpCode = otpCodeService.updateOTPCode(otpCode, true);
                return ResponseEntity.ok(ResponseWrapper.success(new UserDTO(dbUser)));
            }
        }

        return ResponseEntity.badRequest().body(ResponseWrapper.error("AUTH_INVALID_OTP", "Mã OTP không hợp lệ"));
    }

    public ResponseEntity<ResponseWrapper<SignInResponse>> signIn(String phone, String password) {
        Optional<User> optionalUser = userService.getUserByPhone(phone);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error("AUTH_INVALID_ACCOUNT", "Không tìm thấy tài khoản"));
        }

        User dbUser = optionalUser.get();
        if (!dbUser.isActivated()) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error("AUTH_INVALID_ACCOUNT", "Tài khoản chưa được kích hoạt"));
        }

        if (!passwordEncoder.matches(password, dbUser.getPasswordHash())) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error("AUTH_INVALID_PASSWORD", "Mật khẩu chưa chính xác"));
        }

        String token = jwtUtil.generateToken(dbUser);
        SignInResponse data = new SignInResponse(token, dbUser);
        return ResponseEntity.ok(ResponseWrapper.success(data));
    }

    public ResponseEntity<ResponseWrapper<OTPResponse>> forgetPassword(String phone, String newPassword) {
        Optional<User> optionalUser = userService.getUserByPhone(phone);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error("AUTH_INVALID_ACCOUNT", "Không tìm thấy tài khoản"));
        }

        User user = optionalUser.get();
        if (!user.isActivated()) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error("AUTH_INVALID_ACCOUNT", "Tài khoản chưa được kích hoạt"));
        }

        List<OTPCode> otpCodes = otpCodeService.getOTPCodeByUserAndIsUsedFalse(user);
        for (OTPCode otpCode : otpCodes) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(otpCode.getExpiredAt())) {
//                otp is not expired
                return ResponseEntity.ok(ResponseWrapper.success(new OTPResponse(user.getPhone(), otpCode.getExpiredAt())));
            }
        }
//        resend otp
        OTPCode otpCode = otpCodeService.sendOTP(
                user,
                "Lấy lại mật khẩu",
                "Mã OTP để lấy lại mật khẩu của bạn là: "
        );
        OTPResponse response = new OTPResponse(user.getPhone(), otpCode.getExpiredAt());
        return ResponseEntity.ok(ResponseWrapper.success(response));
    }

    public ResponseEntity<ResponseWrapper<UserDTO>> verifyForgetPasswordOTP(String phone, String code, String newPassword) {
        Optional<User> optionalUser = userService.getUserByPhone(phone);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error("AUTH_INVALID_ACCOUNT", "Không tìm thấy tài khoản"));
        }

        User user = optionalUser.get();
        List<OTPCode> otpCodes = otpCodeService.getOTPCodeByUserAndIsUsedFalse(user);
        LocalDateTime now = LocalDateTime.now();
        for (OTPCode otpCode : otpCodes) {
            if (now.isBefore(otpCode.getExpiredAt()) && code.equals(otpCode.getCode())) {
                User dbUser = userService.updateUser(user, newPassword);
                OTPCode dbOtpCode = otpCodeService.updateOTPCode(otpCode, true);
                return ResponseEntity.ok(ResponseWrapper.success(new UserDTO(dbUser)));
            }
        }
        return ResponseEntity.badRequest().body(ResponseWrapper.error("AUTH_INVALID_OTP", "Mã OTP không hợp lệ"));
    }

    public ResponseEntity<ResponseWrapper<SignInResponse>> verify(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String phone = userDetails.getUsername();
        Optional<User> optionalUser = userService.getUserByPhone(phone);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error("AUTH_INVALID_ACCOUNT", "Không tìm thấy tài khoản"));
        }
        String newToken = jwtUtil.generateToken(optionalUser.get());
        return ResponseEntity.ok(ResponseWrapper.success(new SignInResponse(newToken, optionalUser.get())));
    }
}
