package com.store.authentication.scheduler;

import com.store.authentication.model.VerificationCode;
import com.store.authentication.repo.InfoLoggerRepository;
import com.store.authentication.repo.VerificationCodeRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduleMaster{

    private final VerificationCodeRepository verificationCodeRepository;
    private final InfoLoggerRepository infoLoggerRepository;

    public ScheduleMaster(VerificationCodeRepository verificationCodeRepository, InfoLoggerRepository infoLoggerRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.infoLoggerRepository = infoLoggerRepository;
    }

    @Scheduled(cron = "0 * * * * ?") // every 1 mint
    public void deleteExpiredOtp() {
        LocalDateTime now = LocalDateTime.now();
        List<VerificationCode> expiredOtpList = verificationCodeRepository.findByExpiryDateBefore(now);
        for (VerificationCode verificationCode : expiredOtpList) {
            verificationCodeRepository.delete(verificationCode);
        }
        System.out.println("Found >> "+expiredOtpList.size()+" expired OTPs");
    }

    @Scheduled(cron = "0 0 0 * * ?")  // This runs every day at midnight
    public void remove24HoursLog() {
        infoLoggerRepository.deleteAll();
    }
}
