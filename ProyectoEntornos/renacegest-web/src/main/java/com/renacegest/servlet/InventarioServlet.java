package com.renacegest.servlet;

import com.renacegest.dao.InMemoryRenaceGestRepository;
import com.renacegest.dao.RenaceGestRepository;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/inventario"})
public class InventarioServlet extends HttpServlet {
    private final RenaceGestRepository repository = InMemoryRenaceGestRepository.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AuthUtil.requireAnyRole(request, response, "Maestre", "Sargento", "Guardia")) {
            return;
        }

        cargarVista(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AuthUtil.requireAnyRole(request, response, "Maestre", "Sargento", "Guardia")) {
            return;
        }

        String accion = request.getParameter("accion");
        String role = AuthUtil.getCurrentRole(request);
        Long currentUserId = AuthUtil.getCurrentUserId(request);
        if (currentUserId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if ("Guardia".equalsIgnoreCase(role)) {
            cargarVista(request, response, "Tu rol tiene acceso de solo lectura en inventario.");
            return;
        }

        if ("Sargento".equalsIgnoreCase(role)
                && ("eliminarPertrecho".equalsIgnoreCase(accion) || "validarMasivo".equalsIgnoreCase(accion))) {
            cargarVista(request, response, "Tu rol no tiene permisos para esa accion.");
            return;
        }

        String estado;

        try {
            if ("crearSeccion".equalsIgnoreCase(accion)) {
                repository.crearSeccion(
                        request.getParameter("nombreSeccion"),
                        Long.valueOf(request.getParameter("responsableId")),
                        currentUserId
                );
                estado = "Seccion creada correctamente.";
            } else if ("crearPertrecho".equalsIgnoreCase(accion)) {
                repository.crearPertrechoManual(
                        Long.valueOf(request.getParameter("seccionId")),
                        request.getParameter("descripcionManual"),
                        Integer.parseInt(request.getParameter("integridadManual")),
                        request.getParameter("estadoIaManual"),
                        Boolean.parseBoolean(request.getParameter("disponibleManual")),
                        currentUserId
                );
                estado = "Pertrecho creado correctamente.";
            } else if ("actualizarPertrecho".equalsIgnoreCase(accion)) {
                repository.actualizarPertrecho(
                        Long.valueOf(request.getParameter("pertrechoIdEditar")),
                        Long.valueOf(request.getParameter("seccionIdEditar")),
                        request.getParameter("descripcionEditar"),
                        Integer.parseInt(request.getParameter("integridadEditar")),
                        request.getParameter("estadoIaEditar"),
                        Boolean.parseBoolean(request.getParameter("disponibleEditar")),
                        currentUserId
                );
                estado = "Pertrecho actualizado correctamente.";
            } else if ("eliminarPertrecho".equalsIgnoreCase(accion)) {
                boolean eliminado = repository.eliminarPertrecho(
                        Long.valueOf(request.getParameter("pertrechoIdEliminar")),
                        currentUserId
                );
                estado = eliminado ? "Pertrecho eliminado correctamente." : "No se encontro el pertrecho.";
            } else if ("altaIa".equalsIgnoreCase(accion)) {
                var resultado = repository.altaPertrechoConIa(request.getParameter("descripcionPertrecho"), currentUserId);
                estado = "Alta IA completada: " + resultado.getCategoriaPropuesta() + " (" + resultado.getConfianza() + "%).";
            } else if ("validarMasivo".equalsIgnoreCase(accion)) {
                String[] seleccionados = request.getParameterValues("pertrechoSeleccionado");
                List<Long> ids = new ArrayList<>();
                if (seleccionados != null) {
                    for (String item : seleccionados) {
                        ids.add(Long.valueOf(item));
                    }
                }
                boolean aplicado = repository.validarEstadoIaMasivo(
                        ids,
                        request.getParameter("estadoIaNuevo"),
                    currentUserId
                );
                estado = aplicado ? "Validacion masiva aplicada." : "No se seleccionaron pertrechos para validar.";
            } else if ("prestar".equalsIgnoreCase(accion)) {
                repository.prestarPertrecho(
                        Long.valueOf(request.getParameter("guardiaId")),
                        Long.valueOf(request.getParameter("pertrechoId")),
                    currentUserId,
                        request.getParameter("observacionesSalida")
                );
                estado = "Alarde registrado: salida de pertrecho confirmada.";
            } else if ("devolver".equalsIgnoreCase(accion)) {
                repository.registrarDevolucion(
                        Long.valueOf(request.getParameter("alardeId")),
                        Integer.parseInt(request.getParameter("integridadEntrada")),
                        request.getParameter("observacionesEntrada")
                );
                estado = "Devolucion registrada y puntos de honor recalculados.";
            } else {
                estado = "Accion no reconocida.";
            }
        } catch (Exception ex) {
            estado = ex.getMessage();
        }

        cargarVista(request, response, estado);
    }

    private void cargarVista(HttpServletRequest request, HttpServletResponse response, String estado) throws ServletException, IOException {
        String role = AuthUtil.getCurrentRole(request);
        boolean canEditInventory = role != null && ("Maestre".equalsIgnoreCase(role) || "Sargento".equalsIgnoreCase(role));
        boolean canDeletePertrecho = "Maestre".equalsIgnoreCase(role);

        request.setAttribute("estado", estado);
        request.setAttribute("guardias", repository.findAllGuardias());
        request.setAttribute("secciones", repository.findAllSecciones());
        request.setAttribute("pertrechos", repository.findAllPertrechos());
        request.setAttribute("alardes", repository.findAllAlardes());
        request.setAttribute("ticketsMaestranza", repository.getTotalTicketsMaestranza());
        request.setAttribute("currentRole", role);
        request.setAttribute("currentUserId", AuthUtil.getCurrentUserId(request));
        request.setAttribute("canEditInventory", canEditInventory);
        request.setAttribute("canDeletePertrecho", canDeletePertrecho);
        request.setAttribute("canValidateMasivo", "Maestre".equalsIgnoreCase(role));

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/inventario.jsp");
        dispatcher.forward(request, response);
    }
}
