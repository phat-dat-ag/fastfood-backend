package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.OTPCode;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.auth.InvalidOTPCodeException;
import com.example.fastfoodshop.exception.otp.SendOTPException;
import com.example.fastfoodshop.factory.otp.OTPCodeFactory;
import com.example.fastfoodshop.factory.user.UserFactory;
import com.example.fastfoodshop.repository.OTPCodeRepository;
import com.example.fastfoodshop.service.implementation.OTPCodeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;

@ExtendWith(MockitoExtension.class)
public class OTPCodeServiceImplTest {
    @Mock
    EmailService emailService;

    @Mock
    OTPCodeRepository otpCodeRepository;

    @InjectMocks
    OTPCodeServiceImpl otpCodeService;

    private static final String CODE = "123321";

    private static final String EMAIL_TITLE = "TITLE";
    private static final String EMAIL_MESSAGE = "MESSAGE";

    @Test
    void sendOTP_shouldReturnOTPCode() {
        User user = UserFactory.createActivatedUser();

        OTPCode otpCode = OTPCodeFactory.createUnusedOTPCode(user);

        when(otpCodeRepository.save(any(OTPCode.class))).thenReturn(otpCode);

        doNothing().when(emailService).sendEmail(eq(user.getEmail()), eq(EMAIL_TITLE), anyString());

        OTPCode otpCodeResponse = otpCodeService.sendOTP(user, EMAIL_TITLE, EMAIL_MESSAGE);

        assertNotNull(otpCodeResponse);
        assertNotNull(otpCodeResponse.getCode());

        assertEquals(6, otpCodeResponse.getCode().length());

        verify(otpCodeRepository).save(any(OTPCode.class));
        verify(emailService).sendEmail(eq(user.getEmail()), eq(EMAIL_TITLE), anyString());
    }

    @Test
    void sendOTP_withException_shouldThrowSendOTPException() {
        User user = UserFactory.createActivatedUser();

        OTPCode otpCode = OTPCodeFactory.createUnusedOTPCode(user);

        when(otpCodeRepository.save(any(OTPCode.class))).thenReturn(otpCode);

        doThrow(new RuntimeException("Mail error"))
                .when(emailService)
                .sendEmail(eq(user.getEmail()), eq(EMAIL_TITLE), anyString());

        assertThrows(
                SendOTPException.class,
                () -> otpCodeService.sendOTP(user, EMAIL_TITLE, EMAIL_MESSAGE)
        );

        verify(otpCodeRepository).save(any(OTPCode.class));
        verify(emailService).sendEmail(eq(user.getEmail()), eq(EMAIL_TITLE), anyString());
    }

    @Test
    void getOTPCodeByUserAndIsUsedFalse_shouldReturnOTPCodeList() {
        User user = UserFactory.createActivatedUser();

        List<OTPCode> otpCodes = OTPCodeFactory.createUnusedOTPCodeList(user);

        when(otpCodeRepository.findByUserAndIsUsedFalse(user)).thenReturn(otpCodes);

        List<OTPCode> otpCodeResponse = otpCodeService.getOTPCodeByUserAndIsUsedFalse(user);

        assertNotNull(otpCodeResponse);

        assertEquals(otpCodes.size(), otpCodeResponse.size());

        verify(otpCodeRepository).findByUserAndIsUsedFalse(user);
    }

    @Test
    void markOTPAsUsed_usedStatus_shouldBeSuccessful() {
        User user = UserFactory.createActivatedUser();

        OTPCode unusedOTP = OTPCodeFactory.createUnusedOTPCode(user);

        when(otpCodeRepository.save(unusedOTP)).thenReturn(unusedOTP);

        otpCodeService.markOTPAsUsed(unusedOTP);

        assertTrue(unusedOTP.isUsed());

        verify(otpCodeRepository).save(unusedOTP);
    }

    @Test
    void findValidOTPOrNull_shouldReturnOTPCode() {
        User user = UserFactory.createActivatedUser();

        List<OTPCode> otpCodes = OTPCodeFactory.createUnusedOTPCodeList(user);

        when(otpCodeRepository.findByUserAndIsUsedFalse(user)).thenReturn(otpCodes);

        OTPCode otpCodeResponse = otpCodeService.findValidOTPOrNull(user);

        assertNotNull(otpCodeResponse);
        assertEquals(otpCodes.get(0).getCode(), otpCodeResponse.getCode());

        verify(otpCodeRepository).findByUserAndIsUsedFalse(user);
    }

    @Test
    void findValidOTPOrNull_emptyOTPCodes_shouldReturnNull() {
        User user = UserFactory.createActivatedUser();

        when(otpCodeRepository.findByUserAndIsUsedFalse(user)).thenReturn(List.of());

        OTPCode otpCodeResponse = otpCodeService.findValidOTPOrNull(user);

        assertNull(otpCodeResponse);

        verify(otpCodeRepository).findByUserAndIsUsedFalse(user);
    }

    @Test
    void findValidOTPOrNull_notFoundValidOTPCode_shouldReturnNull() {
        User user = UserFactory.createActivatedUser();

        List<OTPCode> expiredOTPCodes = OTPCodeFactory.createUnusedAndExpiredOTPCodeList(user);

        when(otpCodeRepository.findByUserAndIsUsedFalse(user)).thenReturn(expiredOTPCodes);

        OTPCode otpCodeResponse = otpCodeService.findValidOTPOrNull(user);

        assertNull(otpCodeResponse);

        verify(otpCodeRepository).findByUserAndIsUsedFalse(user);
    }

    @Test
    void findMatchingValidOTP_shouldReturnOTPCode() {
        User user = UserFactory.createActivatedUser();

        OTPCode otpCode = OTPCodeFactory.createUnusedWithCode(user, CODE);

        List<OTPCode> otpCodes = List.of(otpCode);

        when(otpCodeRepository.findByUserAndIsUsedFalse(user)).thenReturn(otpCodes);

        OTPCode otpCodeResponse = otpCodeService.findMatchingValidOTP(user, CODE);

        assertNotNull(otpCodeResponse);
        assertEquals(otpCodes.get(0).getCode(), otpCodeResponse.getCode());

        verify(otpCodeRepository).findByUserAndIsUsedFalse(user);
    }

    @Test
    void findMatchingValidOTP_emptyOTPCodes_shouldThrowInvalidOTPCodeException() {
        User user = UserFactory.createActivatedUser();

        when(otpCodeRepository.findByUserAndIsUsedFalse(user)).thenReturn(List.of());

        assertThrows(
                InvalidOTPCodeException.class,
                () -> otpCodeService.findMatchingValidOTP(user, CODE)
        );

        verify(otpCodeRepository).findByUserAndIsUsedFalse(user);
    }

    @Test
    void findMatchingValidOTP_notMatching_shouldThrowInvalidOTPCodeException() {
        User user = UserFactory.createActivatedUser();

        List<OTPCode> otpCodes = OTPCodeFactory.createUnusedOTPCodeList(user);

        when(otpCodeRepository.findByUserAndIsUsedFalse(user)).thenReturn(otpCodes);

        assertThrows(
                InvalidOTPCodeException.class,
                () -> otpCodeService.findMatchingValidOTP(user, CODE)
        );

        verify(otpCodeRepository).findByUserAndIsUsedFalse(user);
    }

    @Test
    void findMatchingValidOTP_expiredOTPCode_shouldThrowInvalidOTPCodeException() {
        User user = UserFactory.createActivatedUser();

        OTPCode expiredOTPCode = OTPCodeFactory.createUnusedAndExpiredWithCode(user, CODE);

        List<OTPCode> otpCodes = List.of(expiredOTPCode);

        when(otpCodeRepository.findByUserAndIsUsedFalse(user)).thenReturn(otpCodes);

        assertThrows(
                InvalidOTPCodeException.class,
                () -> otpCodeService.findMatchingValidOTP(user, CODE)
        );

        verify(otpCodeRepository).findByUserAndIsUsedFalse(user);
    }
}