package com.store.authentication.utils;

import com.store.authentication.config.KeywordsAndConstants;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class GenerateUUID {
    public static String generateShortUUID() {
        return UUID.randomUUID().toString();
    }

    public static String generateAPIKey() {
        String uuidToAppend = UUID.randomUUID().toString().replace("-", "");
        return KeywordsAndConstants.API_KEY_PREFIX + uuidToAppend;
    }
}
