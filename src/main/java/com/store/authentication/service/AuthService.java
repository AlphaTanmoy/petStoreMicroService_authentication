package com.store.authentication.service;

import com.store.authentication.request.LoginRequest;
import com.store.authentication.request.SignUpRequest;
import com.store.authentication.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    void sentLoginOtp(String email) throws Exception;
    String createUser(SignUpRequest req, HttpServletRequest httpRequest) throws Exception;
    AuthResponse signIn(LoginRequest req, HttpServletRequest httpRequest) throws Exception;
}
