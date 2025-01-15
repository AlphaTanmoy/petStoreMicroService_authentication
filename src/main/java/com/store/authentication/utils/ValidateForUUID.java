package com.store.authentication.utils;

import com.store.authentication.config.KeywordsAndConstants;
import com.store.authentication.error.BadRequestException;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class ValidateForUUID {

    public static void check(String potentialId, String forEntity) throws BadRequestException {
        try {
            UUID.fromString(potentialId);
        } catch (Exception ex) {
            ex.printStackTrace();
            BadRequestException badRequestException = new BadRequestException();
            badRequestException.setErrorMessage("Error Validating Id for "+forEntity);
            throw badRequestException;
        }
    }

    public static String extractUUIDFromAPIKey(String apiKey) {
        if (apiKey == null || !apiKey.startsWith(KeywordsAndConstants.API_KEY_PREFIX)) {
            throw new IllegalArgumentException("Invalid API key");
        }
        return apiKey.substring(KeywordsAndConstants.API_KEY_PREFIX.length());
    }
}
