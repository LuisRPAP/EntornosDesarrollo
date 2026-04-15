package com.renacegest.servlet;

import com.renacegest.dao.RenaceGestRepository;
import com.renacegest.model.Guardia;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/recuperar-clave"})
public class RecuperarClaveServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedDbProfile = AuthUtil.normalizeDbProfile(request.getParameter("dbProfile"));
        request.setAttribute("selectedDbProfile", selectedDbProfile);
        request.setAttribute("estado", request.getParameter("estado"));
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/recuperar-clave.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String dbProfile = AuthUtil.normalizeDbProfile(request.getParameter("dbProfile"));
        String apodo = request.getParameter("apodo");
        String fraseRecuperacion = request.getParameter("fraseRecuperacion");
        String nuevaClave = request.getParameter("nuevaClave");
        String confirmaClave = request.getParameter("confirmaClave");

        if (apodo == null || apodo.isBlank()) {
            redirectWithStatus(response, request, "usuario");
            return;
        }
        if (fraseRecuperacion == null || fraseRecuperacion.isBlank()) {
            redirectWithStatus(response, request, "frase");
            return;
        }
        if (nuevaClave == null || nuevaClave.isBlank()) {
            redirectWithStatus(response, request, "clave");
            return;
        }
        if (!nuevaClave.equals(confirmaClave)) {
            redirectWithStatus(response, request, "confirma");
            return;
        }

        RenaceGestRepository repository = repository(dbProfile);
        Guardia guardia = repository.findAllGuardias().stream()
                .filter(item -> item.getApodo() != null && item.getApodo().equalsIgnoreCase(apodo.trim()))
                .findFirst()
                .orElse(null);

        if (guardia == null) {
            redirectWithStatus(response, request, "usuario");
            return;
        }

        boolean cambiado = repository.cambiarClaveConFrase(guardia.getApodo(), fraseRecuperacion, nuevaClave);
        if (!cambiado) {
            redirectWithStatus(response, request, "frase");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/login?dbProfile=" + dbProfile + "&error=recuperada");
    }

    private RenaceGestRepository repository(String dbProfile) {
        if ("REAL".equalsIgnoreCase(dbProfile)) {
            return com.renacegest.dao.MySQLRenaceGestRepository.getRealInstance();
        }
        return com.renacegest.dao.MySQLRenaceGestRepository.getPruebaInstance();
    }

    private void redirectWithStatus(HttpServletResponse response, HttpServletRequest request, String estado) throws IOException {
        response.sendRedirect(request.getContextPath() + "/recuperar-clave?estado=" + estado + "&dbProfile=" + AuthUtil.normalizeDbProfile(request.getParameter("dbProfile")));
    }
}
