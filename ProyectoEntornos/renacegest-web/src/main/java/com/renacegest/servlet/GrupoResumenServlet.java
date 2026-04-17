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

@WebServlet(urlPatterns = {"/grupos-resumen"})
public class GrupoResumenServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PermissionService.requireSectionAccess(request, response, PermissionService.SECTION_GRUPOS_RESUMEN, "Maestre", "Sargento", "Guardia")) {
            return;
        }

        RenaceGestRepository repository = SessionRepositoryResolver.resolve(request);
        Map<Long, List<MiembroGrupo>> miembrosPorGrupo = new HashMap<>();
        repository.findAllGrupos().forEach(grupo -> miembrosPorGrupo.put(grupo.getId(), repository.findMiembrosByGrupo(grupo.getId())));

        String role = AuthUtil.getCurrentRole(request);
        Long currentUserId = AuthUtil.getCurrentUserId(request);

        request.setAttribute("grupos", repository.findAllGrupos());
        request.setAttribute("miembrosPorGrupo", miembrosPorGrupo);
        request.setAttribute("currentRole", role);
        request.setAttribute("sectionAccess", PermissionService.resolveAccessMap(getServletContext(), role, currentUserId));

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/grupos-resumen.jsp");
        dispatcher.forward(request, response);
    }
}
