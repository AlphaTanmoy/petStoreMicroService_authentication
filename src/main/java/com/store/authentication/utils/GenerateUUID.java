package com.store.authentication.utils;

import java.util.UUID;

public class GenerateUUID {
    public static String generateShortUUID() {
        return UUID.randomUUID().toString();
    }
}
