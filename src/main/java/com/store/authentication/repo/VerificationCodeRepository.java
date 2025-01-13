package com.store.authentication.repo;

import com.store.authentication.model.VerificationCode;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode,String> {
    VerificationCode findByEmail(String email);
    VerificationCode findByOtp(String otp);


    @Transactional
    @Modifying
    @Query("UPDATE VerificationCode v SET v.user.id = :userId WHERE v.email = :email")
    void updateUserId(String userId, String email);


    List<VerificationCode> findByExpiryDateBefore(LocalDateTime now);
}

