package com.ODG.ODG_back.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class JwtCookieUtil {

    private static final String COOKIE_NAME = "access_token";

    public static void addTokenToCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofHours(1))
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
    }

    public static String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void deleteCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
    }
}
