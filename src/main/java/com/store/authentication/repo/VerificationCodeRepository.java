package com.store.authentication.repo;

import com.store.authentication.model.AuthVerificationCode;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VerificationCodeRepository extends JpaRepository<AuthVerificationCode, String> {

    @Query(
            value = "SELECT * FROM auth_verification_code " +
                    "WHERE email = :email"
            , nativeQuery = true
    )
    List<AuthVerificationCode> findByEmail(@Param("email") String email);

    AuthVerificationCode findByOtp(String otp);


    @Transactional
    @Modifying
    @Query(
            value = "UPDATE auth_verification_code SET user_id = :userId WHERE email = :email"
            , nativeQuery = true
    )
    void updateUserId(
            @Param("userId") String userId,
            @Param("email") String email
    );


    List<AuthVerificationCode> findByExpiryDateBefore(LocalDateTime now);
}

