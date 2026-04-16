package com.renacegest.servlet;

import com.renacegest.dao.RenaceGestRepository;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AuthUtil.requireAnyRole(request, response, "Maestre", "Sargento", "Guardia")) {
            return;
        }

        RenaceGestRepository repository = SessionRepositoryResolver.resolve(request);
        request.setAttribute("guardias", repository.findAllGuardias());
        request.setAttribute("grupos", repository.findAllGrupos());
        request.setAttribute("mensajes", repository.findAllMensajes().stream().limit(5).collect(Collectors.toList()));
        request.setAttribute("totalGuardias", repository.findAllGuardias().size());
        request.setAttribute("totalGrupos", repository.findAllGrupos().size());
        request.setAttribute("totalMensajes", repository.findAllMensajes().size());
        request.setAttribute("currentRole", AuthUtil.getCurrentRole(request));

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/home.jsp");
        dispatcher.forward(request, response);
    }
}
