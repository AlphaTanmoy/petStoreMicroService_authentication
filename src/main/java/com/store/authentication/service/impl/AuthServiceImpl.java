package com.store.authentication.service.impl;

import com.store.authentication.config.JwtProvider;
import com.store.authentication.config.KeywordsAndConstants;
import com.store.authentication.enums.INFO_LOG_TYPE;
import com.store.authentication.enums.USER_ROLE;
import com.store.authentication.model.InfoLogger;
import com.store.authentication.model.User;
import com.store.authentication.model.UserLogs;
import com.store.authentication.model.VerificationCode;
import com.store.authentication.repo.InfoLoggerRepository;
import com.store.authentication.repo.UserLogsRepository;
import com.store.authentication.repo.UserRepository;
import com.store.authentication.repo.VerificationCodeRepository;
import com.store.authentication.request.LoginRequest;
import com.store.authentication.request.SignUpRequest;
import com.store.authentication.response.AuthResponse;
import com.store.authentication.service.AuthService;
import com.store.authentication.service.EmailService;
import com.store.authentication.utils.OtpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.store.authentication.config.KeywordsAndConstants.MAXIMUM_DEVICE_CAN_CONNECT;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserLogsRepository userLogsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final CustomUserServiceImplementation customUserDetails;
    private final InfoLoggerRepository infoLoggerRepository;

    @Override
    public void sentLoginOtp(String email) throws Exception {
        if (email.startsWith(KeywordsAndConstants.SIGNING_PREFIX)) {
            email = email.substring(KeywordsAndConstants.SIGNING_PREFIX.length());
            User user=userRepository.findByEmail(email);
            if(user==null) throw new Exception("User not found with this email!");
        }

        VerificationCode isExist = verificationCodeRepository
                .findByEmail(email);

        if (isExist != null) {
            verificationCodeRepository.delete(isExist);
        }

        String otp = OtpUtils.generateOTP();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode);

        String subject = KeywordsAndConstants.OTP_SUBJECT_FOR_LOGIN;
        String text = KeywordsAndConstants.OTP_TEXT_FOR_LOGIN;
        InfoLogger infoLogger = new InfoLogger();
        infoLogger.setType(INFO_LOG_TYPE.OTP);
        infoLogger.setMessage("OTP Generated for Login/Sign Up Request ");
        infoLoggerRepository.save(infoLogger);
        emailService.sendVerificationOtpEmail(email, otp, subject, text);
    }

    @Transactional
    @Override
    public String createUser(SignUpRequest req, HttpServletRequest httpRequest) throws Exception {
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(req.getEmail());

        if (verificationCode == null || !verificationCode.getOtp().equals(req.getOtp())) {
            throw new Exception("Wrong Otp");
        }

        User user = userRepository.findByEmail(req.getEmail());
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(req.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtProvider.generateToken(authentication);

        String ipAddress = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = httpRequest.getRemoteAddr();
        }

        String deviceId = UUID.randomUUID().toString();

        // If user doesn't exist, create a new user
        if (user == null) {
            // Create new user and save it
            User createdUser = new User();
            createdUser.setFullName(req.getFullName());
            createdUser.setEmail(req.getEmail());
            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            createdUser.setPassword(passwordEncoder.encode(req.getOtp()));
            createdUser.setMobile("9800098000");

            userRepository.save(createdUser);  // Save the user first
        }

        User createdUser = userRepository.findByEmail(req.getEmail());
        verificationCode.setUser(createdUser);  // Set the user for verification code
        verificationCodeRepository.save(verificationCode);  // Save the verification code

        UserLogs userDevice = new UserLogs();
        userDevice.setUser(createdUser);  // Set the user in the log
        userDevice.setIpAddress(ipAddress);
        userDevice.setDeviceId(deviceId);
        userDevice.setJwtToken(jwtToken);
        userDevice.setDeviceType(httpRequest.getHeader("User-Agent"));
        userDevice.setOperatingSystem("Unknown");

        userLogsRepository.save(userDevice);  // Save the user logs

        return jwtProvider.generateToken(authentication);
    }



    @Override
    public AuthResponse signIn(LoginRequest req, HttpServletRequest httpRequest) throws Exception {
        String username = req.getEmail();
        String otp = req.getOtp();

        User foundUser = userRepository.findByEmail(req.getEmail());
        String ipAddress = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        Authentication authentication = authenticate(username, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
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

        System.out.println(username + " ----- " + otp);

        int findTotalLoggedInDevices = userLogsRepository.findCountByEmail(foundUser.getEmail());

        if(findTotalLoggedInDevices > MAXIMUM_DEVICE_CAN_CONNECT){
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

    private UsernamePasswordAuthenticationToken authenticate(String username, String otp) throws Exception {
        UserDetails userDetails = customUserDetails.loadUserByUsername(username);

        System.out.println("sign in userDetails - " + userDetails);

        if (userDetails == null) {
            System.out.println("sign in userDetails - null ");
            throw new BadCredentialsException("Invalid username or password");
        }
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new Exception("wrong otp...");
        }
        if (LocalDateTime.now().isAfter(verificationCode.getExpiryDate())) {
            verificationCodeRepository.delete(verificationCode);
            throw new Exception("OTP expired...");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
