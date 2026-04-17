package com.renacegest.servlet;

import com.renacegest.dao.RenaceGestRepository;
import com.renacegest.model.Guardia;
import com.renacegest.model.GrupoMision;
import com.renacegest.model.SeccionMaestranza;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/importacion"})
@MultipartConfig
public class ImportacionMasivaServlet extends HttpServlet {
    private static final String TYPE_GUARDIAS = "guardias";
    private static final String TYPE_SECCIONES = "secciones";
    private static final String TYPE_PERTRECHOS = "pertrechos";
    private static final String TYPE_GRUPOS = "grupos";
    private static final String TYPE_MENSAJES = "mensajes";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PermissionService.requireSectionAccess(request, response, PermissionService.SECTION_IMPORTACION, "Maestre", "Sargento")) {
            return;
        }
        applyResetMessageIfPresent(request);
        request.setAttribute("selectedType", TYPE_GUARDIAS);
        request.setAttribute("selectedDelimiter", "auto");
        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PermissionService.requireSectionAccess(request, response, PermissionService.SECTION_IMPORTACION, "Maestre", "Sargento")) {
            return;
        }

        Long currentUserId = AuthUtil.getCurrentUserId(request);
        if (currentUserId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String targetType = normalizeType(request.getParameter("targetType"));
        String delimiterMode = normalizeDelimiterMode(request.getParameter("delimiterMode"));
        request.setAttribute("selectedType", targetType);
        request.setAttribute("selectedDelimiter", delimiterMode);

        Part csvPart = request.getPart("csvFile");
        if (csvPart == null || csvPart.getSize() == 0) {
            request.setAttribute("estado", "Debes adjuntar un archivo CSV.");
            forward(request, response);
            return;
        }

        List<String> rawLines = readLines(csvPart);
        if (rawLines.isEmpty()) {
            request.setAttribute("estado", "El archivo CSV esta vacio.");
            forward(request, response);
            return;
        }

        char delimiter = resolveDelimiter(rawLines, delimiterMode);
        String detectedType = detectTypeFromHeader(rawLines, delimiter);
        if (detectedType != null && !detectedType.equals(targetType)) {
            request.setAttribute("estado", "El CSV parece ser de tipo '" + detectedType + "'. Selecciona ese tipo e intenta de nuevo.");
            forward(request, response);
            return;
        }

        RenaceGestRepository repository = SessionRepositoryResolver.resolve(request);
        ImportSummary summary = importByType(repository, targetType, rawLines, currentUserId, delimiter);

        request.setAttribute("estado", String.format(
                "Importacion finalizada: %d insertadas, %d omitidas, %d con error.",
                summary.inserted,
                summary.skipped,
                summary.errors.size()
        ));
        request.setAttribute("importErrors", summary.errors);
        request.setAttribute("importInserted", summary.inserted);
        request.setAttribute("importSkipped", summary.skipped);
        forward(request, response);
    }

    private ImportSummary importByType(RenaceGestRepository repository, String targetType, List<String> rawLines, Long currentUserId, char delimiter) {
        List<List<String>> rows = parseRows(rawLines, delimiter);
        rows = stripHeaderIfPresent(rows, targetType);

        ImportSummary summary = new ImportSummary();
        int logicalLine = 1;
        for (List<String> row : rows) {
            logicalLine++;
            if (isRowEmpty(row)) {
                summary.skipped++;
                continue;
            }

            try {
                if (TYPE_GUARDIAS.equals(targetType)) {
                    importGuardiaRow(repository, row, currentUserId);
                } else if (TYPE_SECCIONES.equals(targetType)) {
                    importSeccionRow(repository, row, currentUserId);
                } else if (TYPE_PERTRECHOS.equals(targetType)) {
                    importPertrechoRow(repository, row, currentUserId);
                } else if (TYPE_GRUPOS.equals(targetType)) {
                    importGrupoRow(repository, row, currentUserId);
                } else if (TYPE_MENSAJES.equals(targetType)) {
                    importMensajeRow(repository, row);
                } else {
                    throw new IllegalArgumentException("Tipo de importacion no soportado: " + targetType);
                }
                summary.inserted++;
            } catch (Exception ex) {
                summary.errors.add("Linea " + logicalLine + ": " + humanizeImportError(ex));
            }
        }

        return summary;
    }

    private void importGuardiaRow(RenaceGestRepository repository, List<String> row, Long currentUserId) {
        String nombreReal = required(row, 0, "nombreReal");
        String apodo = required(row, 1, "apodo");
        String rango = value(row, 2, "Guardia");
        String claveAcceso = required(row, 3, "claveAcceso");
        boolean maestreActivo = parseBoolean(value(row, 4, "false"));
        repository.crearGuardia(nombreReal, apodo, rango, claveAcceso, maestreActivo, currentUserId);
    }

    private void importSeccionRow(RenaceGestRepository repository, List<String> row, Long currentUserId) {
        String nombreSeccion = required(row, 0, "nombreSeccion");
        String responsableApodo = required(row, 1, "responsableApodo");

        Long responsableId = findGuardiaIdByApodo(repository.findAllGuardias(), responsableApodo);
        if (responsableId == null) {
            throw new IllegalArgumentException("No existe responsable con apodo '" + responsableApodo + "'.");
        }

        repository.crearSeccion(nombreSeccion, responsableId, currentUserId);
    }

    private void importPertrechoRow(RenaceGestRepository repository, List<String> row, Long currentUserId) {
        String seccionNombre = required(row, 0, "seccionNombre");
        String descripcion = required(row, 1, "descripcion");
        int integridad = parseInt(value(row, 2, "100"), "integridad");
        String estadoIa = value(row, 3, "Pendiente");
        boolean disponible = parseBoolean(value(row, 4, "true"));
        double valorEconomico = parseDouble(value(row, 5, "0"));

        Long seccionId = findSeccionIdByNombre(repository.findAllSecciones(), seccionNombre);
        if (seccionId == null) {
            throw new IllegalArgumentException("No existe seccion con nombre '" + seccionNombre + "'.");
        }

        repository.crearPertrechoManual(seccionId, descripcion, integridad, estadoIa, disponible, valorEconomico, currentUserId);
    }

    private void importGrupoRow(RenaceGestRepository repository, List<String> row, Long currentUserId) {
        String nombreGrupo = required(row, 0, "nombreGrupo");
        String descripcion = required(row, 1, "descripcion");
        String tipo = value(row, 2, "GrupoTrabajo");
        String jefeApodo = required(row, 3, "jefeApodo");

        Long jefeId = findGuardiaIdByApodo(repository.findAllGuardias(), jefeApodo);
        if (jefeId == null) {
            throw new IllegalArgumentException("No existe jefe con apodo '" + jefeApodo + "'.");
        }

        repository.crearGrupo(nombreGrupo, descripcion, tipo, jefeId, currentUserId);
    }

    private void importMensajeRow(RenaceGestRepository repository, List<String> row) {
        String emisorApodo = required(row, 0, "emisorApodo");
        String grupoNombre = value(row, 1, "");
        String contenido = required(row, 2, "contenido");
        boolean broadcast = parseBoolean(value(row, 3, "false"));

        Map<String, Long> guardiasByApodo = mapGuardiasByApodo(repository.findAllGuardias());
        Map<String, Long> gruposByNombre = mapGruposByNombre(repository.findAllGrupos());

        Long emisorId = guardiasByApodo.get(emisorApodo.toLowerCase());
        if (emisorId == null) {
            throw new IllegalArgumentException("No existe emisor con apodo '" + emisorApodo + "'.");
        }

        Long grupoId = null;
        if (!grupoNombre.isBlank()) {
            grupoId = gruposByNombre.get(grupoNombre.toLowerCase());
            if (grupoId == null) {
                throw new IllegalArgumentException("No existe grupo con nombre '" + grupoNombre + "'.");
            }
        }

        repository.enviarMensaje(emisorId, grupoId, contenido, broadcast);
    }

    private Long findGuardiaIdByApodo(List<Guardia> guardias, String apodo) {
        for (Guardia guardia : guardias) {
            if (guardia.getApodo().equalsIgnoreCase(apodo)) {
                return guardia.getId();
            }
        }
        return null;
    }

    private Long findSeccionIdByNombre(List<SeccionMaestranza> secciones, String nombreSeccion) {
        for (SeccionMaestranza seccion : secciones) {
            if (seccion.getNombreSeccion().equalsIgnoreCase(nombreSeccion)) {
                return seccion.getId();
            }
        }
        return null;
    }

    private Map<String, Long> mapGuardiasByApodo(List<Guardia> guardias) {
        Map<String, Long> out = new HashMap<>();
        for (Guardia guardia : guardias) {
            out.put(guardia.getApodo().toLowerCase(), guardia.getId());
        }
        return out;
    }

    private Map<String, Long> mapGruposByNombre(List<GrupoMision> grupos) {
        Map<String, Long> out = new HashMap<>();
        for (GrupoMision grupo : grupos) {
            out.put(grupo.getNombreGrupo().toLowerCase(), grupo.getId());
        }
        return out;
    }

    private List<String> readLines(Part csvPart) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvPart.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }
                lines.add(line);
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("No se pudo leer el CSV: " + ex.getMessage());
        }
        return lines;
    }

    private List<List<String>> parseRows(List<String> lines, char delimiter) {
        List<List<String>> rows = new ArrayList<>();
        for (String line : lines) {
            rows.add(parseCsvLine(line, delimiter));
        }
        return rows;
    }

    private List<String> parseCsvLine(String line, char delimiter) {
        List<String> cols = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == delimiter && !inQuotes) {
                cols.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        cols.add(current.toString().trim());
        return cols;
    }

    private List<List<String>> stripHeaderIfPresent(List<List<String>> rows, String targetType) {
        if (rows.isEmpty()) {
            return rows;
        }

        List<String> first = rows.get(0);
        if (first.isEmpty()) {
            return rows;
        }

        String firstCol = first.get(0).toLowerCase();
        boolean isHeader = (TYPE_GUARDIAS.equals(targetType) && (firstCol.contains("nombrereal") || firstCol.contains("nombre_real")))
                || (TYPE_SECCIONES.equals(targetType) && firstCol.contains("nombreseccion"))
                || (TYPE_PERTRECHOS.equals(targetType) && firstCol.contains("seccionnombre"))
                || (TYPE_GRUPOS.equals(targetType) && firstCol.contains("nombregrupo"))
                || (TYPE_MENSAJES.equals(targetType) && firstCol.contains("emisorapodo"));

        if (isHeader) {
            return rows.subList(1, rows.size());
        }
        return rows;
    }

    private String detectTypeFromHeader(List<String> lines, char delimiter) {
        if (lines == null || lines.isEmpty()) {
            return null;
        }

        List<String> cols = parseCsvLine(lines.get(0), delimiter);
        if (cols.isEmpty()) {
            return null;
        }

        String first = normalizeHeaderToken(cols.get(0));
        if ("nombrereal".equals(first) || "nombre_real".equals(first)) {
            return TYPE_GUARDIAS;
        }
        if ("nombreseccion".equals(first)) {
            return TYPE_SECCIONES;
        }
        if ("seccionnombre".equals(first)) {
            return TYPE_PERTRECHOS;
        }
        if ("nombregrupo".equals(first)) {
            return TYPE_GRUPOS;
        }
        if ("emisorapodo".equals(first)) {
            return TYPE_MENSAJES;
        }

        return null;
    }

    private String normalizeHeaderToken(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase().replace(" ", "");
    }

    private String humanizeImportError(Exception ex) {
        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }

        String message = root.getMessage();
        if (message == null || message.isBlank()) {
            message = ex.getMessage();
        }
        if (message == null || message.isBlank()) {
            return "Error desconocido durante la importacion.";
        }

        String lower = message.toLowerCase();
        if (lower.contains("duplicate entry")) {
            return "Registro duplicado: ese valor ya existe en la base de datos.";
        }
        if (lower.contains("solo el maestre puede enviar mensajes globales")) {
            return "Broadcast no permitido: solo el Maestre puede enviar mensajes globales.";
        }

        return message;
    }

    private char resolveDelimiter(List<String> lines, String mode) {
        if ("semicolon".equals(mode)) {
            return ';';
        }
        if ("tab".equals(mode)) {
            return '\t';
        }
        if ("comma".equals(mode)) {
            return ',';
        }

        String sample = lines.get(0);
        int semicolons = count(sample, ';');
        int commas = count(sample, ',');
        int tabs = count(sample, '\t');
        if (tabs >= semicolons && tabs >= commas) {
            return '\t';
        }
        return semicolons >= commas ? ';' : ',';
    }

    private int count(String source, char needle) {
        int count = 0;
        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) == needle) {
                count++;
            }
        }
        return count;
    }

    private String required(List<String> row, int index, String field) {
        String value = value(row, index, "");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Campo obligatorio vacio: " + field);
        }
        return value;
    }

    private String value(List<String> row, int index, String defaultValue) {
        if (index < 0 || index >= row.size()) {
            return defaultValue;
        }
        String value = row.get(index);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private int parseInt(String raw, String field) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Valor numerico invalido para " + field + ": " + raw);
        }
    }

    private boolean parseBoolean(String raw) {
        String value = raw == null ? "" : raw.trim().toLowerCase();
        return "true".equals(value)
                || "1".equals(value)
                || "si".equals(value)
                || "yes".equals(value)
                || "y".equals(value);
    }

    private double parseDouble(String raw) {
        if (raw == null || raw.isBlank()) {
            return 0.0;
        }

        try {
            return Math.max(0.0, Double.parseDouble(raw.trim().replace(',', '.')));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Valor economico invalido: " + raw);
        }
    }

    private boolean isRowEmpty(List<String> row) {
        for (String col : row) {
            if (col != null && !col.isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String normalizeType(String raw) {
        if (TYPE_SECCIONES.equals(raw) || TYPE_PERTRECHOS.equals(raw) || TYPE_GRUPOS.equals(raw) || TYPE_MENSAJES.equals(raw)) {
            return raw;
        }
        return TYPE_GUARDIAS;
    }

    private String normalizeDelimiterMode(String raw) {
        if ("semicolon".equals(raw) || "comma".equals(raw) || "tab".equals(raw)) {
            return raw;
        }
        return "auto";
    }

    private void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/importacion.jsp");
        dispatcher.forward(request, response);
    }

    private void applyResetMessageIfPresent(HttpServletRequest request) {
        String reset = request.getParameter("reset");
        if ("ok".equalsIgnoreCase(reset)) {
            request.setAttribute("estado", "Base PRUEBA reiniciada correctamente. Ya puedes importar desde cero.");
        } else if ("forbidden".equalsIgnoreCase(reset)) {
            request.setAttribute("estado", "Reset bloqueado: esta accion solo se permite en el perfil PRUEBA.");
        } else if ("error".equalsIgnoreCase(reset)) {
            request.setAttribute("estado", "No se pudo completar el reset de PRUEBA. Revisa la conexion y los permisos MySQL.");
        }
    }

    private static final class ImportSummary {
        private int inserted;
        private int skipped;
        private final List<String> errors = new ArrayList<>();
    }
}
