<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>RenaceGest | Mensajes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<div class="app-shell">
    <header class="page-head">
        <div>
            <p class="eyebrow">Mensajeria de la guardia</p>
            <h1>Mensajes internos</h1>
        </div>
        <a class="button button-secondary" href="${pageContext.request.contextPath}/home">Volver</a>
    </header>

    <c:if test="${not empty estado}">
        <div class="notice">${estado}</div>
    </c:if>

    <section class="panel-grid">
        <article class="panel">
            <h2>Enviar mensaje</h2>
            <form class="form-grid" method="post" action="${pageContext.request.contextPath}/mensajes">
                <label>
                    Destino
                    <select name="grupoId">
                        <option value="">Sin grupo (broadcast solo Maestre)</option>
                        <c:forEach items="${grupos}" var="grupo">
                            <option value="${grupo.id}">${grupo.nombreGrupo}</option>
                        </c:forEach>
                    </select>
                </label>
                <label>
                    Contenido
                    <textarea name="contenido" rows="4" required></textarea>
                </label>
                <c:if test="${not canBroadcast}">
                    <input type="hidden" name="broadcast" value="false">
                    <small>Solo Maestre puede enviar broadcast global.</small>
                </c:if>
                <c:if test="${canBroadcast}">
                    <label class="checkbox-row">
                        <input type="checkbox" name="broadcast" value="true">
                        Mensaje global
                    </label>
                </c:if>
                <button class="button button-primary" type="submit">Enviar mensaje</button>
            </form>
        </article>

        <article class="panel">
            <h2>Conversacion reciente</h2>
            <div class="timeline">
                <c:forEach items="${mensajes}" var="mensaje">
                    <article class="message-card">
                        <div class="message-meta">
                            <strong>${mensaje.emisorApodo}</strong>
                            <span>${mensaje.fechaEnvio}</span>
                        </div>
                        <p>${mensaje.contenido}</p>
                        <small>
                            <c:choose>
                                <c:when test="${mensaje.broadcast}">Broadcast global</c:when>
                                <c:otherwise>Grupo: ${mensaje.grupoNombre}</c:otherwise>
                            </c:choose>
                        </small>
                    </article>
                </c:forEach>
            </div>
        </article>
    </section>
</div>
</body>
</html>
