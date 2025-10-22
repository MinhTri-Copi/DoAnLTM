package com.example.doanltm.Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHashUtil {
    
    /**
     * Hash password sử dụng SHA-256 với salt
     */
    public static String hashPassword(String password) {
        try {
            // Generate salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // Hash password với salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Combine salt + hash
            byte[] saltAndHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltAndHash, salt.length, hashedPassword.length);
            
            // Encode to Base64
            return Base64.getEncoder().encodeToString(saltAndHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
    
    /**
     * Verify password với hashed password
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            // Decode Base64
            byte[] saltAndHash = Base64.getDecoder().decode(hashedPassword);
            
            // Extract salt
            byte[] salt = new byte[16];
            System.arraycopy(saltAndHash, 0, salt, 0, 16);
            
            // Hash input password với same salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedInput = md.digest(password.getBytes());
            
            // Compare hashes
            byte[] storedHash = new byte[saltAndHash.length - 16];
            System.arraycopy(saltAndHash, 16, storedHash, 0, storedHash.length);
            
            return MessageDigest.isEqual(hashedInput, storedHash);
        } catch (IllegalArgumentException | NoSuchAlgorithmException e) {
            System.err.println("❌ Lỗi khi verify password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if password is strong (convenience method)
     */
    public static boolean isPasswordStrong(String password) {
        return validatePasswordStrength(password).isValid();
    }
    
    /**
     * Validate mật khẩu theo yêu cầu:
     * - Tối thiểu 6 kỷ tự
     * - 1 chữ hoa
     * - 1 số
     * - 1 kỷ đặc biệt
     */
    public static PasswordValidation validatePasswordStrength(String password) {
        PasswordValidation validation = new PasswordValidation();
        
        if (password == null || password.isEmpty()) {
            return validation;
        }
        
        // Check length >= 6
        validation.hasMinLength = password.length() >= 6;
        
        // Check at least one uppercase
        validation.hasUppercase = password.matches(".*[A-Z].*");
        
        // Check at least one digit
        validation.hasDigit = password.matches(".*[0-9].*");
        
        // Check at least one special character
        validation.hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>?/`~].*");
        
        return validation;
    }
    
    /**
     * Inner class để lưu trữ validation results
     */
    public static class PasswordValidation {
        public boolean hasMinLength = false;
        public boolean hasUppercase = false;
        public boolean hasDigit = false;
        public boolean hasSpecialChar = false;
        
        public boolean isValid() {
            return hasMinLength && hasUppercase && hasDigit && hasSpecialChar;
        }
        
        @Override
        public String toString() {
            return "PasswordValidation{" +
                    "hasMinLength=" + hasMinLength +
                    ", hasUppercase=" + hasUppercase +
                    ", hasDigit=" + hasDigit +
                    ", hasSpecialChar=" + hasSpecialChar +
                    ", isValid=" + isValid() +
                    '}';
        }
    }
}
