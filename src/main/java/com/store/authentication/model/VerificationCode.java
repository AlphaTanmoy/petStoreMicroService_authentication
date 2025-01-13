package com.store.authentication.model;

import com.store.authentication.utils.GenerateUUID;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import static com.store.authentication.config.KeywordsAndConstants.OTP_EXPIRED_IN_MINUTES;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "verification_code")
public class VerificationCode {

    @Id
    private String id = GenerateUUID.generateShortUUID();

    private String otp;

    private String email;

    @OneToOne
    private User user;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime expiryDate =LocalDateTime.now().plusMinutes(OTP_EXPIRED_IN_MINUTES);
}
