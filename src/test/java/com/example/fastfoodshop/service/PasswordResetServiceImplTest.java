package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.OTPCode;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.auth.InvalidUserStatusException;
import com.example.fastfoodshop.factory.forget_password.ForgetPasswordRequestFactory;
import com.example.fastfoodshop.factory.otp.OTPCodeFactory;
import com.example.fastfoodshop.factory.user.UserFactory;
import com.example.fastfoodshop.request.ForgetPasswordRequest;
import com.example.fastfoodshop.response.auth.OTPResponse;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceImplTest {
    @Mock
    UserService userService;

    @Mock
    OTPCodeService otpCodeService;

    @InjectMocks
    PasswordResetServiceImpl passwordResetService;

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
}