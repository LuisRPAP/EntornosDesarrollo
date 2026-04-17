package com.renacegest.servlet;

import com.renacegest.dao.RenaceGestRepository;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/guardias"})
public class GuardiaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PermissionService.requireSectionAccess(request, response, PermissionService.SECTION_GUARDIAS, "Maestre")) {
            return;
        }

        cargarVista(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PermissionService.requireSectionAccess(request, response, PermissionService.SECTION_GUARDIAS, "Maestre")) {
            return;
        }

        Long currentUserId = AuthUtil.getCurrentUserId(request);
        if (currentUserId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String accion = request.getParameter("accion");
        String estado;
        RenaceGestRepository repository = repository(request);

        try {
            if ("crear".equalsIgnoreCase(accion)) {
                com.renacegest.model.Guardia guardiaCreada = repository.crearGuardia(
                        request.getParameter("nombreReal"),
                        request.getParameter("apodo"),
                        request.getParameter("rango"),
                        request.getParameter("claveAcceso"),
                        Boolean.parseBoolean(request.getParameter("maestreActivo")),
                        currentUserId
                );
                guardarRecuperacionSiCorresponde(repository, guardiaCreada == null ? null : guardiaCreada.getId(), request);
                estado = "Guardia creado correctamente.";
            } else if ("actualizar".equalsIgnoreCase(accion)) {
                Long guardiaId = Long.valueOf(request.getParameter("guardiaId"));
                repository.actualizarGuardia(
                        guardiaId,
                        request.getParameter("nombreReal"),
                        request.getParameter("apodo"),
                        request.getParameter("rango"),
                        request.getParameter("claveAcceso"),
                        Integer.parseInt(request.getParameter("puntosGracia")),
                        request.getParameter("estadoHonor"),
                        Boolean.parseBoolean(request.getParameter("maestreActivo")),
                        currentUserId
                );
                guardarRecuperacionSiCorresponde(repository, guardiaId, request);
                estado = "Guardia actualizado correctamente.";
            } else if ("eliminar".equalsIgnoreCase(accion)) {
                boolean eliminado = repository.eliminarGuardia(
                        Long.valueOf(request.getParameter("guardiaId")),
                    currentUserId
                );
                estado = eliminado ? "Guardia eliminado correctamente." : "No se encontro el guardia.";
            } else {
                estado = "Accion no reconocida.";
            }
        } catch (Exception ex) {
            estado = ex.getMessage();
        }

        cargarVista(request, response, estado);
    }

    private void cargarVista(HttpServletRequest request, HttpServletResponse response, String estado) throws ServletException, IOException {
        request.setAttribute("estado", estado);
        request.setAttribute("guardias", repository(request).findAllGuardias());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/guardias.jsp");
        dispatcher.forward(request, response);
    }

    private RenaceGestRepository repository(HttpServletRequest request) {
        return SessionRepositoryResolver.resolve(request);
    }

    private void guardarRecuperacionSiCorresponde(RenaceGestRepository repository, Long guardiaId, HttpServletRequest request) {
        if (guardiaId == null) {
            return;
        }

        String correoRecuperacion = request.getParameter("correoRecuperacion");
        String fraseRecuperacion = request.getParameter("fraseRecuperacion");
        if (fraseRecuperacion != null && !fraseRecuperacion.isBlank()) {
            repository.guardarDatosRecuperacionGuardia(guardiaId, correoRecuperacion, fraseRecuperacion);
        }
    }
}
