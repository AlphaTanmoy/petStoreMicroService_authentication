package com.store.authentication.service;

import com.store.authentication.config.JwtProvider;
import com.store.authentication.config.KeywordsAndConstants;
import com.store.authentication.enums.INFO_LOG_TYPE;
import com.store.authentication.enums.TIRE_CODE;
import com.store.authentication.enums.USER_ROLE;
import com.store.authentication.error.BadRequestException;
import com.store.authentication.model.InfoLogger;
import com.store.authentication.model.User;
import com.store.authentication.model.UserLogs;
import com.store.authentication.model.VerificationCode;
import com.store.authentication.repo.*;
import com.store.authentication.request.LoginRequest;
import com.store.authentication.request.SignUpRequest;
import com.store.authentication.response.AuthResponse;
import com.store.authentication.utils.OtpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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

    public void sentLoginOtp(String email) throws BadRequestException {

        long userCount = userRepository.countUserByEmail(email);
        if(userCount>0){
            User findConfirmedUser = userRepository.findByEmail(email);
            Long jwtBlackListCount = jwtBlackListRepository.findByUserId(findConfirmedUser.getId());
            if(jwtBlackListCount>0) throw new BadRequestException(
                    findConfirmedUser.getFullName()+", You are blackListed. Contact Support For Remove As BlackList"+findConfirmedUser.getRole()+"!"
            );
        }

        if (email.startsWith(KeywordsAndConstants.SIGNING_PREFIX)) {
            email = email.substring(KeywordsAndConstants.SIGNING_PREFIX.length());
            User user=userRepository.findByEmail(email);
            if(user==null) throw new BadRequestException("User not found with this email!");
        }

        List<VerificationCode> isExist = verificationCodeRepository
                .findByEmail(email);

        if (!isExist.isEmpty()) {
            verificationCodeRepository.delete(isExist.get(0));
        }

        String otp = OtpUtils.generateOTP();
        String message = "";
        if(userCount==0) message = "OTP Generated for Sign Up Request";
        else message = "OTP Generated for Login Request";

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode);

        String subject = KeywordsAndConstants.OTP_SUBJECT_FOR_LOGIN;
        String text = KeywordsAndConstants.OTP_TEXT_FOR_LOGIN;
        InfoLogger infoLogger = new InfoLogger();
        infoLogger.setType(INFO_LOG_TYPE.OTP);

        infoLogger.setMessage(message+" ");
        infoLoggerRepository.save(infoLogger);
        emailService.sendVerificationOtpEmail(email, otp, subject, text);
    }

    @Transactional
    public String createUser(SignUpRequest req, HttpServletRequest httpRequest) throws BadRequestException {
        long userCount = userRepository.countUserByEmail(req.getEmail());
        if(userCount>0){
            User findConfirmedUser = userRepository.findByEmail(req.getEmail());
            Long jwtBlackListCount = jwtBlackListRepository.findByUserId(findConfirmedUser.getId());
            if(jwtBlackListCount>0) throw new BadRequestException(
                    findConfirmedUser.getFullName()+", You are blackListed. Contact Support For Remove As BlackList"+findConfirmedUser.getRole()+"!"
            );
        }

        List<VerificationCode> verificationCode = verificationCodeRepository.findByEmail(req.getEmail());

        if (verificationCode == null || !verificationCode.get(0).getOtp().equals(req.getOtp())) {
            throw new BadRequestException("Wrong Otp");
        }

        long user = userRepository.countUserByEmail(req.getEmail());
        if (user==0) {
            User createdUser = new User();
            createdUser.setFullName(req.getFullName());
            createdUser.setEmail(req.getEmail());
            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            createdUser.setPassword(passwordEncoder.encode(req.getOtp()));
            createdUser.setMobile("9800098000");
            createdUser.setTireCode(TIRE_CODE.TIRE4);
            userRepository.save(createdUser);
        } else {
            throw new BadRequestException("User already exists");
        }

        User createdUser = userRepository.findByEmail(req.getEmail());

        String jwtToken = jwtProvider.generateToken(createdUser.getId(), createdUser.getEmail(), createdUser.getRole());

        String ipAddress = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = httpRequest.getRemoteAddr();
        }

        String deviceId = UUID.randomUUID().toString();

        verificationCode.get(0).setUser(createdUser);
        verificationCodeRepository.save(verificationCode.get(0));

        UserLogs userDevice = new UserLogs();
        userDevice.setUser(createdUser);
        userDevice.setIpAddress(ipAddress);
        userDevice.setDeviceId(deviceId);
        userDevice.setJwtToken(jwtToken);
        userDevice.setDeviceType(httpRequest.getHeader("User-Agent"));
        userDevice.setOperatingSystem("Unknown");

        userLogsRepository.save(userDevice);

        return jwtToken;
    }



    public AuthResponse signIn(LoginRequest req, HttpServletRequest httpRequest) throws BadRequestException {

        long userCount = userRepository.countUserByEmail(req.getEmail());
        if(userCount>0){
            User findConfirmedUser = userRepository.findByEmail(req.getEmail());
            Long jwtBlackListCount = jwtBlackListRepository.findByUserId(findConfirmedUser.getId());
            if(jwtBlackListCount>0) throw new BadRequestException(
                    findConfirmedUser.getFullName()+", You are blackListed. Contact Support For Remove As BlackList"+findConfirmedUser.getRole()+"!"
            );
        }

        String email = req.getEmail();
        String otp = req.getOtp();

        User foundUser = userRepository.findByEmail(req.getEmail());
        String ipAddress = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        Authentication authentication = authenticate(email, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(foundUser.getId(), foundUser.getEmail(), foundUser.getRole());
        AuthResponse authResponse = new AuthResponse();

        String deviceId = UUID.randomUUID().toString();
        UserLogs userDevice = new UserLogs();
        userDevice.setUser(foundUser);
        userDevice.setIpAddress(ipAddress);
        userDevice.setDeviceId(deviceId);
        userDevice.setJwtToken(token);
        userDevice.setDeviceType(httpRequest.getHeader("User-Agent"));
        userDevice.setOperatingSystem("Unknown");
        userLogsRepository.save(userDevice);

        System.out.println(email + " ----- " + otp);

        int findTotalLoggedInDevices = userLogsRepository.findCountByEmail(foundUser.getEmail());

        if(findTotalLoggedInDevices > KeywordsAndConstants.MAXIMUM_DEVICE_CAN_CONNECT){
            userLogsRepository.deleteAllByEmailAndLastCreated(foundUser.getEmail());
            authResponse.setMessage("Login Success But Logged Out From Most Last Logged In Device");
        }else{
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
        List<VerificationCode> verificationCode = verificationCodeRepository.findByEmail(email);

        if (verificationCode == null || !verificationCode.get(0).getOtp().equals(otp)) {
            throw new BadRequestException("wrong otp...");
        }
        if (LocalDateTime.now().isAfter(verificationCode.get(0).getExpiryDate())) {
            verificationCodeRepository.delete(verificationCode.get(0));
            throw new BadRequestException("OTP expired...");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
