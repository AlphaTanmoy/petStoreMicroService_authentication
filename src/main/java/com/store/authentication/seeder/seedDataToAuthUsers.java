package com.store.authentication.seeder;

import com.store.authentication.enums.MICROSERVICE;
import com.store.authentication.enums.TIRE_CODE;
import com.store.authentication.enums.USER_ROLE;
import com.store.authentication.model.AuthUsers;
import com.store.authentication.model.UserLogs;
import com.store.authentication.model.VerificationCode;
import com.store.authentication.repo.UserLogsRepository;
import com.store.authentication.repo.UserRepository;
import com.store.authentication.repo.VerificationCodeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class seedDataToAuthUsers {

    private final UserRepository authUsersRepository;
    private final VerificationCodeRepository verificationCodeRepository;

    @Autowired
    public seedDataToAuthUsers(UserRepository authUsersRepository, VerificationCodeRepository verificationCodeRepository) {
        this.authUsersRepository = authUsersRepository;
        this.verificationCodeRepository = verificationCodeRepository;
    }

    @PostConstruct
    public void seedData() {
        AuthUsers findUsers = authUsersRepository.findByEmail("master.admin@alphStore.com");
        if (findUsers.getId().isEmpty()) {
            AuthUsers user = new AuthUsers();
            user.setEmail("master.admin@alphaStore.com");
            user.setFullName("Master");
            user.setPassword("password");
            user.setMobile("0000000000");
            user.setRole(USER_ROLE.ROLE_ADMIN);
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
        }
    }
}
