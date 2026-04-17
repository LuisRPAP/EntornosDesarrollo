<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>RenaceGest | Inventario y prendas</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
        </head>

        <body>
            <div class="app-shell app-shell-fixed inventario-shell">
                <%@ include file="_topnav.jspf" %>
                    <header class="page-head">
                        <div>
                            <div class="brand-lockup">
                                <img class="brand-mark"
                                    src="${pageContext.request.contextPath}/assets/img/brand/marca-gds-centrada.png"
                                    alt="Emblema Guardias de Santiago">
                                <img class="brand-mark-horizontal"
                                    src="${pageContext.request.contextPath}/assets/img/brand/logo-gds-horizontal-color.png"
                                    alt="Logotipo Guardias de Santiago">
                            </div>
                            <p class="eyebrow">Custodia de patrimonio</p>
                            <h1>Inventario y prendas</h1>
                            <p class="lead">Esta pantalla se divide en cuatro zonas: almacén, préstamos, histórico y
                                búsqueda.</p>
                        </div>
                        <div class="hero-actions">
                            <a class="button button-secondary" href="${pageContext.request.contextPath}/home">Volver</a>
                            <a class="button button-primary" href="${pageContext.request.contextPath}/amigos">Vista
                                publica</a>
                        </div>
                    </header>

                    <c:if test="${not empty estado}">
                        <div class="notice notice-info">${estado}</div>
                    </c:if>

                    <c:if test="${not canEditInventory}">
                        <div class="notice notice-info">Tu rol es de consulta: puedes revisar inventario, historial y
                            préstamos, pero no modificar el almacén.</div>
                    </c:if>

                    <section class="panel">
                        <h2>Mapa de la pantalla</h2>
                        <p class="panel-intro">Si es la primera vez, entra primero en el bloque que describes a
                            continuación.</p>
                        <div class="help-grid">
                            <article class="help-card">
                                <strong>1. Almacén</strong>
                                <p>Altas por IA o manuales, correcciones, bajas y valoración económica.</p>
                                <p><a href="#almacen">Ir al almacén</a></p>
                            </article>
                            <article class="help-card">
                                <strong>2. Prendas y préstamos</strong>
                                <p>Salir, devolver y consultar quién tiene cada prenda ahora.</p>
                                <p><a href="#prestamos">Ir a préstamos</a></p>
                            </article>
                            <article class="help-card">
                                <strong>3. Histórico</strong>
                                <p>Ver piezas activas, archivadas y el libro de prestamos.</p>
                                <p><a href="#historico">Ir al histórico</a></p>
                            </article>
                            <article class="help-card">
                                <strong>4. Buscar</strong>
                                <p>Localizar una prenda por palabras simples como color o tipo.</p>
                                <p><a href="#buscar">Ir a buscar</a></p>
                            </article>
                        </div>
                    </section>

                    <section class="stats-grid">
                        <article class="stat-card">
                            <span>Secciones</span>
                            <strong>${secciones.size()}</strong>
                        </article>
                        <article class="stat-card">
                            <span>Pertrechos activos</span>
                            <strong>${pertrechosActivos.size()}</strong>
                        </article>
                        <article class="stat-card">
                            <span>Pertrechos archivados</span>
                            <strong>${pertrechosArchivados.size()}</strong>
                        </article>
                        <article class="stat-card">
                            <span>Prestamos</span>
                            <strong>${alardes.size()}</strong>
                        </article>
                    </section>

                    <section id="almacen" class="panel">
                        <div class="panel-head">
                            <h2>1. Almacén de materiales</h2>
                            <span class="badge badge-neutral">Altas, correcciones y bajas</span>
                        </div>
                        <p class="panel-intro">Aquí se crean y corrigen los materiales que entran en el sistema. La
                            valoración económica es orientativa y puede ajustarse.</p>

                        <c:if test="${canEditInventory}">
                            <div class="panel-grid">
                                <article class="panel">
                                    <h3>Crear seccion</h3>
                                    <p class="field-note">Usa esto solo cuando aparezca una sección nueva en el taller o
                                        almacén.</p>
                                    <form class="form-grid" method="post"
                                        action="${pageContext.request.contextPath}/inventario">
                                        <input type="hidden" name="accion" value="crearSeccion">
                                        <label>
                                            Nombre de seccion
                                            <input type="text" name="nombreSeccion" required
                                                placeholder="Armeria, Sastreria...">
                                        </label>
                                        <label>
                                            Responsable
                                            <select name="responsableId" required>
                                                <c:forEach items="${guardias}" var="guardia">
                                                    <option value="${guardia.id}">${guardia.apodo} - ${guardia.rango}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </label>
                                        <button class="button button-primary" type="submit">Crear seccion</button>
                                    </form>
                                </article>

                                <article class="panel">
                                    <h3>Alta manual de material</h3>
                                    <p class="field-note">Si la IA se equivoca, aquí puedes corregir el material con
                                        nombre,
                                        estado y valor.</p>
                                    <form class="form-grid" method="post"
                                        action="${pageContext.request.contextPath}/inventario">
                                        <input type="hidden" name="accion" value="crearPertrecho">
                                        <label>
                                            Seccion
                                            <select name="seccionId" required>
                                                <c:forEach items="${secciones}" var="seccion">
                                                    <option value="${seccion.id}">${seccion.nombreSeccion}</option>
                                                </c:forEach>
                                            </select>
                                        </label>
                                        <label>
                                            Nombre o descripcion
                                            <textarea name="descripcionManual" rows="2" required
                                                placeholder="Jubon negro con cruz roja"></textarea>
                                        </label>
                                        <label>
                                            Valor economico estimado
                                            <input type="number" name="valorEconomicoManual" step="0.01" min="0"
                                                value="0">
                                        </label>
                                        <label>
                                            Integridad (0-100)
                                            <input type="number" name="integridadManual" min="0" max="100" value="100"
                                                required>
                                        </label>
                                        <label>
                                            Estado IA
                                            <select name="estadoIaManual">
                                                <option value="Pendiente">Pendiente</option>
                                                <option value="Validado">Validado</option>
                                                <option value="Dudoso">Dudoso</option>
                                            </select>
                                        </label>
                                        <input type="hidden" name="disponibleManual" value="true">
                                        <button class="button button-primary" type="submit">Guardar material</button>
                                    </form>
                                </article>

                                <article class="panel">
                                    <h3>Alta guiada por IA</h3>
                                    <p class="field-note">Escribe una descripción simple de la prenda. La IA sugiere una
                                        sección y luego puedes corregirla.</p>
                                    <form class="form-grid" method="post"
                                        action="${pageContext.request.contextPath}/inventario">
                                        <input type="hidden" name="accion" value="altaIa">
                                        <label>
                                            Descripcion de la prenda
                                            <textarea name="descripcionPertrecho" rows="3" required
                                                placeholder="Jubon negro con cruz roja, tela gruesa..."></textarea>
                                        </label>
                                        <button class="button button-primary" type="submit">Analizar y
                                            registrar</button>
                                    </form>
                                </article>
                            </div>

                            <div class="panel-grid">
                                <article class="panel">
                                    <h3>Corregir material</h3>
                                    <p class="field-note">Usa esto para ajustar una ficha ya creada sin volver a empezar
                                        desde cero.</p>
                                    <form class="form-grid" method="post"
                                        action="${pageContext.request.contextPath}/inventario">
                                        <input type="hidden" name="accion" value="actualizarPertrecho">
                                        <label>
                                            Material
                                            <select name="pertrechoIdEditar" required>
                                                <c:forEach items="${pertrechosActivos}" var="pertrecho">
                                                    <option value="${pertrecho.id}">${pertrecho.descripcion}</option>
                                                </c:forEach>
                                            </select>
                                        </label>
                                        <label>
                                            Seccion
                                            <select name="seccionIdEditar" required>
                                                <c:forEach items="${secciones}" var="seccion">
                                                    <option value="${seccion.id}">${seccion.nombreSeccion}</option>
                                                </c:forEach>
                                            </select>
                                        </label>
                                        <label>
                                            Nombre o descripcion
                                            <textarea name="descripcionEditar" rows="2" required></textarea>
                                        </label>
                                        <label>
                                            Valor economico estimado
                                            <input type="number" name="valorEconomicoEditar" step="0.01" min="0"
                                                value="0">
                                        </label>
                                        <label>
                                            Integridad (0-100)
                                            <input type="number" name="integridadEditar" min="0" max="100" value="100"
                                                required>
                                        </label>
                                        <label>
                                            Estado IA
                                            <select name="estadoIaEditar">
                                                <option value="Pendiente">Pendiente</option>
                                                <option value="Validado">Validado</option>
                                                <option value="Dudoso">Dudoso</option>
                                            </select>
                                        </label>
                                        <input type="hidden" name="disponibleEditar" value="true">
                                        <button class="button button-secondary" type="submit">Actualizar
                                            material</button>
                                    </form>
                                </article>

                                <c:if test="${canDeletePertrecho}">
                                    <article class="panel">
                                        <h3>Dar de baja</h3>
                                        <p class="field-note">La prenda no se borra: queda archivada para poder buscarla
                                            y
                                            consultar su historial.</p>
                                        <form class="form-grid" method="post"
                                            action="${pageContext.request.contextPath}/inventario">
                                            <input type="hidden" name="accion" value="eliminarPertrecho">
                                            <label>
                                                Material activo
                                                <select name="pertrechoIdEliminar" required>
                                                    <c:forEach items="${pertrechosActivos}" var="pertrecho">
                                                        <option value="${pertrecho.id}">${pertrecho.descripcion}
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </label>
                                            <button class="button button-secondary" type="submit">Marcar como
                                                archivado</button>
                                        </form>
                                    </article>
                                </c:if>
                            </div>
                        </c:if>

                        <c:if test="${canValidateMasivo}">
                            <section class="panel">
                                <div class="panel-head">
                                    <h3>Validacion masiva IA (solo Maestre)</h3>
                                </div>
                                <p class="panel-intro">Sirve para revisar varios materiales a la vez sin entrar uno por
                                    uno.
                                </p>
                                <form class="form-grid" method="post"
                                    action="${pageContext.request.contextPath}/inventario">
                                    <input type="hidden" name="accion" value="validarMasivo">
                                    <label>
                                        Estado nuevo
                                        <select name="estadoIaNuevo">
                                            <option value="Validado">Validado</option>
                                            <option value="Pendiente">Pendiente</option>
                                            <option value="Dudoso">Dudoso</option>
                                        </select>
                                    </label>
                                    <div class="checkbox-grid">
                                        <c:forEach items="${pertrechosActivos}" var="pertrecho">
                                            <label class="checkbox-row">
                                                <input type="checkbox" name="pertrechoSeleccionado"
                                                    value="${pertrecho.id}">
                                                ${pertrecho.descripcion} (${pertrecho.estadoIa})
                                            </label>
                                        </c:forEach>
                                    </div>
                                    <button class="button button-secondary" type="submit">Aplicar validacion</button>
                                </form>
                            </section>
                        </c:if>
                    </section>

                    <section id="prestamos" class="panel">
                        <div class="panel-head">
                            <h2>2. Prendas y préstamos</h2>
                            <span class="badge badge-neutral">Salidas y devoluciones</span>
                        </div>
                        <p class="panel-intro">Esta parte registra quién tiene la prenda, cuándo salió y cuándo vuelve.
                            Aquí
                            se entiende el estado actual de cada objeto.</p>

                        <c:if test="${canEditInventory}">
                            <div class="panel-grid">
                                <article class="panel">
                                    <h3>Registrar salida</h3>
                                    <p class="field-note">Selecciona un guardia y un material activo para crear un
                                        préstamo
                                        nuevo.</p>
                                    <form class="form-grid" method="post"
                                        action="${pageContext.request.contextPath}/inventario">
                                        <input type="hidden" name="accion" value="prestar">
                                        <label>
                                            Guardia receptor
                                            <select name="guardiaId" required>
                                                <c:forEach items="${guardias}" var="guardia">
                                                    <option value="${guardia.id}">${guardia.apodo} -
                                                        ${guardia.estadoHonor}
                                                        (${guardia.puntosGracia} puntos)</option>
                                                </c:forEach>
                                            </select>
                                        </label>
                                        <label>
                                            Material activo
                                            <select name="pertrechoId" required>
                                                <c:forEach items="${pertrechosActivos}" var="pertrecho">
                                                    <option value="${pertrecho.id}">${pertrecho.descripcion} -
                                                        ${pertrecho.seccionNombre} (${pertrecho.disponible ?
                                                        'Disponible' :
                                                        'Prestado'})</option>
                                                </c:forEach>
                                            </select>
                                        </label>
                                        <label>
                                            Observaciones
                                            <textarea name="observacionesSalida" rows="2"
                                                placeholder="Salida para ensayo, préstamo temporal..."></textarea>
                                        </label>
                                        <button class="button button-primary" type="submit">Registrar salida</button>
                                    </form>
                                </article>

                                <article class="panel">
                                    <h3>Registrar devolucion</h3>
                                    <p class="field-note">Marca aquí cuando la prenda regresa al almacén y deja
                                        constancia
                                        de su estado.</p>
                                    <form class="form-grid" method="post"
                                        action="${pageContext.request.contextPath}/inventario">
                                        <input type="hidden" name="accion" value="devolver">
                                        <label>
                                            Préstamo abierto
                                            <select name="alardeId" required>
                                                <c:forEach items="${alardes}" var="alarde">
                                                    <option value="${alarde.id}">
                                                        #${alarde.id} - ${alarde.guardiaApodo} /
                                                        ${alarde.pertrechoDescripcion}
                                                        (${empty alarde.fechaEntrada ? 'Abierto' : 'Cerrado'})
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </label>
                                        <label>
                                            Integridad al devolver (0-100)
                                            <input type="number" name="integridadEntrada" min="0" max="100" value="100"
                                                required>
                                        </label>
                                        <label>
                                            Observaciones
                                            <textarea name="observacionesEntrada" rows="2"
                                                placeholder="Devuelto con manchas leves..."></textarea>
                                        </label>
                                        <button class="button button-secondary" type="submit">Cerrar prestamo</button>
                                    </form>
                                </article>
                            </div>
                        </c:if>
                    </section>

                    <section id="historico" class="panel">
                        <div class="panel-head">
                            <h2>3. Histórico</h2>
                            <span class="badge badge-neutral">Activos, archivados y prestamos</span>
                        </div>
                        <p class="panel-intro">Aquí puedes revisar todo el material con su estado actual. Los archivados
                            no
                            se borran y siguen apareciendo para búsquedas y consultas.</p>

                        <div class="panel-grid">
                            <article class="panel">
                                <div class="panel-head">
                                    <h3>Pertrechos activos</h3>
                                    <span class="badge badge-ok">${pertrechosActivos.size()} activos</span>
                                </div>
                                <div class="group-grid inventory-grid">
                                    <c:forEach items="${pertrechosActivos}" var="pertrecho">
                                        <article class="group-card inventory-card" data-searchable="true"
                                            data-search-text="${pertrecho.descripcion} ${pertrecho.seccionNombre} ${pertrecho.estadoIa} ${pertrecho.tokenQr}">
                                            <strong>${pertrecho.descripcion}</strong>
                                            <p>Seccion: ${pertrecho.seccionNombre}</p>
                                            <p>Integridad: ${pertrecho.integridad}%</p>
                                            <p>Valor economico: ${pertrecho.valorEconomico} €</p>
                                            <p>Estado IA: ${pertrecho.estadoIa}</p>
                                            <p>Token QR: ${pertrecho.tokenQr}</p>
                                            <p>Disponibilidad: ${pertrecho.disponible ? 'Disponible' : 'Prestado'}</p>
                                            <p>Alta: ${empty pertrecho.fechaCreacion ? 'Sin fecha' :
                                                pertrecho.fechaCreacion}</p>
                                            <a class="button button-secondary"
                                                href="${pageContext.request.contextPath}/qr?token=${pertrecho.tokenQr}">Ver
                                                ficha publica</a>
                                        </article>
                                    </c:forEach>
                                </div>
                            </article>

                            <article class="panel">
                                <div class="panel-head">
                                    <h3>Pertrechos archivados</h3>
                                    <span class="badge badge-danger">${pertrechosArchivados.size()} archivados</span>
                                </div>
                                <div class="group-grid inventory-grid">
                                    <c:forEach items="${pertrechosArchivados}" var="pertrecho">
                                        <article class="group-card inventory-card inventory-card-muted"
                                            data-searchable="true"
                                            data-search-text="${pertrecho.descripcion} ${pertrecho.seccionNombre} ${pertrecho.estadoIa} ${pertrecho.motivoBaja}">
                                            <strong>${pertrecho.descripcion}</strong>
                                            <p>Seccion: ${pertrecho.seccionNombre}</p>
                                            <p>Valor economico: ${pertrecho.valorEconomico} €</p>
                                            <p>Estado IA: ${pertrecho.estadoIa}</p>
                                            <p>Estado: Archivado</p>
                                            <p>Baja: ${empty pertrecho.fechaBaja ? 'Sin fecha' : pertrecho.fechaBaja}
                                            </p>
                                            <p>Motivo: ${empty pertrecho.motivoBaja ? 'Baja manual' :
                                                pertrecho.motivoBaja}
                                            </p>
                                        </article>
                                    </c:forEach>
                                </div>
                            </article>
                        </div>

                        <div class="timeline inventory-history">
                            <c:forEach items="${alardes}" var="alarde">
                                <article class="message-card inventory-card" data-searchable="true"
                                    data-search-text="${alarde.guardiaApodo} ${alarde.pertrechoDescripcion} ${alarde.autorizadorApodo}">
                                    <div class="message-meta">
                                        <strong>#${alarde.id} · ${alarde.guardiaApodo}</strong>
                                        <span>${alarde.fechaSalida}</span>
                                    </div>
                                    <p>${alarde.pertrechoDescripcion}</p>
                                    <small>Autorizador: ${alarde.autorizadorApodo}</small>
                                    <small>Entrada: ${empty alarde.fechaEntrada ? 'Pendiente' :
                                        alarde.fechaEntrada}</small>
                                    <small>Integridad salida/entrada: ${alarde.integridadSalida}% /
                                        ${alarde.integridadEntrada < 0 ? 'N/A' : alarde.integridadEntrada}</small>
                                            <small>Delta gracia: ${alarde.deltaGracia}</small>
                                            <small class="${alarde.ticketMaestranza ? 'text-danger' : ''}">
                                                ${alarde.ticketMaestranza ? 'Ticket maestranza generado' : 'Sin
                                                incidencias
                                                graves'}
                                            </small>
                                </article>
                            </c:forEach>
                        </div>
                    </section>

                    <section id="buscar" class="panel">
                        <div class="panel-head">
                            <h2>4. Buscar una prenda</h2>
                            <span class="badge badge-neutral">Busqueda simple</span>
                        </div>
                        <p class="panel-intro">Escribe palabras como color, nombre, material o seccion. El filtro
                            buscara en
                            todo lo visible del inventario.</p>
                        <div class="search-row">
                            <input id="inventorySearch" type="text" placeholder="Ejemplo: jubon negro cruz roja">
                            <button class="button button-secondary" type="button"
                                id="clearInventorySearch">Limpiar</button>
                        </div>
                    </section>
            </div>
            <script>
                (function () {
                    const searchInput = document.getElementById('inventorySearch');
                    const clearButton = document.getElementById('clearInventorySearch');
                    const cards = document.querySelectorAll('[data-searchable="true"]');

                    if (!searchInput || !cards.length) {
                        return;
                    }

                    function normalize(text) {
                        return (text || '').toString().toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
                    }

                    function applyFilter() {
                        const query = normalize(searchInput.value.trim());
                        cards.forEach(function (card) {
                            const haystack = normalize(card.getAttribute('data-search-text') || card.textContent || '');
                            card.style.display = query === '' || haystack.includes(query) ? '' : 'none';
                        });
                    }

                    searchInput.addEventListener('input', applyFilter);
                    clearButton.addEventListener('click', function () {
                        searchInput.value = '';
                        applyFilter();
                        searchInput.focus();
                    });
                })();
            </script>
        </body>

        </html>