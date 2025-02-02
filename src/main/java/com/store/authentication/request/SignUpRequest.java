package com.store.authentication.request;

import lombok.Data;

@Data
public class SignUpRequest {
    private String email;
    private String fullName;
    private String otp;
    private String mobileNumber;
}
