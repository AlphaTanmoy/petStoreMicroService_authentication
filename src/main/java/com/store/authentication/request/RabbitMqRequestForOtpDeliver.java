package com.store.authentication.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RabbitMqRequestForOtpDeliver {

    public String message;
    public String email;
    public String otp;
    public String subject;
}
