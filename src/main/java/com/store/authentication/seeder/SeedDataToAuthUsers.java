package com.store.authentication.seeder;

import com.store.authentication.enums.INFO_LOG_TYPE;
import com.store.authentication.enums.MICROSERVICE;
import com.store.authentication.enums.TIRE_CODE;
import com.store.authentication.enums.USER_ROLE;
import com.store.authentication.model.AuthUsers;
import com.store.authentication.model.InfoLogger;
import com.store.authentication.model.AuthVerificationCode;
import com.store.authentication.repo.InfoLoggerRepository;
import com.store.authentication.repo.UserRepository;
import com.store.authentication.repo.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Component
public class SeedDataToAuthUsers {

    private final UserRepository authUsersRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final InfoLoggerRepository infoLoggerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SeedDataToAuthUsers(
            UserRepository authUsersRepository,
            VerificationCodeRepository verificationCodeRepository,
            InfoLoggerRepository infoLoggerRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.authUsersRepository = authUsersRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.infoLoggerRepository = infoLoggerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void seedAuthMasterData() {
        AuthUsers findUsers = authUsersRepository.findByEmail("tanmoy.projects.preview@gmail.com");
        if (findUsers == null) {
            AuthUsers user = new AuthUsers();
            user.setEmail("tanmoy.projects.preview@gmail.com");
            user.setFullName("Master");
            user.setPassword(passwordEncoder.encode("000000"));
            user.setRole(USER_ROLE.ROLE_MASTER);
            user.setTireCode(TIRE_CODE.TIRE0);
            user.setMicroservice_name(MICROSERVICE.AUTHENTICATION);

            authUsersRepository.save(user);

            List<AuthVerificationCode> existingCode = verificationCodeRepository.findByEmail("tanmoy.projects.preview@gmail.com");
            if(!existingCode.isEmpty()){
                verificationCodeRepository.deleteById(existingCode.get(0).getId());
            }

            AuthVerificationCode code = new AuthVerificationCode();

            code.setOtp("000000");
            code.setEmail("tanmoy.projects.preview@gmail.com");
            code.setUser(user);
            code.setExpiryDate(ZonedDateTime.now().plusDays(30));
            verificationCodeRepository.save(code);

            InfoLogger infoLogger = new InfoLogger();
            infoLogger.setMicroservice_name(MICROSERVICE.AUTHENTICATION);
            infoLogger.setType(INFO_LOG_TYPE.SEEDING);
            infoLogger.setMessage("AlphaStore Master User Seeded");
            infoLoggerRepository.save(infoLogger);
        }
    }
}
