package com.renacegest.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/", "/inicio"})
public class InicioServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String role = AuthUtil.getCurrentRole(request);
        Long userId = AuthUtil.getCurrentUserId(request);

        request.setAttribute("currentRole", role);
        request.setAttribute("currentUserName", request.getSession(false) == null ? null : request.getSession(false).getAttribute("currentUserName"));
        request.setAttribute("sectionAccess", PermissionService.resolveAccessMap(getServletContext(), role, userId));

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/inicio.jsp");
        dispatcher.forward(request, response);
    }
}
