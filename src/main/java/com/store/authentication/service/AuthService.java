package com.store.authentication.service;

import com.store.authentication.config.JwtProvider;
import com.store.authentication.config.KeywordsAndConstants;
import com.store.authentication.enums.INFO_LOG_TYPE;
import com.store.authentication.enums.MICROSERVICE;
import com.store.authentication.enums.TIRE_CODE;
import com.store.authentication.enums.USER_ROLE;
import com.store.authentication.error.BadRequestException;
import com.store.authentication.model.InfoLogger;
import com.store.authentication.model.AuthUsers;
import com.store.authentication.model.UserLogs;
import com.store.authentication.model.AuthVerificationCode;
import com.store.authentication.repo.*;
import com.store.authentication.request.LoginRequest;
import com.store.authentication.request.SignUpRequest;
import com.store.authentication.response.AuthResponse;
import com.store.authentication.response.GetDeviceDetails;
import com.store.authentication.utils.DeviceUtils;
import com.store.authentication.utils.GenerateUUID;
import com.store.authentication.utils.OtpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.store.authentication.config.KeywordsAndConstants.*;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserLogsRepository userLogsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final CustomUserServiceImplementation customUserDetails;
    private final InfoLoggerRepository infoLoggerRepository;
    private final JWTBlackListRepository jwtBlackListRepository;
    private final DeviceUtils deviceUtils;

    public void sentLoginOtp(String email) throws BadRequestException {

        long userCount = userRepository.countUserByEmail(email);
        if (userCount > 0) {
            AuthUsers findConfirmedUser = userRepository.findByEmail(email);
            Long jwtBlackListCount = jwtBlackListRepository.findByUserId(findConfirmedUser.getId());
            if (jwtBlackListCount > 0) throw new BadRequestException(
                    findConfirmedUser.getFullName() + ", You are blackListed. Contact Support For Remove As BlackList" + findConfirmedUser.getRole() + "!"
            );
        }

        if (email.startsWith(KeywordsAndConstants.SIGNING_PREFIX)) {
            email = email.substring(KeywordsAndConstants.SIGNING_PREFIX.length());
            AuthUsers user = userRepository.findByEmail(email);
            if (user == null) throw new BadRequestException("User not found with this email!");
        }

        List<AuthVerificationCode> isExist = verificationCodeRepository
                .findByEmail(email);

        if (!isExist.isEmpty()) {
            verificationCodeRepository.delete(isExist.get(0));
        }

        String otp = OtpUtils.generateOTP();
        String message = "";
        if (userCount == 0) message = "OTP Generated for Sign Up Request";
        else message = "OTP Generated for Login Request";

        AuthVerificationCode authVerificationCode = new AuthVerificationCode();
        authVerificationCode.setOtp(otp);
        authVerificationCode.setEmail(email);
        verificationCodeRepository.save(authVerificationCode);

        String subject = KeywordsAndConstants.OTP_SUBJECT_FOR_LOGIN;
        String text = KeywordsAndConstants.OTP_TEXT_FOR_LOGIN;
        InfoLogger infoLogger = new InfoLogger();
        infoLogger.setType(INFO_LOG_TYPE.OTP);
        infoLogger.setMicroservice_name(MICROSERVICE.AUTHENTICATION);
        infoLogger.setMessage(message + " ");
        infoLoggerRepository.save(infoLogger);
        emailService.sendVerificationOtpEmail(email, otp, subject, text);
    }

    @Transactional
    public String createUser(SignUpRequest req, HttpServletRequest httpRequest) throws BadRequestException {

        USER_ROLE finalUserRole = USER_ROLE.ROLE_MASTER;
        TIRE_CODE finalTireCode = TIRE_CODE.TIRE0;
        if (Objects.equals(req.getMicroServiceName(), MICROSERVICE.ADMIN.name())) {
            finalUserRole = USER_ROLE.ROLE_ADMIN;
            finalTireCode = TIRE_CODE.TIRE1;
        }
        if (Objects.equals(req.getMicroServiceName(), MICROSERVICE.SELLER.name())) {
            finalUserRole = USER_ROLE.ROLE_SELLER;
            finalTireCode = TIRE_CODE.TIRE2;
        }
        if (Objects.equals(req.getMicroServiceName(), MICROSERVICE.CHAT.name())) {
            finalUserRole = USER_ROLE.ROLE_CUSTOMER_CARE;
            finalTireCode = TIRE_CODE.TIRE3;
        }
        if (Objects.equals(req.getMicroServiceName(), MICROSERVICE.DELIVERY.name())) {
            finalUserRole = USER_ROLE.ROLE_DELIVERY_BOY;
            finalTireCode = TIRE_CODE.TIRE3;
        }
        if (Objects.equals(req.getMicroServiceName(), MICROSERVICE.USER.name())) {
            finalUserRole = USER_ROLE.ROLE_CUSTOMER;
            finalTireCode = TIRE_CODE.TIRE4;
        }

        AuthUsers findConfirmedUser = userRepository.findByEmail(req.getEmail());
        if (findConfirmedUser != null && !findConfirmedUser.getId().isEmpty()) return null;

        if(findConfirmedUser != null){
            Long jwtBlackListCount = jwtBlackListRepository.findByUserId(findConfirmedUser.getId());
            if (jwtBlackListCount > 0) throw new BadRequestException(
                    findConfirmedUser.getFullName() + ", You are blackListed. Contact Support For Remove As BlackList" + findConfirmedUser.getRole() + "!"
            );
        }

        List<AuthVerificationCode> authVerificationCode = verificationCodeRepository.findByEmail(req.getEmail());

        if(Objects.equals(req.getMicroServiceName(), MICROSERVICE.AUTHENTICATION.name())){
            if (authVerificationCode == null || !authVerificationCode.get(0).getOtp().equals(req.getOtp())) {
                throw new BadRequestException("Wrong Otp");
            }
        }

        AuthUsers createdUser = new AuthUsers();
        createdUser.setFullName(req.getFullName());
        createdUser.setEmail(req.getEmail());
        createdUser.setRole(finalUserRole);
        createdUser.setPassword(passwordEncoder.encode(req.getOtp()));
        createdUser.setTireCode(finalTireCode);
        createdUser.setMicroservice_name(MICROSERVICE.valueOf(req.getMicroServiceName()));
        userRepository.save(createdUser);

        String jwtToken = jwtProvider.generateToken(createdUser.getId(), createdUser.getEmail(), createdUser.getRole());

        String ipAddress = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = httpRequest.getRemoteAddr();
        }

        if(Objects.equals(req.getMicroServiceName(), MICROSERVICE.AUTHENTICATION.name())) {
            authVerificationCode.get(0).setUser(createdUser);
            verificationCodeRepository.save(authVerificationCode.get(0));
        }
        else {
            String microServiceName = req.getMicroServiceName();
            String baseUrl = "";
            if(Objects.equals(microServiceName, MICROSERVICE.ADMIN.name())) baseUrl = ADMIN_MICROSERVICE_BASE_URL_LOC;
            if(Objects.equals(microServiceName, MICROSERVICE.CHAT.name())) baseUrl = CHAT_MICROSERVICE_BASE_URL_LOC;
            if(Objects.equals(microServiceName, MICROSERVICE.CORE.name())) baseUrl = CORE_MICROSERVICE_BASE_URL_LOC;
            if(Objects.equals(microServiceName, MICROSERVICE.PAYMENT.name())) baseUrl = PAYMENT_MICROSERVICE_BASE_URL_LOC;
            if(Objects.equals(microServiceName, MICROSERVICE.SELLER.name())) baseUrl = SELLER_MICROSERVICE_BASE_URL_LOC;
            if(Objects.equals(microServiceName, MICROSERVICE.USER.name())) baseUrl = USER_MICROSERVICE_BASE_URL_LOC;

            try{
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                String constructURL = baseUrl+"/saveOtp/"+req.getEmail();
                HttpEntity<String> response = new HttpEntity<>(null, headers);
                ResponseEntity<String> responseFromMicroservice = restTemplate.postForEntity(constructURL, response, String.class);
                if (responseFromMicroservice.getStatusCode() != HttpStatus.OK) {
                    throw new BadRequestException("Failed to create user in "+microServiceName+" Microservice");
                }
            } catch (Exception e){
                throw new BadRequestException("Failed to Successfully Store Otp on "+microServiceName);
            }
        }

        GetDeviceDetails deviceDetails = deviceUtils.findDeviceDetails(httpRequest.getHeader("User-Agent"));

        UserLogs userLogs = new UserLogs();
        userLogs.setUser(createdUser);
        userLogs.setIpAddress(ipAddress);
        userLogs.setJwtToken(jwtToken);
        userLogs.setDeviceId(GenerateUUID.generateShortUUID());
        userLogs.setDeviceType(deviceDetails.getDeviceType());
        userLogs.setOperatingSystem(deviceDetails.getOperatingSystem());
        userLogs.setMicroservice_name(MICROSERVICE.valueOf(req.getMicroServiceName()));
        userLogsRepository.save(userLogs);

        return jwtToken;
    }


    public AuthResponse signIn(LoginRequest req, HttpServletRequest httpRequest) throws BadRequestException {

        long userCount = userRepository.countUserByEmail(req.getEmail());
        if (userCount > 0) {
            AuthUsers findConfirmedUser = userRepository.findByEmail(req.getEmail());
            Long jwtBlackListCount = jwtBlackListRepository.findByUserId(findConfirmedUser.getId());
            if (jwtBlackListCount > 0) throw new BadRequestException(
                    findConfirmedUser.getFullName() + ", You are blackListed. Contact Support For Remove As BlackList" + findConfirmedUser.getRole() + "!"
            );
        }

        String email = req.getEmail();
        String otp = req.getOtp();

        AuthUsers foundUser = userRepository.findByEmail(req.getEmail());
        String ipAddress = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        Authentication authentication = authenticate(email, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(foundUser.getId(), foundUser.getEmail(), foundUser.getRole());
        AuthResponse authResponse = new AuthResponse();

        String deviceId = UUID.randomUUID().toString();
        UserLogs userLogs = new UserLogs();
        userLogs.setUser(foundUser);
        userLogs.setIpAddress(ipAddress);
        userLogs.setDeviceId(deviceId);
        userLogs.setJwtToken(token);
        userLogs.setDeviceType(httpRequest.getHeader("User-Agent"));
        userLogs.setOperatingSystem("Unknown");
        userLogs.setMicroservice_name(MICROSERVICE.AUTHENTICATION);
        userLogsRepository.save(userLogs);

        System.out.println(email + " ----- " + otp);

        int findTotalLoggedInDevices = userLogsRepository.findCountByEmail(foundUser.getEmail());

        if (findTotalLoggedInDevices > KeywordsAndConstants.MAXIMUM_DEVICE_CAN_CONNECT) {
            userLogsRepository.deleteAllByEmailAndLastCreated(foundUser.getEmail());
            authResponse.setMessage("Login Success But Logged Out From Most Last Logged In Device");
        } else {
            authResponse.setMessage("Login Success");
        }

        authResponse.setJwt(token);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();
        authResponse.setRole(USER_ROLE.valueOf(roleName));

        return authResponse;
    }

    private UsernamePasswordAuthenticationToken authenticate(String email, String otp) throws BadRequestException {
        UserDetails userDetails = customUserDetails.loadUserByUsername(email);

        System.out.println("sign in userDetails - " + userDetails);

        if (userDetails == null) {
            System.out.println("sign in userDetails - null ");
            throw new BadCredentialsException("Invalid username or password");
        }
        List<AuthVerificationCode> authVerificationCode = verificationCodeRepository.findByEmail(email);

        if (authVerificationCode == null || !authVerificationCode.get(0).getOtp().equals(otp)) {
            throw new BadRequestException("wrong otp...");
        }
        if (LocalDateTime.now().isAfter(authVerificationCode.get(0).getExpiryDate())) {
            verificationCodeRepository.delete(authVerificationCode.get(0));
            throw new BadRequestException("OTP expired...");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
