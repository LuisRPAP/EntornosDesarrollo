package com.renacegest.servlet;

import com.renacegest.dao.RenaceGestRepository;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/mensajes"})
public class MensajeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PermissionService.requireSectionAccess(request, response, PermissionService.SECTION_MENSAJES, "Maestre", "Sargento", "Guardia")) {
            return;
        }

        cargarVista(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PermissionService.requireSectionAccess(request, response, PermissionService.SECTION_MENSAJES, "Maestre", "Sargento", "Guardia")) {
            return;
        }

        String estado;
        RenaceGestRepository repository = repository(request);
        try {
            Long emisorId = AuthUtil.getCurrentUserId(request);
            if (emisorId == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            boolean canBroadcast = AuthUtil.hasAnyRole(request, "Maestre");
            String contenido = request.getParameter("contenido");
            boolean broadcast = Boolean.parseBoolean(request.getParameter("broadcast"));
            String grupoIdParam = request.getParameter("grupoId");
            Long grupoId = grupoIdParam == null || grupoIdParam.isBlank() ? null : Long.valueOf(grupoIdParam);

            if (broadcast && !canBroadcast) {
                throw new IllegalArgumentException("Solo Maestre puede enviar broadcast global.");
            }

            if (grupoId == null && !broadcast) {
                throw new IllegalArgumentException("Selecciona un grupo o activa broadcast.");
            }

            repository.enviarMensaje(emisorId, grupoId, contenido, broadcast);
            estado = "Mensaje enviado correctamente.";
        } catch (Exception ex) {
            estado = ex.getMessage();
        }

        cargarVista(request, response, estado);
    }

    private void cargarVista(HttpServletRequest request, HttpServletResponse response, String estado) throws ServletException, IOException {
        RenaceGestRepository repository = repository(request);
        request.setAttribute("estado", estado);
        request.setAttribute("mensajes", repository.findAllMensajes());
        request.setAttribute("grupos", repository.findAllGrupos());
        request.setAttribute("canBroadcast", AuthUtil.hasAnyRole(request, "Maestre"));
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/mensajes.jsp");
        dispatcher.forward(request, response);
    }

    private RenaceGestRepository repository(HttpServletRequest request) {
        return SessionRepositoryResolver.resolve(request);
    }
}
