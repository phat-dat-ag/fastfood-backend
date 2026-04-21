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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OTPCodeServiceImplTest {
    @Mock
    OTPCodeRepository otpCodeRepository;

    @InjectMocks
    OTPCodeServiceImpl otpCodeService;

    @Test
    void markOTPAsUsed_usedStatus_shouldBeSuccessful() {
        User user = UserFactory.createActivatedUser();

        OTPCode unusedOTP = OTPCodeFactory.createUnusedOTPCode(user);

        when(otpCodeRepository.save(unusedOTP)).thenReturn(unusedOTP);

        otpCodeService.markOTPAsUsed(unusedOTP);

        assertTrue(unusedOTP.isUsed());

        verify(otpCodeRepository).save(unusedOTP);
    }
}