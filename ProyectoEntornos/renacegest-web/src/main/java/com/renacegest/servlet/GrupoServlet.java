package com.renacegest.servlet;

import com.renacegest.dao.RenaceGestRepository;
import com.renacegest.model.MiembroGrupo;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/grupos"})
public class GrupoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PermissionService.requireSectionAccess(request, response, PermissionService.SECTION_GRUPOS, "Maestre", "Sargento", "Guardia")) {
            return;
        }
        cargarVista(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PermissionService.requireSectionAccess(request, response, PermissionService.SECTION_GRUPOS, "Maestre", "Sargento", "Guardia")) {
            return;
        }

        Long currentUserId = AuthUtil.getCurrentUserId(request);
        if (currentUserId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (AuthUtil.hasAnyRole(request, "Guardia")) {
            cargarVista(request, response, "Tu rol puede consultar grupos, pero no modificarlos.");
            return;
        }

        String accion = request.getParameter("accion");
        String mensajeEstado;
        RenaceGestRepository repository = repository(request);

        try {
            if ("crear".equalsIgnoreCase(accion)) {
                repository.crearGrupo(
                        request.getParameter("nombreGrupo"),
                        request.getParameter("descripcion"),
                        request.getParameter("tipo"),
                        Long.valueOf(request.getParameter("jefeEquipoId")),
                    currentUserId
                );
                mensajeEstado = "Grupo creado correctamente.";
            } else if ("agregar".equalsIgnoreCase(accion)) {
                boolean aplicado = repository.agregarMiembro(
                        Long.valueOf(request.getParameter("grupoId")),
                        Long.valueOf(request.getParameter("miembroId")),
                    currentUserId
                );
                mensajeEstado = aplicado ? "Miembro añadido al grupo." : "No se ha podido añadir el miembro.";
            } else if ("quitar".equalsIgnoreCase(accion)) {
                boolean aplicado = repository.quitarMiembro(
                        Long.valueOf(request.getParameter("grupoId")),
                        Long.valueOf(request.getParameter("miembroId")),
                    currentUserId
                );
                mensajeEstado = aplicado ? "Miembro eliminado del grupo." : "No se ha podido eliminar el miembro.";
            } else {
                mensajeEstado = "Accion no reconocida.";
            }
        } catch (Exception ex) {
            mensajeEstado = ex.getMessage();
        }

        cargarVista(request, response, mensajeEstado);
    }

    private void cargarVista(HttpServletRequest request, HttpServletResponse response, String mensajeEstado) throws ServletException, IOException {
        RenaceGestRepository repository = repository(request);
        Map<Long, List<MiembroGrupo>> miembrosPorGrupo = new HashMap<>();
        repository.findAllGrupos().forEach(grupo -> miembrosPorGrupo.put(grupo.getId(), repository.findMiembrosByGrupo(grupo.getId())));
        request.setAttribute("grupos", repository.findAllGrupos());
        request.setAttribute("guardias", repository.findAllGuardias());
        request.setAttribute("miembrosPorGrupo", miembrosPorGrupo);
        request.setAttribute("mensajeEstado", mensajeEstado);
        request.setAttribute("canManageGroups", AuthUtil.hasAnyRole(request, "Maestre", "Sargento"));
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/grupos.jsp");
        dispatcher.forward(request, response);
    }

    private RenaceGestRepository repository(HttpServletRequest request) {
        return SessionRepositoryResolver.resolve(request);
    }
}
