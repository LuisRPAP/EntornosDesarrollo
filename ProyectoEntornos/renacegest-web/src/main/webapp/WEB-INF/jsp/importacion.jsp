<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>RenaceGest | Importacion Masiva</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
        </head>

        <body>
            <div class="app-shell">
                <%@ include file="_topnav.jspf" %>
                    <header class="page-head">
                        <div>
                            <p class="eyebrow">Carga de datos</p>
                            <h1>Importacion masiva CSV</h1>
                            <p class="lead">Carga muchos datos a la vez con una guia simple para evitar errores y con
                                valor
                                economico opcional en materiales.</p>
                        </div>
                        <a class="button button-secondary" href="${pageContext.request.contextPath}/home">Volver</a>
                    </header>

                    <c:if test="${not empty estado}">
                        <div class="notice notice-info">${estado}</div>
                    </c:if>

                    <section class="panel" style="margin-top: 18px;">
                        <h2>Pasos recomendados</h2>
                        <ol class="step-list">
                            <li>Elige primero el tipo de datos correcto.</li>
                            <li>Sube un archivo CSV con las columnas del formato indicado abajo.</li>
                            <li>Importa en este orden: Guardias, Secciones, Pertrechos, Grupos, Mensajes.</li>
                            <li>Si el CSV de pertrechos incluye valor economico, se guardara; si no, quedara a 0.</li>
                        </ol>
                    </section>

                    <section class="panel">
                        <h2>Subir archivo CSV</h2>
                        <p class="panel-intro">Si tienes dudas, empieza por el archivo de ejemplo de Guardias y entorno
                            PRUEBA.</p>
                        <form class="form-grid" method="post" action="${pageContext.request.contextPath}/importacion"
                            enctype="multipart/form-data">
                            <label>
                                Tipo de datos
                                <p class="field-note">Debe coincidir con la cabecera real del CSV.</p>
                                <select name="targetType" required>
                                    <option value="guardias" ${selectedType=='guardias' ? 'selected' : '' }>Guardias
                                    </option>
                                    <option value="secciones" ${selectedType=='secciones' ? 'selected' : '' }>Secciones
                                    </option>
                                    <option value="pertrechos" ${selectedType=='pertrechos' ? 'selected' : '' }>
                                        Pertrechos
                                    </option>
                                    <option value="grupos" ${selectedType=='grupos' ? 'selected' : '' }>Grupos</option>
                                    <option value="mensajes" ${selectedType=='mensajes' ? 'selected' : '' }>Mensajes
                                    </option>
                                </select>
                            </label>

                            <label>
                                Separador CSV
                                <p class="field-note">Usa Auto detectar si no estas seguro.</p>
                                <select name="delimiterMode" required>
                                    <option value="auto" ${selectedDelimiter=='auto' ? 'selected' : '' }>Auto detectar
                                    </option>
                                    <option value="semicolon" ${selectedDelimiter=='semicolon' ? 'selected' : '' }>Punto
                                        y
                                        coma (;)</option>
                                    <option value="comma" ${selectedDelimiter=='comma' ? 'selected' : '' }>Coma (,)
                                    </option>
                                    <option value="tab" ${selectedDelimiter=='tab' ? 'selected' : '' }>Tabulador
                                    </option>
                                </select>
                            </label>

                            <label>
                                Archivo CSV
                                <p class="field-note">Solo archivos .csv con texto plano.</p>
                                <input type="file" name="csvFile" accept=".csv,text/csv" required>
                            </label>

                            <button class="button button-primary" type="submit">Importar archivo</button>
                        </form>
                    </section>

                    <c:if test="${sessionScope.currentDbProfile == 'PRUEBA' && sessionScope.currentRole == 'Maestre'}">
                        <section class="panel">
                            <h2>Reset de base PRUEBA</h2>
                            <p class="lead text-danger">Borra todos los datos de PRUEBA y te permite empezar de cero. No
                                afecta a la base REAL.</p>
                            <form method="post" action="${pageContext.request.contextPath}/reset-prueba-db"
                                onsubmit="return confirm('Se borraran todos los datos de PRUEBA. ¿Continuar?');">
                                <button class="button button-secondary" type="submit">Resetear base PRUEBA</button>
                            </form>
                        </section>
                    </c:if>

                    <c:if test="${not empty importErrors}">
                        <section class="panel">
                            <h2>Errores detectados</h2>
                            <p class="panel-intro">Cada mensaje indica una fila concreta para que puedas corregir solo
                                lo
                                necesario.</p>
                            <div class="timeline">
                                <c:forEach items="${importErrors}" var="error">
                                    <article class="message-card">
                                        <p>${error}</p>
                                    </article>
                                </c:forEach>
                            </div>
                        </section>
                    </c:if>

                    <section class="panel">
                        <h2>Ejemplos descargables</h2>
                        <p class="lead">Descarga los ejemplos y cambia solo los valores, manteniendo cabeceras y orden.
                        </p>
                        <div class="timeline">
                            <article class="message-card">
                                <strong>Orden recomendado</strong>
                                <p>1. Guardias 2. Secciones 3. Pertrechos 4. Grupos 5. Mensajes</p>
                            </article>
                            <article class="message-card">
                                <strong>Archivos</strong>
                                <p><a
                                        href="${pageContext.request.contextPath}/assets/import/guardias-orden-1.csv">guardias-orden-1.csv</a>
                                </p>
                                <p><a
                                        href="${pageContext.request.contextPath}/assets/import/secciones-orden-2.csv">secciones-orden-2.csv</a>
                                </p>
                                <p><a
                                        href="${pageContext.request.contextPath}/assets/import/pertrechos-orden-3.csv">pertrechos-orden-3.csv</a>
                                </p>
                                <p><a
                                        href="${pageContext.request.contextPath}/assets/import/grupos-orden-4.csv">grupos-orden-4.csv</a>
                                </p>
                                <p><a
                                        href="${pageContext.request.contextPath}/assets/import/mensajes-orden-5.csv">mensajes-orden-5.csv</a>
                                </p>
                            </article>
                            <article class="message-card">
                                <strong>Notas</strong>
                                <p>Usa el perfil PRUEBA para validar estos datos sin tocar el entorno real.</p>
                            </article>
                        </div>
                    </section>

                    <section class="panel">
                        <h2>Plantillas de columnas</h2>
                        <p class="panel-intro">Copia estos encabezados exactamente iguales para evitar errores de
                            validacion.</p>
                        <div class="timeline">
                            <article class="message-card">
                                <strong>Guardias</strong>
                                <p>nombreReal;apodo;rango;claveAcceso;maestreActivo</p>
                                <small>Luis Rodriguez;MaestreLupo;Maestre;maestre123;true</small>
                            </article>
                            <article class="message-card">
                                <strong>Secciones</strong>
                                <p>nombreSeccion;responsableApodo</p>
                                <small>Armeria;MaestreLupo</small>
                            </article>
                            <article class="message-card">
                                <strong>Pertrechos</strong>
                                <p>seccionNombre;descripcion;integridad;estadoIa;disponible;valorEconomico</p>
                                <small>Armeria;Morrion de desfile;95;Validado;true;240</small>
                            </article>
                            <article class="message-card">
                                <strong>Grupos</strong>
                                <p>nombreGrupo;descripcion;tipo;jefeApodo</p>
                                <small>Escuadra Norte;Grupo de inventario;GrupoTrabajo;MaestreLupo</small>
                            </article>
                            <article class="message-card">
                                <strong>Mensajes</strong>
                                <p>emisorApodo;grupoNombre;contenido;broadcast</p>
                                <small>MaestreLupo;;Aviso global a toda la guardia;true</small>
                            </article>
                        </div>
                    </section>
            </div>
        </body>

        </html>