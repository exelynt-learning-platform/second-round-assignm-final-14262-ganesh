package com.optshop.config;

public class SecurityConstants {
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";
    public static final String PASSWORD_MESSAGE = "Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, a number, and a special character.";
}
