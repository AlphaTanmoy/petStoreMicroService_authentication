package com.store.authentication.response;

import com.store.authentication.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCodeGrabber {
    private String otp;
    private String email;
    private User user;
    private LocalDateTime expiryDate;
}
