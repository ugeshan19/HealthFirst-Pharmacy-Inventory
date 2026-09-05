package com.healthfirst.pims.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Ugeshan Naicker Student No.402313594
 * Simple SHA-256 password hashing helper.
 * Passwords are never stored or compared in plain text.
 */
public final class PasswordUtil {

    private PasswordUtil() {}

    public static String hash(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainText.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to hash password", e);
        }
    }

    public static boolean matches(String plainText, String hashed) {
        return hash(plainText).equalsIgnoreCase(hashed);
    }
}
