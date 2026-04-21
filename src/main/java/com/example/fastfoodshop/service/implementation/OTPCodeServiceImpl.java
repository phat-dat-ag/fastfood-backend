package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.entity.OTPCode;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.auth.InvalidOTPCodeException;
import com.example.fastfoodshop.exception.otp.SendOTPException;
import com.example.fastfoodshop.repository.OTPCodeRepository;
import com.example.fastfoodshop.service.EmailService;
import com.example.fastfoodshop.service.OTPCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OTPCodeServiceImpl implements OTPCodeService {
    private final OTPCodeRepository otpCodeRepository;
    private final EmailService emailService;

    public List<OTPCode> getOTPCodeByUserAndIsUsedFalse(User user) {
        log.info(
                "[OTPCode Service]: Retrieved all unused OTP code for user phone={}",
                user.getPhone()
        );
        return otpCodeRepository.findByUserAndIsUsedFalse(user);
    }

    private String generateOTP() {
        int number = (int) (Math.random() * 900000) + 100000;

        log.debug("[OTPCode Service]: Generated OTP code: {}", number);

        return String.valueOf(number);
    }

    private OTPCode buildOTPCode(User user) {
        String otp_code = generateOTP();

        OTPCode otp = new OTPCode();
        otp.setUser(user);
        otp.setCode(otp_code);
        otp.setExpiredAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);

        return otp;
    }

    public OTPCode sendOTP(User user, String emailTitle, String emailMessage) {
        OTPCode otp = buildOTPCode(user);

        otpCodeRepository.save(otp);

        try {
            emailService.sendEmail(
                    user.getEmail(),
                    emailTitle,
                    emailMessage + otp.getCode()
            );

            log.info(
                    "[OTPCode Service]: Successfully sent OTP: {} to user phone={}, email={}",
                    otp, user.getPhone(), user.getEmail()
            );

            return otp;
        } catch (Exception e) {
            otpCodeRepository.delete(otp);

            log.error("[OTPCode Service]: Sent OTP with exception {}", e.getMessage());

            throw new SendOTPException(e.getMessage());
        }
    }

    public void markOTPAsUsed(OTPCode otpCode) {
        otpCode.setUsed(true);
        otpCodeRepository.save(otpCode);

        log.info("[OTPCode Service]: Successfully set OTP id={} to be used", otpCode.getId());
    }

    public OTPCode findValidOTPOrNull(User user) {
        LocalDateTime now = LocalDateTime.now();

        log.info("[OTPCode Service]: Retrieved valid OTP code or null");

        return getOTPCodeByUserAndIsUsedFalse(user)
                .stream()
                .filter(otp -> now.isBefore(otp.getExpiredAt()))
                .findFirst()
                .orElse(null);
    }

    public OTPCode findMatchingValidOTP(User user, String otp) {
        LocalDateTime now = LocalDateTime.now();

        log.info("[OTPCode Service]: Found matching valid OTP");

        return getOTPCodeByUserAndIsUsedFalse(user)
                .stream()
                .filter(code ->
                        now.isBefore(code.getExpiredAt()) &&
                                code.getCode().equals(otp)
                )
                .findFirst()
                .orElseThrow(InvalidOTPCodeException::new);
    }
}