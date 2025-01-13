package com.store.authentication.rabbitMq;

import com.store.authentication.request.RabbitMqRequestForOtpDeliver;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqReceiver {

    private JavaMailSender javaMailSender;

    @RabbitListener(
            queues = "#{(T(com.store.authentication.config.KeywordsAndConstants).RABBIT_MQ_QUEUE_FOR_LOGIN_OR_SIGNUP_OTP)}",
            concurrency = "1"
    )
    public void processEmail(RabbitMqRequestForOtpDeliver rabbitMqRequestForOtpDeliver) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setSubject(rabbitMqRequestForOtpDeliver.getSubject());
            helper.setText(rabbitMqRequestForOtpDeliver.getMessage() + rabbitMqRequestForOtpDeliver.getOtp(), true);
            helper.setTo(rabbitMqRequestForOtpDeliver.getEmail());
            javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

}

