package com.Finzo.user_service.constants;

public class ApplicationConstants {
    public static final String JWT_SECRET_KEY = "app.jwt.secret";
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 hours
}
