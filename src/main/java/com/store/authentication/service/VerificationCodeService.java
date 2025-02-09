package com.store.authentication.service;

import com.store.authentication.error.BadRequestException;
import com.store.authentication.model.AuthVerificationCode;
import com.store.authentication.repo.VerificationCodeRepository;
import com.store.authentication.response.VerificationCodeGrabber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    public VerificationCodeService(VerificationCodeRepository verificationCodeRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
    }

    public AuthVerificationCode findVerificationCodesByEmail(String email) throws BadRequestException {
        BadRequestException badRequestException = new BadRequestException();

        List<AuthVerificationCode> codes = verificationCodeRepository.findByEmail(email);

        if (codes.isEmpty()) {
            badRequestException.setErrorMessage("No OTPs found for Email Id: " + email);
            throw badRequestException;
        }
        return codes.get(0);
    }

    public void saveVerificationCodesByEmail(VerificationCodeGrabber verificationCodeGrabber){
        BadRequestException badRequestException = new BadRequestException();

        List<AuthVerificationCode> codes = verificationCodeRepository.findByEmail(verificationCodeGrabber.getEmail());
        if (codes.isEmpty()) {
            badRequestException.setErrorMessage("Can't Save for: " + verificationCodeGrabber.getEmail());
            throw badRequestException;
        }
        codes.get(0).setUser(verificationCodeGrabber.getUser());
        verificationCodeRepository.save(codes.get(0));
    }

}
