package com.renacegest.servlet;

import com.renacegest.dao.RenaceGestRepository;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/permisos"})
public class PermisosServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PermissionService.requireSectionAccess(request, response, PermissionService.SECTION_PERMISOS, "Maestre")) {
            return;
        }
        cargarVista(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PermissionService.requireSectionAccess(request, response, PermissionService.SECTION_PERMISOS, "Maestre")) {
            return;
        }

        String estado;
        try {
            String alcance = request.getParameter("alcance");
            String seccion = request.getParameter("seccion");
            boolean permitir = "permitir".equalsIgnoreCase(request.getParameter("valor"));

            if ("rol".equalsIgnoreCase(alcance)) {
                String role = request.getParameter("rolObjetivo");
                PermissionService.setRolePermission(getServletContext(), role, seccion, permitir);
                estado = "Permiso actualizado para rol " + role + ".";
            } else if ("usuario".equalsIgnoreCase(alcance)) {
                Long guardiaId = Long.valueOf(request.getParameter("guardiaId"));
                PermissionService.setUserPermission(getServletContext(), guardiaId, seccion, permitir);
                estado = "Permiso actualizado para usuario ID " + guardiaId + ".";
            } else {
                estado = "Alcance no reconocido.";
            }
        } catch (Exception ex) {
            estado = ex.getMessage();
        }

        cargarVista(request, response, estado);
    }

    private void cargarVista(HttpServletRequest request, HttpServletResponse response, String estado) throws ServletException, IOException {
        RenaceGestRepository repository = SessionRepositoryResolver.resolve(request);
        List<com.renacegest.model.Guardia> guardias = repository.findAllGuardias();

        request.setAttribute("estado", estado);
        request.setAttribute("guardias", guardias);
        request.setAttribute("roles", List.of("Maestre", "Sargento", "Guardia"));
        request.setAttribute("secciones", List.of(
                PermissionService.SECTION_HOME,
                PermissionService.SECTION_INVENTARIO,
                PermissionService.SECTION_GRUPOS,
                PermissionService.SECTION_GRUPOS_RESUMEN,
                PermissionService.SECTION_MENSAJES,
                PermissionService.SECTION_LISTADOS,
                PermissionService.SECTION_IMPORTACION,
                PermissionService.SECTION_GUARDIAS,
                PermissionService.SECTION_PERMISOS
        ));

        request.setAttribute("roleOverridesMaestre", PermissionService.getRoleOverridesForRole(getServletContext(), "Maestre"));
        request.setAttribute("roleOverridesSargento", PermissionService.getRoleOverridesForRole(getServletContext(), "Sargento"));
        request.setAttribute("roleOverridesGuardia", PermissionService.getRoleOverridesForRole(getServletContext(), "Guardia"));

        String selectedUserId = request.getParameter("guardiaIdVista");
        Long userId = null;
        if (selectedUserId != null && !selectedUserId.isBlank()) {
            try {
                userId = Long.valueOf(selectedUserId);
            } catch (NumberFormatException ignored) {
            }
        }
        if (userId == null && !guardias.isEmpty()) {
            userId = guardias.get(0).getId();
        }
        request.setAttribute("guardiaIdVista", userId);
        request.setAttribute("userOverridesVista", PermissionService.getUserOverridesForUser(getServletContext(), userId));

        String role = AuthUtil.getCurrentRole(request);
        Long currentUserId = AuthUtil.getCurrentUserId(request);
        request.setAttribute("currentRole", role);
        request.setAttribute("sectionAccess", PermissionService.resolveAccessMap(getServletContext(), role, currentUserId));

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/permisos.jsp");
        dispatcher.forward(request, response);
    }
}
