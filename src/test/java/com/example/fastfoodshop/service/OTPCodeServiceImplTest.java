package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.OTPCode;
import com.example.fastfoodshop.entity.User;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OTPCodeServiceImplTest {
    @Mock
    OTPCodeRepository otpCodeRepository;

    @InjectMocks
    OTPCodeServiceImpl otpCodeService;

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
}