package com.store.authentication.seeder;

import com.store.authentication.enums.MICROSERVICE;
import com.store.authentication.enums.TIRE_CODE;
import com.store.authentication.enums.USER_ROLE;
import com.store.authentication.model.AuthUsers;
import com.store.authentication.model.UserLogs;
import com.store.authentication.repo.UserLogsRepository;
import com.store.authentication.repo.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class seedDataToAuthUsers {

    private final UserRepository authUsersRepository;
    private final UserLogsRepository userLogsRepository;

    @Autowired
    public seedDataToAuthUsers(UserRepository authUsersRepository, UserLogsRepository userLogsRepository) {
        this.authUsersRepository = authUsersRepository;
        this.userLogsRepository = userLogsRepository;
    }

//    @PostConstruct
//    public void seedData() {
//        if (authUsersRepository.count() == 0) {
//            AuthUsers user = new AuthUsers();
//            user.setEmail("master.admin@alphaStore.com");
//            user.setFullName("Master Admin");
//            user.setPassword("password");
//            user.setMobile("0000000000");
//            user.setRole(USER_ROLE.ROLE_ADMIN);
//            user.setTireCode(TIRE_CODE.TIRE0);
//            user.setMicroservice_name(MICROSERVICE.AUTHENTICATION);
//
//            authUsersRepository.save(user);
//
//            UserLogs log = new UserLogs();
//            log.setUser(user);
//            log.setIpAddress("0.0.0.0");
//            log.setDeviceId(UUID.randomUUID().toString());
//            log.setJwtToken("samplejwttoken");
//            log.setDeviceType("MASTER_COMPUTER");
//            log.setOperatingSystem("MASTER_OS");
//            log.setMicroservice_name(MICROSERVICE.AUTHENTICATION);
//
//            userLogsRepository.save(log);
//        }
//    }
}
