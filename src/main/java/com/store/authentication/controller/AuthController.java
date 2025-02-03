package com.store.authentication.controller;

import com.store.authentication.config.JwtProvider;
import com.store.authentication.config.KeywordsAndConstants;
import com.store.authentication.enums.MICROSERVICE;
import com.store.authentication.enums.USER_ROLE;
import com.store.authentication.error.BadRequestException;
import com.store.authentication.model.AuthUsers;
import com.store.authentication.request.LoginRequest;
import com.store.authentication.request.RequestEmail;
import com.store.authentication.request.SignUpRequest;
import com.store.authentication.response.ApiResponse;
import com.store.authentication.response.AuthResponse;
import com.store.authentication.response.GetProfile;
import com.store.authentication.service.ApiKeyService;
import com.store.authentication.service.AuthService;
import com.store.authentication.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final ApiKeyService apiKeyService;

    @PostMapping("/signUp")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody SignUpRequest req, HttpServletRequest httpRequest) throws BadRequestException {
        if(req.getMicroServiceName()==null)
            req.setMicroServiceName(String.valueOf(MICROSERVICE.AUTHENTICATION));
        try {
            MICROSERVICE.valueOf(req.getMicroServiceName().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadRequestException("Invalid Microservice Name: " + req.getMicroServiceName());
        }

        String jwt=authService.createUser(req, httpRequest);

        if(jwt==null){
            throw new BadRequestException("User Already Exists With this email Id");
        }

        AuthResponse res=new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("Register Success");
        res.setRole(USER_ROLE.ROLE_CUSTOMER);
        res.setStatus(true);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/sent/otp")
    public ResponseEntity<ApiResponse> sentLoginOtp(
            @RequestBody RequestEmail req
    ) throws Exception {

        authService.sentLoginOtp(req.getEmail());

        ApiResponse res = new ApiResponse();
        res.setStatus(true);
        res.setMessage("We will send an OTP to your email -> " + req.getEmail());
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }


    @PostMapping("/signIn")
    public ResponseEntity<AuthResponse> signIn(@RequestBody LoginRequest loginRequest, HttpServletRequest httpRequest) throws Exception {
        AuthResponse authResponse = authService.signIn(loginRequest, httpRequest);
        authResponse.setStatus(true);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/getProfile")
    public GetProfile getProfile(
            @Valid @RequestHeader(value = KeywordsAndConstants.HEADER_AUTH_TOKEN, required = false) String token
    ){
        BadRequestException badRequestException = new BadRequestException();
        if(token.isEmpty()) {
            badRequestException.setErrorMessage("Provide a valid Token");
        }

        String actionTakerId = jwtProvider.getIdFromJwtToken(token);
        Optional<AuthUsers> findUser = userService.findUserById(actionTakerId);
        GetProfile getProfile = new GetProfile();

        if(findUser.isPresent()){
            getProfile.setName(findUser.get().getFullName());
            String apiKey = apiKeyService.findApiKeyByUserId(findUser.get().getId());
            if(apiKey == null) getProfile.setApiKey("No API Key Present");
            else getProfile.setApiKey(apiKey);
            getProfile.setUserRole(findUser.get().getRole());
            getProfile.setTireCode(findUser.get().getTireCode());
        }
        else{
            badRequestException.setErrorMessage("No User Data Found");
            throw badRequestException;
        }

        return getProfile;
    }
}
