package com.renacegest.servlet;

import com.renacegest.dao.RenaceGestRepository;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AuthUtil.requireAnyRole(request, response, "Maestre", "Sargento", "Guardia")) {
            return;
        }

        try {
            RenaceGestRepository repository = SessionRepositoryResolver.resolve(request);
            request.setAttribute("guardias", repository.findAllGuardias());
            request.setAttribute("grupos", repository.findAllGrupos());
            request.setAttribute("mensajes", repository.findAllMensajes().stream().limit(5).collect(Collectors.toList()));
            request.setAttribute("totalGuardias", repository.findAllGuardias().size());
            request.setAttribute("totalGrupos", repository.findAllGrupos().size());
            request.setAttribute("totalMensajes", repository.findAllMensajes().size());
        } catch (RuntimeException ex) {
            request.setAttribute("guardias", Collections.emptyList());
            request.setAttribute("grupos", Collections.emptyList());
            request.setAttribute("mensajes", Collections.emptyList());
            request.setAttribute("totalGuardias", 0);
            request.setAttribute("totalGrupos", 0);
            request.setAttribute("totalMensajes", 0);
            request.setAttribute("dbErrorMessage", "Sesion iniciada, pero no se pudo conectar a MySQL. Revisa credenciales y permisos de BD.");
        }
        request.setAttribute("currentRole", AuthUtil.getCurrentRole(request));

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/home.jsp");
        dispatcher.forward(request, response);
    }
}
