package com.renacegest.servlet;

import com.renacegest.dao.InMemoryRenaceGestRepository;
import com.renacegest.dao.RenaceGestRepository;
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
    private final RenaceGestRepository repository = InMemoryRenaceGestRepository.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("guardias", repository.findAllGuardias());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/login.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String role = request.getParameter("role");
        String guardiaIdRaw = request.getParameter("guardiaId");
        String claveAcceso = request.getParameter("claveAcceso");

        if (role == null || role.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/login?error=rol");
            return;
        }

        HttpSession session = request.getSession(true);

        if ("Amigo".equalsIgnoreCase(role)) {
            session.setAttribute("currentRole", "Amigo");
            session.setAttribute("currentUserId", null);
            session.setAttribute("currentUserName", "Amigo");
            response.sendRedirect(request.getContextPath() + "/amigos");
            return;
        }

        if (guardiaIdRaw == null || guardiaIdRaw.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/login?error=usuario");
            return;
        }

        if (claveAcceso == null || claveAcceso.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/login?error=clave");
            return;
        }

        Guardia guardia = repository.findGuardiaById(Long.valueOf(guardiaIdRaw));
        if (guardia == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=usuario");
            return;
        }

        if (!role.equalsIgnoreCase(guardia.getRango())) {
            response.sendRedirect(request.getContextPath() + "/login?error=rango");
            return;
        }

        if (!claveAcceso.equals(guardia.getClaveAcceso())) {
            response.sendRedirect(request.getContextPath() + "/login?error=clave");
            return;
        }

        session.setAttribute("currentRole", guardia.getRango());
        session.setAttribute("currentUserId", guardia.getId());
        session.setAttribute("currentUserName", guardia.getApodo());
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
