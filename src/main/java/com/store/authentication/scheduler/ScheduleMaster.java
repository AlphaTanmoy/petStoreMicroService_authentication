package com.store.authentication.scheduler;

import com.store.authentication.model.AuthVerificationCode;
import com.store.authentication.repo.InfoLoggerRepository;
import com.store.authentication.repo.VerificationCodeRepository;
import com.store.authentication.service.ApiKeyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduleMaster{

    private final VerificationCodeRepository verificationCodeRepository;
    private final InfoLoggerRepository infoLoggerRepository;
    private final ApiKeyService apiKeyService;

    public ScheduleMaster(
            VerificationCodeRepository verificationCodeRepository,
            InfoLoggerRepository infoLoggerRepository,
            ApiKeyService apiKeyService
    ) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.infoLoggerRepository = infoLoggerRepository;
        this.apiKeyService = apiKeyService;
    }

    @Scheduled(cron = "0 * * * * ?") // every 1 mint
    public void deleteExpiredOtp() {
        LocalDateTime now = LocalDateTime.now();
        List<AuthVerificationCode> expiredOtpList = verificationCodeRepository.findByExpiryDateBefore(now);
        for (AuthVerificationCode authVerificationCode : expiredOtpList) {
            verificationCodeRepository.delete(authVerificationCode);
        }
        System.out.println("Found >> "+expiredOtpList.size()+" expired OTPs");
    }

    @Scheduled(cron = "0 0 0 * * ?")  // This runs every day at midnight
    public void remove24HoursLog() {
        infoLoggerRepository.deleteAll();
        apiKeyService.deleteApiKeyIfExpired();
    }
}
