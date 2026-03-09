package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.entity.OTPCode;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.otp.SendOTPException;
import com.example.fastfoodshop.repository.OTPCodeRepository;
import com.example.fastfoodshop.service.EmailService;
import com.example.fastfoodshop.service.OTPCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OTPCodeServiceImpl implements OTPCodeService {
    private final OTPCodeRepository otpCodeRepository;
    private final EmailService emailService;

    public List<OTPCode> getOTPCodeByUserAndIsUsedFalse(User user) {
        return otpCodeRepository.findByUserAndIsUsedFalse(user);
    }

    private String generateOTP() {
        int number = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(number);
    }

    public OTPCode sendOTP(User user, String emailTitle, String emailMessage) {
        String otp_code = generateOTP();

        OTPCode otp = new OTPCode();
        otp.setUser(user);
        otp.setCode(otp_code);
        otp.setExpiredAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        otpCodeRepository.save(otp);

        try {
            emailService.sendEmail(
                    user.getEmail(),
                    emailTitle,
                    emailMessage + otp_code
            );
            return otp;
        } catch (Exception e) {
            otpCodeRepository.delete(otp);
            throw new SendOTPException(e.getMessage());
        }
    }

    public void updateOTPCode(OTPCode otpCode, boolean isUsed) {
        otpCode.setUsed(isUsed);
        otpCodeRepository.save(otpCode);
    }
}

