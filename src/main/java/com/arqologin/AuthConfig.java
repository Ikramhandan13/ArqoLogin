package com.arqologin;

import java.util.Set;

public class AuthConfig {
    public static int AUTH_TIMEOUT;
    public static int MAX_PER_IP;
    public static int IP_LOCKOUT_MINUTES;
    public static int PASSWORD_MIN_LENGTH;
    public static boolean DISALLOW_COMMON;
    public static String USERNAME_PATTERN;

    public static final Set<String> COMMON_PASSWORDS = Set.of(
            "123456", "password", "qwerty", "12345678", "abc123"
    );
}
