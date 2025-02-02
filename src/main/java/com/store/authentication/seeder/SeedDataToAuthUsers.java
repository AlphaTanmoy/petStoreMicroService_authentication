package com.store.authentication.seeder;

import com.store.authentication.enums.INFO_LOG_TYPE;
import com.store.authentication.enums.MICROSERVICE;
import com.store.authentication.enums.TIRE_CODE;
import com.store.authentication.enums.USER_ROLE;
import com.store.authentication.model.AuthUsers;
import com.store.authentication.model.InfoLogger;
import com.store.authentication.model.VerificationCode;
import com.store.authentication.repo.InfoLoggerRepository;
import com.store.authentication.repo.UserRepository;
import com.store.authentication.repo.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SeedDataToAuthUsers {

    private final UserRepository authUsersRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final InfoLoggerRepository infoLoggerRepository;

    @Autowired
    public SeedDataToAuthUsers(
            UserRepository authUsersRepository,
            VerificationCodeRepository verificationCodeRepository,
            InfoLoggerRepository infoLoggerRepository
    ) {
        this.authUsersRepository = authUsersRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.infoLoggerRepository = infoLoggerRepository;
    }

    public void seedAuthMasterData() {
        AuthUsers findUsers = authUsersRepository.findByEmail("master.admin@alphStore.com");
        if (findUsers == null) {
            AuthUsers user = new AuthUsers();
            user.setEmail("master.admin@alphaStore.com");
            user.setFullName("Master");
            user.setPassword("password");
            user.setRole(USER_ROLE.ROLE_MASTER);
            user.setTireCode(TIRE_CODE.TIRE0);
            user.setMicroservice_name(MICROSERVICE.AUTHENTICATION);

            authUsersRepository.save(user);

            List<VerificationCode> existingCode = verificationCodeRepository.findByEmail("master.admin@alphStore.com");
            if(!existingCode.isEmpty()){
                verificationCodeRepository.deleteById(existingCode.get(0).getId());
            }

            VerificationCode code = new VerificationCode();

            code.setOtp("000000");
            code.setEmail("master.admin@alphStore.com");
            code.setUser(user);
            code.setExpiryDate(LocalDateTime.now().plusDays(30));
            verificationCodeRepository.save(code);

            InfoLogger infoLogger = new InfoLogger();
            infoLogger.setMicroservice_name(MICROSERVICE.AUTHENTICATION);
            infoLogger.setType(INFO_LOG_TYPE.SEEDING);
            infoLogger.setMessage("AlphaStore Master User Seeded");
            infoLoggerRepository.save(infoLogger);
        }
    }
}
