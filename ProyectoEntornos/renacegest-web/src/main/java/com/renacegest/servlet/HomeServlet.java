package com.renacegest.servlet;

import com.renacegest.dao.RenaceGestRepository;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AuthUtil.requireAnyRole(request, response, "Maestre", "Sargento", "Guardia")) {
            return;
        }

        HttpSession session = request.getSession(false);
        String currentRole = (String) session.getAttribute("currentRole");
        request.setAttribute("currentRole", currentRole);
        request.setAttribute("currentUserName", session.getAttribute("currentUserName"));
        request.setAttribute("canManageGuardias", "Maestre".equalsIgnoreCase(currentRole));
        request.setAttribute("canManageGroups", "Maestre".equalsIgnoreCase(currentRole) || "Sargento".equalsIgnoreCase(currentRole));
        request.setAttribute("canManageInventario", "Maestre".equalsIgnoreCase(currentRole) || "Sargento".equalsIgnoreCase(currentRole));

        RenaceGestRepository repository = repository(request);
        request.setAttribute("guardias", repository.findAllGuardias());
        request.setAttribute("grupos", repository.findAllGrupos());
        request.setAttribute("mensajes", repository.findAllMensajes().stream().limit(5).collect(Collectors.toList()));
        request.setAttribute("pertrechos", repository.findAllPertrechos().stream().limit(5).collect(Collectors.toList()));
        request.setAttribute("totalGuardias", repository.findAllGuardias().size());
        request.setAttribute("totalGrupos", repository.findAllGrupos().size());
        request.setAttribute("totalMensajes", repository.findAllMensajes().size());
        request.setAttribute("totalPertrechos", repository.findAllPertrechos().size());
        request.setAttribute("totalAlardes", repository.findAllAlardes().size());
        request.setAttribute("totalTickets", repository.getTotalTicketsMaestranza());
        request.setAttribute("currentDbProfile", AuthUtil.getCurrentDbProfile(request));

        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }

    private RenaceGestRepository repository(HttpServletRequest request) {
        return SessionRepositoryResolver.resolve(request);
    }
}
