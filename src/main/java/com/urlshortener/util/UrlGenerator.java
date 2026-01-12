package com.urlshortener.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;

public class UrlGenerator {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 8;

    public static String generateShortCode(String originalUrl, UUID userId) {
        try {
            String input = originalUrl + userId.toString() + System.currentTimeMillis();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());

            BigInteger number = new BigInteger(1, digest);
            StringBuilder shortCode = new StringBuilder();

            for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
                int index = number.mod(BigInteger.valueOf(ALPHABET.length())).intValue();
                shortCode.append(ALPHABET.charAt(index));
                number = number.divide(BigInteger.valueOf(ALPHABET.length()));
            }

            return shortCode.toString();

        } catch (Exception e) {
            // Fallback
            return userId.toString().replace("-", "").substring(0, 8);
        }
    }
}