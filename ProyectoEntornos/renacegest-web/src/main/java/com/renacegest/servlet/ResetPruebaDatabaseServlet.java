package com.renacegest.servlet;

import com.renacegest.db.DBConnection;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/reset-prueba-db"})
public class ResetPruebaDatabaseServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!AuthUtil.requireAnyRole(request, response, "Maestre")) {
            return;
        }

        String profile = AuthUtil.getCurrentDbProfile(request);
        if (!DBConnection.PROFILE_PRUEBA.equalsIgnoreCase(profile)) {
            response.sendRedirect(request.getContextPath() + "/importacion?reset=forbidden");
            return;
        }

        try {
            DBConnection.resetPruebaDatabase();
            Long hiddenId = DBConnection.ensureHiddenSuperuser(DBConnection.PROFILE_PRUEBA);

            HttpSession session = request.getSession(false);
            if (session != null && hiddenId != null) {
                session.setAttribute("currentUserId", hiddenId);
                session.setAttribute("currentUserName", "Administrador");
            }

            response.sendRedirect(request.getContextPath() + "/importacion?reset=ok");
        } catch (RuntimeException ex) {
            response.sendRedirect(request.getContextPath() + "/importacion?reset=error");
        }
    }
}
