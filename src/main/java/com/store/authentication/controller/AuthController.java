package com.store.authentication.controller;

import com.store.authentication.enums.USER_ROLE;
import com.store.authentication.model.VerificationCode;
import com.store.authentication.request.LoginRequest;
import com.store.authentication.request.SignUpRequest;
import com.store.authentication.response.ApiResponse;
import com.store.authentication.response.AuthResponse;
import com.store.authentication.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody SignUpRequest req, HttpServletRequest httpRequest) throws Exception {

        String jwt=authService.createUser(req, httpRequest);

        AuthResponse res=new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("Register Success");
        res.setRole(USER_ROLE.ROLE_CUSTOMER);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/sent/otp")
    public ResponseEntity<ApiResponse> sentLoginOtp(
            @RequestBody VerificationCode req) throws Exception {

        authService.sentLoginOtp(req.getEmail());

        ApiResponse res = new ApiResponse();
        res.setMessage("We will Send a OTP to your email-> "+req.getEmail());
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/signIn")
    public ResponseEntity<AuthResponse> signIn(@RequestBody LoginRequest loginRequest, HttpServletRequest httpRequest) throws Exception {

        AuthResponse authResponse = authService.signIn(loginRequest, httpRequest);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}
