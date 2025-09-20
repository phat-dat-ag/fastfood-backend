package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.OTPCode;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.repository.OTPCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OTPCodeService {
    private final OTPCodeRepository otpCodeRepository;
    private final EmailService emailService;

    public List<OTPCode> getOTPCodeByUserAndIsUsedFalse(User user) {
        return otpCodeRepository.findByUserAndIsUsedFalse(user);
    }

    //    create and send new otp code
    public OTPCode sendOTP(User user, String emailTitle, String emailMessage) {
        String otp_code = generateOTP();

        OTPCode otp = new OTPCode();
        otp.setUser(user);
        otp.setCode(otp_code);
        otp.setExpiredAt(LocalDateTime.now().plusMinutes(1));
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
//            delete otp_code if sending fails
            otpCodeRepository.delete(otp);
            System.out.println("Lỗi gửi mail: " + e);
            throw new RuntimeException("Lỗi không gửi được OTP");
        }
    }

    public OTPCode updateOTPCode(OTPCode otpCode, boolean isUsed) {
        otpCode.setUsed(isUsed);
        return otpCodeRepository.save(otpCode);
    }

    //    generate otp code with 6 digits
    public String generateOTP() {
        int number = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(number);
    }

}
