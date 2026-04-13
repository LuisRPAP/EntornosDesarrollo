<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>RenaceGest | Inventario y Alardes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<div class="app-shell">
    <header class="page-head">
        <div>
            <p class="eyebrow">Custodia de patrimonio</p>
            <h1>Inventario y alardes</h1>
        </div>
        <div class="hero-actions">
            <a class="button button-secondary" href="${pageContext.request.contextPath}/home">Volver</a>
            <a class="button button-primary" href="${pageContext.request.contextPath}/amigos">Vista publica</a>
        </div>
    </header>

    <c:if test="${not empty estado}">
        <div class="notice">${estado}</div>
    </c:if>

    <c:if test="${not canEditInventory}">
        <div class="notice">Tu rol es de consulta: puedes ver trazabilidad, pertrechos y alardes, pero no modificar inventario.</div>
    </c:if>

    <section class="stats-grid">
        <article class="stat-card">
            <span>Secciones</span>
            <strong>${secciones.size()}</strong>
        </article>
        <article class="stat-card">
            <span>Pertrechos</span>
            <strong>${pertrechos.size()}</strong>
        </article>
        <article class="stat-card">
            <span>Alardes</span>
            <strong>${alardes.size()}</strong>
        </article>
        <article class="stat-card">
            <span>Tickets maestranza</span>
            <strong>${ticketsMaestranza}</strong>
        </article>
    </section>

    <c:if test="${canEditInventory}">
    <section class="panel-grid">
        <article class="panel">
            <h2>Nueva seccion</h2>
            <form class="form-grid" method="post" action="${pageContext.request.contextPath}/inventario">
                <input type="hidden" name="accion" value="crearSeccion">
                <label>
                    Nombre de seccion
                    <input type="text" name="nombreSeccion" required>
                </label>
                <label>
                    Responsable
                    <select name="responsableId" required>
                        <c:forEach items="${guardias}" var="guardia">
                            <option value="${guardia.id}">${guardia.apodo} - ${guardia.rango}</option>
                        </c:forEach>
                    </select>
                </label>
                <button class="button button-primary" type="submit">Crear seccion</button>
            </form>
        </article>

        <article class="panel">
            <h2>CRUD material (manual)</h2>
            <form class="form-grid" method="post" action="${pageContext.request.contextPath}/inventario">
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
                    Descripcion
                    <textarea name="descripcionManual" rows="2" required></textarea>
                </label>
                <label>
                    Integridad (0-100)
                    <input type="text" name="integridadManual" value="100" required>
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
                <button class="button button-primary" type="submit">Crear material</button>
            </form>
        </article>

        <article class="panel">
            <h2>Alta IA de pertrecho</h2>
            <form class="form-grid" method="post" action="${pageContext.request.contextPath}/inventario">
                <input type="hidden" name="accion" value="altaIa">
                <label>
                    Descripcion del pertrecho
                    <textarea name="descripcionPertrecho" rows="3" required></textarea>
                </label>
                <button class="button button-primary" type="submit">Analizar y registrar</button>
            </form>
        </article>
    </section>
    </c:if>

    <c:if test="${canEditInventory}">
    <section class="panel-grid">
        <article class="panel">
            <h2>Actualizar material</h2>
            <form class="form-grid" method="post" action="${pageContext.request.contextPath}/inventario">
                <input type="hidden" name="accion" value="actualizarPertrecho">
                <label>
                    Pertrecho
                    <select name="pertrechoIdEditar" required>
                        <c:forEach items="${pertrechos}" var="pertrecho">
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
                    Descripcion
                    <textarea name="descripcionEditar" rows="2" required></textarea>
                </label>
                <label>
                    Integridad (0-100)
                    <input type="text" name="integridadEditar" value="100" required>
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
                <button class="button button-secondary" type="submit">Actualizar material</button>
            </form>
        </article>

        <c:if test="${canDeletePertrecho}">
        <article class="panel">
            <h2>Eliminar material</h2>
            <form class="form-grid" method="post" action="${pageContext.request.contextPath}/inventario">
                <input type="hidden" name="accion" value="eliminarPertrecho">
                <label>
                    Pertrecho
                    <select name="pertrechoIdEliminar" required>
                        <c:forEach items="${pertrechos}" var="pertrecho">
                            <option value="${pertrecho.id}">${pertrecho.descripcion}</option>
                        </c:forEach>
                    </select>
                </label>
                <button class="button button-secondary" type="submit">Eliminar material</button>
            </form>
        </article>
        </c:if>
    </section>
    </c:if>

    <c:if test="${canValidateMasivo}">
    <section class="panel">
        <div class="panel-head">
            <h2>Validacion masiva IA (Maestre)</h2>
        </div>
        <form class="form-grid" method="post" action="${pageContext.request.contextPath}/inventario">
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
                <c:forEach items="${pertrechos}" var="pertrecho">
                    <label class="checkbox-row">
                        <input type="checkbox" name="pertrechoSeleccionado" value="${pertrecho.id}">
                        ${pertrecho.descripcion} (${pertrecho.estadoIa})
                    </label>
                </c:forEach>
            </div>
            <button class="button button-secondary" type="submit">Aplicar validacion</button>
        </form>
    </section>
    </c:if>

    <c:if test="${canEditInventory}">
    <section class="panel-grid">
        <article class="panel">
            <h2>Registrar salida de alarde</h2>
            <form class="form-grid" method="post" action="${pageContext.request.contextPath}/inventario">
                <input type="hidden" name="accion" value="prestar">
                <label>
                    Guardia receptor
                    <select name="guardiaId" required>
                        <c:forEach items="${guardias}" var="guardia">
                            <option value="${guardia.id}">${guardia.apodo} - ${guardia.estadoHonor} (${guardia.puntosGracia} puntos)</option>
                        </c:forEach>
                    </select>
                </label>
                <label>
                    Pertrecho
                    <select name="pertrechoId" required>
                        <c:forEach items="${pertrechos}" var="pertrecho">
                            <option value="${pertrecho.id}">${pertrecho.descripcion} - ${pertrecho.seccionNombre} (${pertrecho.disponible ? 'Disponible' : 'Prestado'})</option>
                        </c:forEach>
                    </select>
                </label>
                <label>
                    Observaciones
                    <textarea name="observacionesSalida" rows="2"></textarea>
                </label>
                <button class="button button-primary" type="submit">Registrar salida</button>
            </form>
        </article>

        <article class="panel">
            <h2>Registrar devolucion</h2>
            <form class="form-grid" method="post" action="${pageContext.request.contextPath}/inventario">
                <input type="hidden" name="accion" value="devolver">
                <label>
                    Alarde abierto/cerrado
                    <select name="alardeId" required>
                        <c:forEach items="${alardes}" var="alarde">
                            <option value="${alarde.id}">
                                #${alarde.id} - ${alarde.guardiaApodo} / ${alarde.pertrechoDescripcion}
                                (${empty alarde.fechaEntrada ? 'Abierto' : 'Cerrado'})
                            </option>
                        </c:forEach>
                    </select>
                </label>
                <label>
                    Integridad al devolver (0-100)
                    <input type="text" name="integridadEntrada" value="100" required>
                </label>
                <label>
                    Observaciones
                    <textarea name="observacionesEntrada" rows="2"></textarea>
                </label>
                <button class="button button-secondary" type="submit">Cerrar alarde</button>
            </form>
        </article>
    </section>
    </c:if>

    <section class="panel">
        <div class="panel-head">
            <h2>Pertrechos y trazabilidad</h2>
        </div>
        <div class="group-grid">
            <c:forEach items="${pertrechos}" var="pertrecho">
                <article class="group-card">
                    <strong>${pertrecho.descripcion}</strong>
                    <p>Seccion: ${pertrecho.seccionNombre}</p>
                    <p>Integridad: ${pertrecho.integridad}%</p>
                    <p>Estado IA: ${pertrecho.estadoIa}</p>
                    <p>Token QR: ${pertrecho.tokenQr}</p>
                    <p>Disponibilidad: ${pertrecho.disponible ? 'Disponible' : 'Prestado'}</p>
                    <a class="button button-secondary" href="${pageContext.request.contextPath}/qr?token=${pertrecho.tokenQr}">Ver ficha publica</a>
                </article>
            </c:forEach>
        </div>
    </section>

    <section class="panel">
        <div class="panel-head">
            <h2>Libro de alarde</h2>
        </div>
        <div class="timeline">
            <c:forEach items="${alardes}" var="alarde">
                <article class="message-card">
                    <div class="message-meta">
                        <strong>#${alarde.id} · ${alarde.guardiaApodo}</strong>
                        <span>${alarde.fechaSalida}</span>
                    </div>
                    <p>${alarde.pertrechoDescripcion}</p>
                    <small>Autorizador: ${alarde.autorizadorApodo}</small>
                    <small>Entrada: ${empty alarde.fechaEntrada ? 'Pendiente' : alarde.fechaEntrada}</small>
                    <small>Integridad salida/entrada: ${alarde.integridadSalida}% / ${alarde.integridadEntrada < 0 ? 'N/A' : alarde.integridadEntrada}</small>
                    <small>Delta gracia: ${alarde.deltaGracia}</small>
                    <small class="${alarde.ticketMaestranza ? 'text-danger' : ''}">
                        ${alarde.ticketMaestranza ? 'Ticket maestranza generado' : 'Sin incidencias graves'}
                    </small>
                </article>
            </c:forEach>
        </div>
    </section>
</div>
</body>
</html>
