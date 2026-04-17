package com.renacegest.servlet;

import com.renacegest.dao.RenaceGestRepository;
import com.renacegest.model.Guardia;
import com.renacegest.model.GrupoMision;
import com.renacegest.model.MensajeComunicacion;
import com.renacegest.model.Pertrecho;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@WebServlet(urlPatterns = {"/listados"})
public class ListadosServlet extends HttpServlet {
    private static final String TYPE_GUARDIAS = "guardias";
    private static final String TYPE_GRUPOS = "grupos";
    private static final String TYPE_PERTRECHOS = "pertrechos";
    private static final String TYPE_MENSAJES = "mensajes";

    private static final Pattern CONDITION_PATTERN = Pattern.compile("^([a-zA-Z0-9_]+)\\s*(>=|<=|!=|\\^=|\\$=|~|=|>|<|:)\\s*(.+)$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PermissionService.requireSectionAccess(request, response, PermissionService.SECTION_LISTADOS, "Maestre", "Sargento", "Guardia")) {
            return;
        }

        String tipo = normalizeType(request.getParameter("tipo"));
        String filtro = normalizeFilter(request.getParameter("filtro"));

        RenaceGestRepository repository = SessionRepositoryResolver.resolve(request);
        List<Map<String, String>> rows = buildRows(repository, tipo);
        List<Map<String, String>> filteredRows = rows;
        String filterError = null;
        if (!filtro.isBlank()) {
            try {
                filteredRows = applyFilter(rows, filtro);
            } catch (IllegalArgumentException ex) {
                filterError = ex.getMessage();
            }
        }

        request.setAttribute("selectedTipo", tipo);
        request.setAttribute("filtro", filtro);
        request.setAttribute("availableFields", availableFields(tipo));
        request.setAttribute("resultados", filteredRows);
        request.setAttribute("totalOriginal", rows.size());
        request.setAttribute("totalFiltrado", filteredRows.size());
        request.setAttribute("filterError", filterError);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/listados.jsp");
        dispatcher.forward(request, response);
    }

    private List<Map<String, String>> buildRows(RenaceGestRepository repository, String tipo) {
        List<Map<String, String>> out = new ArrayList<>();
        if (TYPE_GRUPOS.equals(tipo)) {
            for (GrupoMision g : repository.findAllGrupos()) {
                Map<String, String> row = new LinkedHashMap<>();
                row.put("id", String.valueOf(g.getId()));
                row.put("nombreGrupo", safe(g.getNombreGrupo()));
                row.put("tipo", safe(g.getTipo()));
                row.put("jefeEquipo", safe(g.getJefeEquipo()));
                row.put("creadoPor", safe(g.getCreadoPor()));
                row.put("activo", String.valueOf(g.isActivo()));
                row.put("descripcion", safe(g.getDescripcion()));
                out.add(row);
            }
            return out;
        }

        if (TYPE_PERTRECHOS.equals(tipo)) {
            for (Pertrecho p : repository.findAllPertrechos()) {
                Map<String, String> row = new LinkedHashMap<>();
                row.put("id", String.valueOf(p.getId()));
                row.put("seccion", safe(p.getSeccionNombre()));
                row.put("descripcion", safe(p.getDescripcion()));
                row.put("integridad", String.valueOf(p.getIntegridad()));
                row.put("estadoIa", safe(p.getEstadoIa()));
                row.put("disponible", String.valueOf(p.isDisponible()));
                row.put("tokenQr", safe(p.getTokenQr()));
                out.add(row);
            }
            return out;
        }

        if (TYPE_MENSAJES.equals(tipo)) {
            for (MensajeComunicacion m : repository.findAllMensajes()) {
                Map<String, String> row = new LinkedHashMap<>();
                row.put("id", String.valueOf(m.getId()));
                row.put("emisor", safe(m.getEmisorApodo()));
                row.put("grupo", safe(m.getGrupoNombre()));
                row.put("broadcast", String.valueOf(m.isBroadcast()));
                row.put("visibleParaTodos", String.valueOf(m.isVisibleParaTodos()));
                row.put("fecha", safe(m.getFechaEnvio()));
                row.put("contenido", safe(m.getContenido()));
                out.add(row);
            }
            return out;
        }

        for (Guardia g : repository.findAllGuardias()) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("id", String.valueOf(g.getId()));
            row.put("nombreReal", safe(g.getNombreReal()));
            row.put("apodo", safe(g.getApodo()));
            row.put("rango", safe(g.getRango()));
            row.put("puntosGracia", String.valueOf(g.getPuntosGracia()));
            row.put("estadoHonor", safe(g.getEstadoHonor()));
            row.put("maestreActivo", String.valueOf(g.isMaestreActivo()));
            out.add(row);
        }
        return out;
    }

    private List<Map<String, String>> applyFilter(List<Map<String, String>> rows, String filterExpression) {
        if (filterExpression.isBlank()) {
            return rows;
        }

        RowMatcher matcher;
        try {
            matcher = new ExpressionParser(filterExpression).parse();
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Filtro invalido: " + ex.getMessage());
        }

        List<Map<String, String>> filtered = new ArrayList<>();
        for (Map<String, String> row : rows) {
            if (matcher.matches(row)) {
                filtered.add(row);
            }
        }
        return filtered;
    }

    private boolean matchesTerm(Map<String, String> row, String rawTerm) {
        String term = normalizeFilter(rawTerm);
        if (term.isBlank()) {
            return true;
        }

        Matcher conditionMatcher = CONDITION_PATTERN.matcher(term);
        if (!conditionMatcher.matches()) {
            String normalized = normalizeText(term);
            for (String value : row.values()) {
                if (normalizeText(value).contains(normalized)) {
                    return true;
                }
            }
            return false;
        }

        String field = conditionMatcher.group(1);
        String operator = conditionMatcher.group(2);
        String expectedRaw = stripQuotes(conditionMatcher.group(3));

        String actual = row.get(field);
        if (actual == null && ("all".equalsIgnoreCase(field) || "_all".equalsIgnoreCase(field))) {
            actual = joinValues(row);
        }
        if (actual == null) {
            return false;
        }

        if ("^=".equals(operator)) {
            return normalizeText(actual).startsWith(normalizeText(expectedRaw));
        }
        if ("$=".equals(operator)) {
            return normalizeText(actual).endsWith(normalizeText(expectedRaw));
        }
        if ("~".equals(operator)) {
            try {
                return Pattern.compile(expectedRaw, Pattern.CASE_INSENSITIVE).matcher(actual).find();
            } catch (PatternSyntaxException ex) {
                return false;
            }
        }
        if (":".equals(operator)) {
            return normalizeText(actual).contains(normalizeText(expectedRaw));
        }
        if ("=".equals(operator)) {
            return normalizeText(actual).equals(normalizeText(expectedRaw));
        }
        if ("!=".equals(operator)) {
            return !normalizeText(actual).equals(normalizeText(expectedRaw));
        }

        Double actualNumber = parseNumber(actual);
        Double expectedNumber = parseNumber(expectedRaw);
        if (actualNumber == null || expectedNumber == null) {
            return false;
        }

        if (">".equals(operator)) {
            return actualNumber > expectedNumber;
        }
        if (">=".equals(operator)) {
            return actualNumber >= expectedNumber;
        }
        if ("<".equals(operator)) {
            return actualNumber < expectedNumber;
        }
        return actualNumber <= expectedNumber;
    }

    private String joinValues(Map<String, String> row) {
        StringBuilder all = new StringBuilder();
        for (String value : row.values()) {
            if (value == null || value.isBlank()) {
                continue;
            }
            if (all.length() > 0) {
                all.append(' ');
            }
            all.append(value);
        }
        return all.toString();
    }

    private String availableFields(String tipo) {
        if (TYPE_GRUPOS.equals(tipo)) {
            return "id, nombreGrupo, tipo, jefeEquipo, creadoPor, activo, descripcion, all";
        }
        if (TYPE_PERTRECHOS.equals(tipo)) {
            return "id, seccion, descripcion, integridad, estadoIa, disponible, tokenQr, all";
        }
        if (TYPE_MENSAJES.equals(tipo)) {
            return "id, emisor, grupo, broadcast, visibleParaTodos, fecha, contenido, all";
        }
        return "id, nombreReal, apodo, rango, puntosGracia, estadoHonor, maestreActivo, all";
    }

    private String normalizeType(String raw) {
        List<String> supported = Arrays.asList(TYPE_GUARDIAS, TYPE_GRUPOS, TYPE_PERTRECHOS, TYPE_MENSAJES);
        if (raw != null && supported.contains(raw)) {
            return raw;
        }
        return TYPE_GUARDIAS;
    }

    private String normalizeFilter(String raw) {
        return raw == null ? "" : raw.trim();
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String stripQuotes(String value) {
        String out = value == null ? "" : value.trim();
        if ((out.startsWith("\"") && out.endsWith("\"")) || (out.startsWith("'") && out.endsWith("'"))) {
            return out.substring(1, out.length() - 1).trim();
        }
        return out;
    }

    private Double parseNumber(String value) {
        try {
            return Double.parseDouble(value.trim().replace(',', '.'));
        } catch (Exception ex) {
            return null;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    @FunctionalInterface
    private interface RowMatcher {
        boolean matches(Map<String, String> row);
    }

    private final class ExpressionParser {
        private final String source;
        private int index;

        private ExpressionParser(String source) {
            this.source = source == null ? "" : source;
            this.index = 0;
        }

        private RowMatcher parse() {
            RowMatcher matcher = parseOr();
            skipWhitespace();
            if (index < source.length()) {
                throw new IllegalArgumentException("Expresion no valida");
            }
            return matcher;
        }

        private RowMatcher parseOr() {
            RowMatcher left = parseAnd();
            while (true) {
                skipWhitespace();
                if (!consumeKeyword("OR")) {
                    break;
                }
                RowMatcher right = parseAnd();
                RowMatcher previousLeft = left;
                left = row -> previousLeft.matches(row) || right.matches(row);
            }
            return left;
        }

        private RowMatcher parseAnd() {
            RowMatcher left = parsePrimary();
            while (true) {
                skipWhitespace();
                if (!consumeKeyword("AND")) {
                    break;
                }
                RowMatcher right = parsePrimary();
                RowMatcher previousLeft = left;
                left = row -> previousLeft.matches(row) && right.matches(row);
            }
            return left;
        }

        private RowMatcher parsePrimary() {
            skipWhitespace();
            if (consumeChar('!')) {
                RowMatcher inner = parsePrimary();
                return row -> !inner.matches(row);
            }
            if (consumeKeyword("NOT")) {
                RowMatcher inner = parsePrimary();
                return row -> !inner.matches(row);
            }

            if (consumeChar('(')) {
                RowMatcher inner = parseOr();
                skipWhitespace();
                if (!consumeChar(')')) {
                    throw new IllegalArgumentException("Falta parentesis de cierre");
                }
                return inner;
            }

            String term = readTerm();
            if (term.isBlank()) {
                throw new IllegalArgumentException("Condicion vacia");
            }
            return row -> matchesTerm(row, term);
        }

        private String readTerm() {
            int start = index;
            boolean inSingleQuotes = false;
            boolean inDoubleQuotes = false;

            while (index < source.length()) {
                char c = source.charAt(index);

                if (c == '\'' && !inDoubleQuotes) {
                    inSingleQuotes = !inSingleQuotes;
                    index++;
                    continue;
                }
                if (c == '"' && !inSingleQuotes) {
                    inDoubleQuotes = !inDoubleQuotes;
                    index++;
                    continue;
                }

                if (!inSingleQuotes && !inDoubleQuotes) {
                    if (c == ')') {
                        break;
                    }
                    if (isKeywordAt("AND") || isKeywordAt("OR")) {
                        break;
                    }
                }

                index++;
            }

            return source.substring(start, index).trim();
        }

        private void skipWhitespace() {
            while (index < source.length() && Character.isWhitespace(source.charAt(index))) {
                index++;
            }
        }

        private boolean consumeChar(char expected) {
            if (index < source.length() && source.charAt(index) == expected) {
                index++;
                return true;
            }
            return false;
        }

        private boolean consumeKeyword(String keyword) {
            if (!isKeywordAt(keyword)) {
                return false;
            }
            index += keyword.length();
            return true;
        }

        private boolean isKeywordAt(String keyword) {
            int end = index + keyword.length();
            if (end > source.length()) {
                return false;
            }
            if (!source.regionMatches(true, index, keyword, 0, keyword.length())) {
                return false;
            }
            return isConnectorBoundary(index - 1) && isConnectorBoundary(end);
        }

        private boolean isConnectorBoundary(int pos) {
            if (pos < 0 || pos >= source.length()) {
                return true;
            }
            char c = source.charAt(pos);
            return Character.isWhitespace(c) || c == '(' || c == ')';
        }
    }
}
