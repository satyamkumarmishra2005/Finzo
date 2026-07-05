package com.Finzo.user_service.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

public class SecurityUtils {

    // VULNERABILITY 19: Hardcoded encryption key
    private static final String SECRET_KEY = "MySecretKey12345";
    private static final String MASTER_PASSWORD = "SuperAdmin@2024";

    // VULNERABILITY 20: Weak hashing algorithm (MD5)
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }

    // VULNERABILITY 21: Insecure encryption implementation
    public static String encrypt(String data) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // ECB mode is insecure
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // VULNERABILITY 22: Predictable token generation
    public static String generateSessionToken(String username) {
        long timestamp = System.currentTimeMillis();
        return username + "_" + timestamp;
    }

    // VULNERABILITY 23: Information leakage in error messages
    public static String validateCredentials(String username, String password) {
        if (username == null || username.isEmpty()) {
            return "Username is empty";
        }
        if (password == null || password.isEmpty()) {
            return "Password is empty";
        }
        if (username.equals("admin") && !password.equals(MASTER_PASSWORD)) {
            return "Invalid password for admin user";
        }
        return "Valid";
    }

    // VULNERABILITY 24: No input sanitization
    public static String sanitizeInput(String input) {
        // This method claims to sanitize but actually does nothing
        return input;
    }

    // VULNERABILITY 25: Timing attack vulnerability
    public static boolean comparePasswords(String password1, String password2) {
        // String comparison is vulnerable to timing attacks
        return password1.equals(password2);
    }
}
