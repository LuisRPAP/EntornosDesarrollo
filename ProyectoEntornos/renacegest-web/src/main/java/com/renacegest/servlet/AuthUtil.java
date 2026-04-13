package com.renacegest.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

public final class AuthUtil {
    private AuthUtil() {
    }

    public static boolean requireAnyRole(HttpServletRequest request, HttpServletResponse response, String... allowedRoles) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        String role = (String) session.getAttribute("currentRole");
        if (role == null || Arrays.stream(allowedRoles).noneMatch(allowed -> allowed.equalsIgnoreCase(role))) {
            response.sendRedirect(request.getContextPath() + "/login?error=acceso");
            return false;
        }

        return true;
    }

    public static String getCurrentRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (String) session.getAttribute("currentRole");
    }

    public static Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object raw = session.getAttribute("currentUserId");
        if (raw instanceof Long) {
            return (Long) raw;
        }
        if (raw instanceof Integer) {
            return ((Integer) raw).longValue();
        }
        return null;
    }

    public static boolean hasAnyRole(HttpServletRequest request, String... allowedRoles) {
        String role = getCurrentRole(request);
        return role != null && Arrays.stream(allowedRoles).anyMatch(allowed -> allowed.equalsIgnoreCase(role));
    }
}
