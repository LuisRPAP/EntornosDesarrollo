package com.renacegest.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PermissionService {
    public static final String SECTION_HOME = "home";
    public static final String SECTION_INVENTARIO = "inventario";
    public static final String SECTION_GRUPOS = "grupos";
    public static final String SECTION_GRUPOS_RESUMEN = "gruposResumen";
    public static final String SECTION_MENSAJES = "mensajes";
    public static final String SECTION_LISTADOS = "listados";
    public static final String SECTION_IMPORTACION = "importacion";
    public static final String SECTION_GUARDIAS = "guardias";
    public static final String SECTION_PERMISOS = "permisos";

    private static final String ROLE_OVERRIDES_KEY = "renacegest.permission.role.overrides";
    private static final String USER_OVERRIDES_KEY = "renacegest.permission.user.overrides";

    private PermissionService() {
    }

    public static boolean requireSectionAccess(HttpServletRequest request,
                                               HttpServletResponse response,
                                               String section,
                                               String... allowedRoles) throws IOException {
        if (!AuthUtil.requireAnyRole(request, response, allowedRoles)) {
            return false;
        }

        String role = AuthUtil.getCurrentRole(request);
        Long userId = AuthUtil.getCurrentUserId(request);
        if (!canAccess(request.getServletContext(), role, userId, section)) {
            response.sendRedirect(request.getContextPath() + "/inicio?error=permiso");
            return false;
        }

        return true;
    }

    public static boolean canAccess(ServletContext context, String role, Long userId, String section) {
        if (section == null || section.isBlank()) {
            return false;
        }

        Map<Long, Map<String, Boolean>> userOverrides = userOverrides(context);
        Map<String, Map<String, Boolean>> roleOverrides = roleOverrides(context);

        if (userId != null) {
            Map<String, Boolean> userMap = userOverrides.get(userId);
            if (userMap != null && userMap.containsKey(section)) {
                return userMap.get(section);
            }
        }

        String roleKey = normalizeRole(role);
        if (roleKey != null) {
            Map<String, Boolean> roleMap = roleOverrides.get(roleKey);
            if (roleMap != null && roleMap.containsKey(section)) {
                return roleMap.get(section);
            }
        }

        return defaultPermission(roleKey, section);
    }

    public static void setRolePermission(ServletContext context, String role, String section, boolean allowed) {
        String roleKey = normalizeRole(role);
        if (roleKey == null || section == null || section.isBlank()) {
            return;
        }

        roleOverrides(context)
                .computeIfAbsent(roleKey, key -> new ConcurrentHashMap<>())
                .put(section, allowed);
    }

    public static void setUserPermission(ServletContext context, Long userId, String section, boolean allowed) {
        if (userId == null || section == null || section.isBlank()) {
            return;
        }

        userOverrides(context)
                .computeIfAbsent(userId, key -> new ConcurrentHashMap<>())
                .put(section, allowed);
    }

    public static Map<String, Boolean> resolveAccessMap(ServletContext context, String role, Long userId) {
        Map<String, Boolean> access = new LinkedHashMap<>();
        access.put(SECTION_HOME, canAccess(context, role, userId, SECTION_HOME));
        access.put(SECTION_INVENTARIO, canAccess(context, role, userId, SECTION_INVENTARIO));
        access.put(SECTION_GRUPOS, canAccess(context, role, userId, SECTION_GRUPOS));
        access.put(SECTION_GRUPOS_RESUMEN, canAccess(context, role, userId, SECTION_GRUPOS_RESUMEN));
        access.put(SECTION_MENSAJES, canAccess(context, role, userId, SECTION_MENSAJES));
        access.put(SECTION_LISTADOS, canAccess(context, role, userId, SECTION_LISTADOS));
        access.put(SECTION_IMPORTACION, canAccess(context, role, userId, SECTION_IMPORTACION));
        access.put(SECTION_GUARDIAS, canAccess(context, role, userId, SECTION_GUARDIAS));
        access.put(SECTION_PERMISOS, canAccess(context, role, userId, SECTION_PERMISOS));
        return access;
    }

    public static Map<String, Boolean> getRoleOverridesForRole(ServletContext context, String role) {
        String roleKey = normalizeRole(role);
        if (roleKey == null) {
            return Map.of();
        }
        Map<String, Boolean> roleMap = roleOverrides(context).get(roleKey);
        return roleMap == null ? Map.of() : new LinkedHashMap<>(roleMap);
    }

    public static Map<String, Boolean> getUserOverridesForUser(ServletContext context, Long userId) {
        if (userId == null) {
            return Map.of();
        }
        Map<String, Boolean> userMap = userOverrides(context).get(userId);
        return userMap == null ? Map.of() : new LinkedHashMap<>(userMap);
    }

    private static String normalizeRole(String role) {
        if (role == null) {
            return null;
        }
        String raw = role.trim();
        if (raw.equalsIgnoreCase("Maestre")) {
            return "Maestre";
        }
        if (raw.equalsIgnoreCase("Sargento")) {
            return "Sargento";
        }
        if (raw.equalsIgnoreCase("Guardia")) {
            return "Guardia";
        }
        if (raw.equalsIgnoreCase("Amigo")) {
            return "Amigo";
        }
        return null;
    }

    private static boolean defaultPermission(String role, String section) {
        if (section.equals(SECTION_HOME) || section.equals(SECTION_INVENTARIO) || section.equals(SECTION_GRUPOS)
                || section.equals(SECTION_GRUPOS_RESUMEN) || section.equals(SECTION_MENSAJES)
                || section.equals(SECTION_LISTADOS)) {
            return role != null && (role.equals("Maestre") || role.equals("Sargento") || role.equals("Guardia"));
        }

        if (section.equals(SECTION_IMPORTACION)) {
            return role != null && (role.equals("Maestre") || role.equals("Sargento"));
        }

        if (section.equals(SECTION_GUARDIAS) || section.equals(SECTION_PERMISOS)) {
            return role != null && role.equals("Maestre");
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, Boolean>> roleOverrides(ServletContext context) {
        synchronized (context) {
            Object current = context.getAttribute(ROLE_OVERRIDES_KEY);
            if (current instanceof Map<?, ?>) {
                return (Map<String, Map<String, Boolean>>) current;
            }
            Map<String, Map<String, Boolean>> created = new ConcurrentHashMap<>();
            context.setAttribute(ROLE_OVERRIDES_KEY, created);
            return created;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<Long, Map<String, Boolean>> userOverrides(ServletContext context) {
        synchronized (context) {
            Object current = context.getAttribute(USER_OVERRIDES_KEY);
            if (current instanceof Map<?, ?>) {
                return (Map<Long, Map<String, Boolean>>) current;
            }
            Map<Long, Map<String, Boolean>> created = new ConcurrentHashMap<>();
            context.setAttribute(USER_OVERRIDES_KEY, created);
            return created;
        }
    }
}
