package com.store.authentication.model;

import com.store.authentication.model.superEntity.SuperEntityWithoutExpiry;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.store.authentication.config.KeywordsAndConstants.OTP_EXPIRED_IN_MINUTES;


@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "verification_code")
public class VerificationCode extends SuperEntityWithoutExpiry {

    private String otp;

    private String email;

    @OneToOne
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate =LocalDateTime.now().plusMinutes(OTP_EXPIRED_IN_MINUTES);
}
