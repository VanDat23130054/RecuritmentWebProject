package com.java_web.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class PasswordUtil {

    private static final int SALT_LENGTH = 16;
    
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
    
    public static byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        return md.digest(password.getBytes());
    }
    
    public static boolean verifyPassword(String password, byte[] storedHash, byte[] salt) 
            throws NoSuchAlgorithmException {
        byte[] computedHash = hashPassword(password, salt);
        return Arrays.equals(computedHash, storedHash);
    }
}
