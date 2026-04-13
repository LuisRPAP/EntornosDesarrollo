package com.renacegest.servlet;

import com.renacegest.dao.InMemoryRenaceGestRepository;
import com.renacegest.dao.RenaceGestRepository;
import com.renacegest.model.Pertrecho;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/qr"})
public class QrPublicoServlet extends HttpServlet {
    private final RenaceGestRepository repository = InMemoryRenaceGestRepository.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");
        Pertrecho pertrecho = repository.findPertrechoByTokenQr(token);

        request.setAttribute("token", token);
        request.setAttribute("pertrecho", pertrecho);
        if (pertrecho != null) {
            request.setAttribute("historial", repository.findAlardesByPertrecho(pertrecho.getId()));
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/qr.jsp");
        dispatcher.forward(request, response);
    }
}
