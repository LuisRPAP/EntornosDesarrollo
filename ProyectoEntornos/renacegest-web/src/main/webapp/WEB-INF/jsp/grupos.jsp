<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>RenaceGest | Grupos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<div class="app-shell">
    <header class="page-head">
        <div>
            <p class="eyebrow">Comunicacion interna</p>
            <h1>Grupos y misiones</h1>
        </div>
        <a class="button button-secondary" href="${pageContext.request.contextPath}/home">Volver</a>
    </header>

    <c:if test="${not empty mensajeEstado}">
        <div class="notice">${mensajeEstado}</div>
    </c:if>

    <c:if test="${not canManageGroups}">
        <div class="notice">Tu rol tiene acceso de lectura en grupos. Solo Maestre o Sargento pueden modificar.</div>
    </c:if>

    <c:if test="${canManageGroups}">
    <section class="panel-grid">
        <article class="panel">
            <h2>Crear grupo</h2>
            <form class="form-grid" method="post" action="${pageContext.request.contextPath}/grupos">
                <input type="hidden" name="accion" value="crear">
                <label>
                    Nombre
                    <input name="nombreGrupo" type="text" required>
                </label>
                <label>
                    Tipo
                    <select name="tipo">
                        <option value="GrupoTrabajo">Grupo de trabajo</option>
                        <option value="Mision">Mision</option>
                    </select>
                </label>
                <label>
                    Descripcion
                    <textarea name="descripcion" rows="3" required></textarea>
                </label>
                <label>
                    Jefe de equipo
                    <select name="jefeEquipoId">
                        <c:forEach items="${guardias}" var="guardia">
                            <option value="${guardia.id}">${guardia.apodo} - ${guardia.rango}</option>
                        </c:forEach>
                    </select>
                </label>
                <button class="button button-primary" type="submit">Crear grupo</button>
            </form>
        </article>

        <article class="panel">
            <h2>Añadir o quitar miembros</h2>
            <form class="form-grid" method="post" action="${pageContext.request.contextPath}/grupos">
                <label>
                    Grupo
                    <select name="grupoId">
                        <c:forEach items="${grupos}" var="grupo">
                            <option value="${grupo.id}">${grupo.nombreGrupo}</option>
                        </c:forEach>
                    </select>
                </label>
                <label>
                    Miembro
                    <select name="miembroId">
                        <c:forEach items="${guardias}" var="guardia">
                            <option value="${guardia.id}">${guardia.apodo}</option>
                        </c:forEach>
                    </select>
                </label>
                <div class="button-row">
                    <button class="button button-primary" name="accion" value="agregar" type="submit">Añadir miembro</button>
                    <button class="button button-secondary" name="accion" value="quitar" type="submit">Quitar miembro</button>
                </div>
            </form>
        </article>
    </section>
    </c:if>

    <section class="panel">
        <h2>Listado de grupos</h2>
        <div class="group-grid">
            <c:forEach items="${grupos}" var="grupo">
                <article class="group-card">
                    <div class="panel-head">
                        <div>
                            <strong>${grupo.nombreGrupo}</strong>
                            <p>${grupo.descripcion}</p>
                        </div>
                        <span class="badge badge-neutral">${grupo.tipo}</span>
                    </div>
                    <p><strong>Jefe:</strong> ${grupo.jefeEquipo}</p>
                    <p><strong>Creado por:</strong> ${grupo.creadoPor}</p>
                    <p><strong>Estado:</strong> ${grupo.activo ? 'Activo' : 'Inactivo'}</p>
                    <div class="members">
                        <c:forEach items="${miembrosPorGrupo[grupo.id]}" var="miembro">
                            <span class="member-pill">${miembro.apodo} · ${miembro.rolEnGrupo}</span>
                        </c:forEach>
                    </div>
                </article>
            </c:forEach>
        </div>
    </section>
</div>
</body>
</html>
