package com.store.authentication.service;


import com.store.authentication.request.RabbitMqRequestForOtpDeliver;
import com.store.authentication.config.KeywordsAndConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EmailService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendVerificationOtpEmail(String userEmail, String otp, String subject, String text) {
        try {
            RabbitMqRequestForOtpDeliver emailMessage = new RabbitMqRequestForOtpDeliver();
            emailMessage.setEmail(userEmail);
            emailMessage.setOtp(otp);
            emailMessage.setSubject(subject);
            emailMessage.setMessage(text);

            rabbitTemplate.convertAndSend(
                    KeywordsAndConstants.RABBIT_MQ_EXCHANGE,
                    KeywordsAndConstants.RABBIT_MQ_ROUTE_KEY_FOR_LOGIN_OR_SIGNUP_OTP,
                    emailMessage
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email through RabbitMQ", e);
        }
    }

}
