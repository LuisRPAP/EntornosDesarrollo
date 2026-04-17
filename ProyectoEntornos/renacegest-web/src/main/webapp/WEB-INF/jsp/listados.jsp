<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>RenaceGest | Listados Avanzados</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
        </head>

        <body>
            <div class="app-shell app-shell-fluid">
                <%@ include file="_topnav.jspf" %>
                    <header class="page-head">
                        <div>
                            <p class="eyebrow">Consulta avanzada</p>
                            <h1>Listados con buscador</h1>
                            <p class="lead">Filtra por multiples condiciones y combina reglas con AND/OR.</p>
                        </div>
                        <a class="button button-secondary" href="${pageContext.request.contextPath}/home">Volver</a>
                    </header>

                    <section class="panel">
                        <h2>Configuracion de busqueda</h2>
                        <form class="form-grid" method="get" action="${pageContext.request.contextPath}/listados">
                            <label>
                                Tipo de listado
                                <select id="tipoListado" name="tipo" required>
                                    <option value="guardias" ${selectedTipo=='guardias' ? 'selected' : '' }>Guardias
                                    </option>
                                    <option value="grupos" ${selectedTipo=='grupos' ? 'selected' : '' }>Grupos</option>
                                    <option value="pertrechos" ${selectedTipo=='pertrechos' ? 'selected' : '' }>
                                        Pertrechos</option>
                                    <option value="mensajes" ${selectedTipo=='mensajes' ? 'selected' : '' }>Mensajes
                                    </option>
                                </select>
                            </label>
                            <label>
                                Expresion de filtro
                                <input id="filtroInput" type="text" name="filtro" value="${filtro}"
                                    list="filtroSugerencias"
                                    placeholder="Ej: rango:Maestre AND puntosGracia>=90 OR estadoHonor=Activo"
                                    autocomplete="off">
                            </label>
                            <datalist id="filtroSugerencias"></datalist>
                            <div class="button-row">
                                <button type="button" class="button button-secondary"
                                    data-insert-token="AND">AND</button>
                                <button type="button" class="button button-secondary" data-insert-token="OR">OR</button>
                                <button type="button" class="button button-secondary"
                                    data-insert-token="NOT">NOT</button>
                                <button type="button" class="button button-secondary" data-insert-token="(">(</button>
                                <button type="button" class="button button-secondary" data-insert-token=")">)</button>
                            </div>
                            <small id="helperFieldsText"></small>
                            <small id="helperExamplesText"></small>
                            <button class="button button-primary" type="submit">Buscar</button>
                        </form>
                        <c:if test="${not empty filterError}">
                            <div class="notice">
                                <p><strong>Error de filtro:</strong> ${filterError}</p>
                            </div>
                        </c:if>
                        <div class="notice">
                            <p><strong>Campos disponibles:</strong> ${availableFields}</p>
                            <p><strong>Operadores:</strong> <code>:</code> contiene, <code>=</code> igual,
                                <code>!=</code> distinto, <code>&gt; &gt;= &lt; &lt;=</code> numericos, <code>^=</code>
                                empieza por, <code>$=</code> termina en, <code>~</code> regex.</p>
                            <p><strong>Conectores:</strong> usa <code>AND</code> y <code>OR</code> (mayusculas o
                                minusculas).</p>
                            <p><strong>Parentesis:</strong> soportado, por ejemplo
                                <code>rango:Maestre AND (estadoHonor=Activo OR puntosGracia&gt;90)</code>.</p>
                            <p><strong>Precedencia:</strong> <code>AND</code> se evalua antes que <code>OR</code> cuando
                                no se usan parentesis.</p>
                            <p><strong>Negacion:</strong> usa <code>NOT</code> o <code>!</code>, por ejemplo
                                <code>NOT estadoHonor=Infame</code>.</p>
                            <p><strong>Busqueda global:</strong> usa <code>all:</code> para buscar en todos los campos,
                                por ejemplo <code>all:maestre AND !all:infame</code>.</p>
                        </div>
                    </section>

                    <section class="panel">
                        <div class="panel-head">
                            <h2>Resultados</h2>
                            <span class="badge badge-neutral">${totalFiltrado} de ${totalOriginal}</span>
                        </div>

                        <c:if test="${empty resultados}">
                            <div class="notice">No hay resultados con ese filtro.</div>
                        </c:if>

                        <c:if test="${not empty resultados}">
                            <div class="timeline">
                                <c:forEach items="${resultados}" var="fila">
                                    <article class="message-card">
                                        <div class="list">
                                            <c:forEach items="${fila}" var="col">
                                                <div class="list-item">
                                                    <strong>${col.key}</strong>
                                                    <span>${col.value}</span>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </article>
                                </c:forEach>
                            </div>
                        </c:if>
                    </section>
            </div>
            <script>
                (function () {
                    var tipoSelect = document.getElementById('tipoListado');
                    var filtroInput = document.getElementById('filtroInput');
                    var datalist = document.getElementById('filtroSugerencias');
                    var fieldsText = document.getElementById('helperFieldsText');
                    var examplesText = document.getElementById('helperExamplesText');

                    if (!tipoSelect || !filtroInput || !datalist) {
                        return;
                    }

                    var operators = [':', '=', '!=', '>', '>=', '<', '<=', '^=', '$=', '~'];
                    var fieldsByType = {
                        guardias: ['id', 'nombreReal', 'apodo', 'rango', 'puntosGracia', 'estadoHonor', 'maestreActivo', 'all'],
                        grupos: ['id', 'nombreGrupo', 'tipo', 'jefeEquipo', 'creadoPor', 'activo', 'descripcion', 'all'],
                        pertrechos: ['id', 'seccion', 'descripcion', 'integridad', 'estadoIa', 'disponible', 'tokenQr', 'all'],
                        mensajes: ['id', 'emisor', 'grupo', 'broadcast', 'visibleParaTodos', 'fecha', 'contenido', 'all']
                    };

                    var examplesByType = {
                        guardias: 'rango:Maestre AND (estadoHonor=Activo OR puntosGracia>=90)',
                        grupos: 'tipo:GrupoTrabajo AND !descripcion~(baja|cerrado)',
                        pertrechos: 'seccion^=Arm AND (integridad<80 OR estadoIa:Dudoso)',
                        mensajes: '(broadcast=true OR grupo:Escuadra) AND !contenido~(spam|prueba)'
                    };

                    function addOption(value) {
                        var option = document.createElement('option');
                        option.value = value;
                        datalist.appendChild(option);
                    }

                    function refreshHelper() {
                        var tipo = tipoSelect.value || 'guardias';
                        var fields = fieldsByType[tipo] || fieldsByType.guardias;
                        var example = examplesByType[tipo] || examplesByType.guardias;

                        datalist.innerHTML = '';
                        fields.forEach(function (field) {
                            operators.forEach(function (operator) {
                                addOption(field + operator);
                            });
                        });

                        addOption('NOT ');
                        addOption('!');
                        addOption('AND ');
                        addOption('OR ');
                        addOption('(');
                        addOption(')');

                        fieldsText.textContent = 'Sugerencias de campos (' + tipo + '): ' + fields.join(', ');
                        examplesText.textContent = 'Ejemplo rapido: ' + example;
                    }

                    function insertToken(token) {
                        var start = filtroInput.selectionStart || 0;
                        var end = filtroInput.selectionEnd || 0;
                        var before = filtroInput.value.substring(0, start);
                        var after = filtroInput.value.substring(end);
                        var withSpaces = before.length > 0 && !before.endsWith(' ') ? ' ' + token : token;
                        withSpaces = withSpaces + (after.length > 0 && !after.startsWith(' ') ? ' ' : '');
                        filtroInput.value = before + withSpaces + after;

                        var cursor = (before + withSpaces).length;
                        filtroInput.setSelectionRange(cursor, cursor);
                        filtroInput.focus();
                    }

                    tipoSelect.addEventListener('change', refreshHelper);
                    document.querySelectorAll('[data-insert-token]').forEach(function (button) {
                        button.addEventListener('click', function () {
                            insertToken(button.getAttribute('data-insert-token'));
                        });
                    });

                    refreshHelper();
                })();
            </script>
        </body>

        </html>