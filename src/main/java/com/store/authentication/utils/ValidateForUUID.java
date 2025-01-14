package com.store.authentication.utils;

import com.store.authentication.error.BadRequestException;

import java.util.UUID;

public class ValidateForUUID {

    public static boolean check(String potentialId, String paramName, String customErrorMessage) throws BadRequestException {
        try {
            UUID.fromString(potentialId);
        } catch (Exception ex) {
            ex.printStackTrace();
            String paramPostFix = paramName.isEmpty() ? "" : " ";
            String errorMessageToShow = customErrorMessage != null ? customErrorMessage :
                    "Please provide a valid " + paramName + paramPostFix + "id";
            BadRequestException badRequestException = new BadRequestException();
            badRequestException.setErrorMessage(errorMessageToShow);
            throw badRequestException;
        }
        return true;
    }
}
