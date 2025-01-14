package com.store.authentication.utils;

import com.store.authentication.config.KeywordsAndConstants;
import com.store.authentication.error.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidatorMaster {

    public boolean validate(String passwordString, boolean throwExceptionIfFailed) {
        boolean valid = true;
        StringBuilder passwordValidationMessage = new StringBuilder();
        boolean genericMessageAdded = false;

        if (KeywordsAndConstants.GET_PASSWORD_MIN_LENGTH != 0 && passwordString.length() < KeywordsAndConstants.GET_PASSWORD_MIN_LENGTH) {
            passwordValidationMessage.append("Password minimum length must be ")
                    .append(KeywordsAndConstants.GET_PASSWORD_MIN_LENGTH)
                    .append(". ");
            valid = false;
        }

        if (KeywordsAndConstants.IS_PASSWORD_MUST_HAVE_CAPITAL_LETTER && !passwordString.matches(".*[A-Z]+.*")) {
            passwordValidationMessage.append("Password must have at least one capital letter. ");
            valid = false;
            genericMessageAdded = true;
        }

        if (KeywordsAndConstants.IS_PASSWORD_MUST_HAVE_SMALL_LETTER && !passwordString.matches(".*[a-z]+.*")) {
            if (genericMessageAdded) {
                passwordValidationMessage.append(", small letter");
            } else {
                passwordValidationMessage.append("Password must have at least one small letter.");
            }
            valid = false;
            genericMessageAdded = true;
        }

        if (KeywordsAndConstants.IS_PASSWORD_MUST_HAVE_NUMBER && !passwordString.matches(".*\\d+.*")) {
            if (genericMessageAdded) {
                passwordValidationMessage.append(", number");
            } else {
                passwordValidationMessage.append("Password must have at least one number.");
            }
            valid = false;
            genericMessageAdded = true;
        }

        if (KeywordsAndConstants.IS_PASSWORD_MUST_HAVE_SPECIAL_CHARACTER && !passwordString.matches(".*\\W+.*")) {
            if (genericMessageAdded) {
                passwordValidationMessage.append(", special character (@%+#)");
            } else {
                passwordValidationMessage.append("Password must have at least one special character (@%+#).");
            }
            valid = false;
            genericMessageAdded = true;
        }

        if (!valid && throwExceptionIfFailed) {
            BadRequestException badRequestException = new BadRequestException();
            badRequestException.setErrorMessage(passwordValidationMessage.toString());
            throw badRequestException;
        }

        return valid;
    }
}

