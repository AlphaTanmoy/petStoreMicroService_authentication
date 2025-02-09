package com.store.authentication.model;

import static com.store.authentication.config.KeywordsAndConstants.OTP_EXPIRED_IN_MINUTES;
import com.store.authentication.model.superEntity.SuperEntityWithoutExpiry;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;


@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "auth_verification_code")
public class AuthVerificationCode extends SuperEntityWithoutExpiry {

    private String otp;

    private String email;

    @OneToOne
    private AuthUsers user;

    @Column(nullable = false)
    private ZonedDateTime expiryDate =ZonedDateTime.now().plusMinutes(OTP_EXPIRED_IN_MINUTES);
}
