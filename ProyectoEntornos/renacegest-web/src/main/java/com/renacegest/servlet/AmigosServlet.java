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

@WebServlet(urlPatterns = {"/amigos"})
public class AmigosServlet extends HttpServlet {
    private final RenaceGestRepository repository = InMemoryRenaceGestRepository.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cargarVista(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        String estado;

        try {
            if ("etiquetar".equalsIgnoreCase(accion)) {
                boolean ok = repository.etiquetarPersonaEnFoto(
                        Long.valueOf(request.getParameter("fotoId")),
                        request.getParameter("nombrePersona"),
                        request.getParameter("etiquetadoPor"),
                        request.getParameter("usuarioRedEtiqueta")
                );
                estado = ok ? "Persona etiquetada correctamente." : "No se pudo crear la etiqueta (foto no valida o persona ya etiquetada).";
            } else if ("valorar".equalsIgnoreCase(accion)) {
                boolean ok = repository.valorarFotoPublica(
                        Long.valueOf(request.getParameter("fotoId")),
                        Integer.parseInt(request.getParameter("puntuacion")),
                        request.getParameter("comentario"),
                        request.getParameter("visitante"),
                        request.getParameter("usuarioRedValoracion")
                );
                estado = ok ? "Gracias por tu valoracion." : "No se pudo registrar la valoracion.";
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
        request.setAttribute("pertrechos", repository.findPertrechosPublicos());
        request.setAttribute("fotosGaleria", repository.findGaleriaPublica());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/amigos.jsp");
        dispatcher.forward(request, response);
    }
}
