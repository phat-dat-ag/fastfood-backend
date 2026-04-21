package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.OTPCode;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.auth.InvalidOTPCodeException;
import com.example.fastfoodshop.exception.auth.InvalidUserStatusException;
import com.example.fastfoodshop.factory.forget_password.ForgetPasswordRequestFactory;
import com.example.fastfoodshop.factory.forget_password.VerifyForgetPasswordRequestFactory;
import com.example.fastfoodshop.factory.otp.OTPCodeFactory;
import com.example.fastfoodshop.factory.user.UserFactory;
import com.example.fastfoodshop.request.ForgetPasswordRequest;
import com.example.fastfoodshop.request.VerifyForgetPasswordRequest;
import com.example.fastfoodshop.response.auth.OTPResponse;
import com.example.fastfoodshop.response.auth.VerifyResponse;
import com.example.fastfoodshop.service.implementation.PasswordResetServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceImplTest {
    @Mock
    UserService userService;

    @Mock
    OTPCodeService otpCodeService;

    @InjectMocks
    PasswordResetServiceImpl passwordResetService;

    private static final String OTP_CODE = "123789";

    @Test
    void forgetPassword_newOTP_shouldReturnOTPResponse() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        List<OTPCode> otpCodes = OTPCodeFactory.createUnusedAndExpiredOTPCodeList(user);

        when(otpCodeService.getOTPCodeByUserAndIsUsedFalse(user)).thenReturn(otpCodes);

        OTPCode otpCode = OTPCodeFactory.createUnusedOTPCode(user);

        when(otpCodeService.sendOTP(eq(user), anyString(), anyString())).thenReturn(otpCode);

        ForgetPasswordRequest validRequest = ForgetPasswordRequestFactory.createValid(user.getPhone());

        OTPResponse otpResponse = passwordResetService.forgetPassword(validRequest);

        assertNotNull(otpResponse);

        assertEquals(user.getPhone(), otpResponse.phone());
        assertEquals(otpCode.getExpiredAt(), otpResponse.expiredAt());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(otpCodeService).getOTPCodeByUserAndIsUsedFalse(user);
        verify(otpCodeService).sendOTP(eq(user), anyString(), anyString());
    }

    @Test
    void forgetPassword_emptyOTPCodeList_shouldReturnOTPResponse() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        when(otpCodeService.getOTPCodeByUserAndIsUsedFalse(user)).thenReturn(List.of());

        OTPCode otpCode = OTPCodeFactory.createUnusedOTPCode(user);

        when(otpCodeService.sendOTP(eq(user), anyString(), anyString())).thenReturn(otpCode);

        ForgetPasswordRequest validRequest = ForgetPasswordRequestFactory.createValid(user.getPhone());

        OTPResponse otpResponse = passwordResetService.forgetPassword(validRequest);

        assertNotNull(otpResponse);

        assertEquals(user.getPhone(), otpResponse.phone());
        assertEquals(otpCode.getExpiredAt(), otpResponse.expiredAt());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(otpCodeService).getOTPCodeByUserAndIsUsedFalse(user);
        verify(otpCodeService).sendOTP(eq(user), anyString(), anyString());
    }

    @Test
    void forgetPassword_existingOTP_shouldReturnOTPResponse() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        List<OTPCode> otpCodes = OTPCodeFactory.createUnusedOTPCodeList(user);

        when(otpCodeService.getOTPCodeByUserAndIsUsedFalse(user)).thenReturn(otpCodes);

        ForgetPasswordRequest validRequest = ForgetPasswordRequestFactory.createValid(user.getPhone());

        OTPResponse otpResponse = passwordResetService.forgetPassword(validRequest);

        assertNotNull(otpResponse);

        assertEquals(user.getPhone(), otpResponse.phone());
        assertEquals(otpCodes.get(0).getExpiredAt(), otpResponse.expiredAt());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(otpCodeService).getOTPCodeByUserAndIsUsedFalse(user);
    }

    @Test
    void forgetPassword_deactivatedUser_shouldThrowInvalidUserStatusException() {
        User user = UserFactory.createDeletedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        ForgetPasswordRequest validRequest = ForgetPasswordRequestFactory.createValid(user.getPhone());

        assertThrows(
                InvalidUserStatusException.class,
                () -> passwordResetService.forgetPassword(validRequest)
        );

        verify(userService).findUserOrThrow(user.getPhone());
    }

    @Test
    void verifyForgetPasswordOTP_unexpiredAndCorrectOTP_shouldReturnVerifyResponse() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        OTPCode otpCode = OTPCodeFactory.createUnusedWithCode(user, OTP_CODE);

        List<OTPCode> otpCodes = List.of(otpCode);

        when(otpCodeService.getOTPCodeByUserAndIsUsedFalse(user)).thenReturn(otpCodes);

        VerifyForgetPasswordRequest validRequest =
                VerifyForgetPasswordRequestFactory.createValid(user.getPhone(), OTP_CODE);

        doNothing().when(userService).saveUserPassword(user, validRequest.newPassword());

        doNothing().when(otpCodeService).markOTPAsUsed(otpCode);

        VerifyResponse verifyResponse = passwordResetService.verifyForgetPasswordOTP(validRequest);

        assertNotNull(verifyResponse);
        assertNotNull(verifyResponse.message());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(otpCodeService).getOTPCodeByUserAndIsUsedFalse(user);
        verify(userService).saveUserPassword(user, validRequest.newPassword());
        verify(otpCodeService).markOTPAsUsed(otpCode);
    }

    @Test
    void verifyForgetPasswordOTP_withoutExistingOTP_shouldThrowInvalidOTPCodeException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        when(otpCodeService.getOTPCodeByUserAndIsUsedFalse(user)).thenReturn(List.of());

        VerifyForgetPasswordRequest validRequest =
                VerifyForgetPasswordRequestFactory.createValid(user.getPhone(), OTP_CODE);

        assertThrows(
                InvalidOTPCodeException.class,
                () -> passwordResetService.verifyForgetPasswordOTP(validRequest)
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(otpCodeService).getOTPCodeByUserAndIsUsedFalse(user);
    }

    @Test
    void verifyForgetPasswordOTP_expiredOTP_shouldThrowInvalidOTPCodeException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        OTPCode expiredOTP = OTPCodeFactory.createUnusedAndExpiredWithCode(user, OTP_CODE);

        when(otpCodeService.getOTPCodeByUserAndIsUsedFalse(user)).thenReturn(List.of(expiredOTP));

        VerifyForgetPasswordRequest validRequest =
                VerifyForgetPasswordRequestFactory.createValid(user.getPhone(), OTP_CODE);

        assertThrows(
                InvalidOTPCodeException.class,
                () -> passwordResetService.verifyForgetPasswordOTP(validRequest)
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(otpCodeService).getOTPCodeByUserAndIsUsedFalse(user);
    }

    @Test
    void verifyForgetPasswordOTP_incorrectOTP_shouldThrowInvalidOTPCodeException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        String wrongOTP = "987456";

        OTPCode expiredOTP = OTPCodeFactory.createUnusedWithCode(user, wrongOTP);

        when(otpCodeService.getOTPCodeByUserAndIsUsedFalse(user)).thenReturn(List.of(expiredOTP));

        VerifyForgetPasswordRequest validRequest =
                VerifyForgetPasswordRequestFactory.createValid(user.getPhone(), OTP_CODE);

        assertThrows(
                InvalidOTPCodeException.class,
                () -> passwordResetService.verifyForgetPasswordOTP(validRequest)
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(otpCodeService).getOTPCodeByUserAndIsUsedFalse(user);
    }
}