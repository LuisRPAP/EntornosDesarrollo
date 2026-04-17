<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>RenaceGest | Portal Principal</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
        </head>

        <body>
            <div class="app-shell app-shell-fixed home-shell">
                <%@ include file="_topnav.jspf" %>
                    <c:if test="${not empty dbErrorMessage}">
                        <div class="notice">${dbErrorMessage}</div>
                    </c:if>

                    <header class="hero">
                        <div>
                            <div class="brand-lockup">
                                <img class="brand-mark"
                                    src="${pageContext.request.contextPath}/assets/img/brand/marca-gds-centrada.png"
                                    alt="Emblema Guardias de Santiago">
                                <img class="brand-mark-horizontal"
                                    src="${pageContext.request.contextPath}/assets/img/brand/logo-gds-horizontal-color.png"
                                    alt="Logotipo Guardias de Santiago">
                            </div>
                            <p class="eyebrow">Guardias de Santiago</p>
                            <h1>RenaceGest</h1>
                            <p class="lead">Gestion de prestamos, grupos y mensajeria con una ruta clara para cada
                                tarea.</p>
                        </div>
                        <div class="hero-actions">
                            <a class="button button-primary"
                                href="${pageContext.request.contextPath}/inventario">Inventario
                                de prendas</a>
                            <a class="button button-primary" href="${pageContext.request.contextPath}/grupos">Gestionar
                                grupos</a>
                            <a class="button button-secondary"
                                href="${pageContext.request.contextPath}/mensajes">Mensajería</a>
                            <a class="button button-secondary"
                                href="${pageContext.request.contextPath}/listados">Listados
                                avanzados</a>
                            <c:if test="${currentRole == 'Maestre' || currentRole == 'Sargento'}">
                                <a class="button button-secondary"
                                    href="${pageContext.request.contextPath}/importacion">Importacion masiva</a>
                            </c:if>
                        </div>
                    </header>

                    <section class="panel quick-path">
                        <h2>Ruta rapida por tareas</h2>
                        <p class="panel-intro">Si no sabes por donde empezar, usa estas entradas directas segun lo que
                            quieras hacer.</p>
                        <div class="help-grid">
                            <article class="help-card">
                                <strong>1. Gestionar prendas e inventario</strong>
                                <p>Entrar al almacén, gestionar préstamos y revisar prendas archivadas.</p>
                                <p><a href="${pageContext.request.contextPath}/inventario">Ir a Inventario</a></p>
                            </article>
                            <article class="help-card">
                                <strong>2. Organizar personas</strong>
                                <p>Crear o ajustar equipos y miembros en Grupos y misiones.</p>
                                <p><a href="${pageContext.request.contextPath}/grupos">Ir a Grupos</a></p>
                            </article>
                            <article class="help-card">
                                <strong>3. Comunicar avisos</strong>
                                <p>Enviar mensajes por grupo o generales.</p>
                                <p><a href="${pageContext.request.contextPath}/mensajes">Ir a Mensajeria</a></p>
                            </article>
                            <article class="help-card">
                                <strong>4. Consultar resultados</strong>
                                <p>Ver listados y revisar actividad reciente.</p>
                                <p><a href="${pageContext.request.contextPath}/listados">Ir a Listados</a></p>
                            </article>
                        </div>
                    </section>

                    <section class="stats-grid">
                        <article class="stat-card">
                            <span>Guardias</span>
                            <strong>${totalGuardias}</strong>
                        </article>
                        <article class="stat-card">
                            <span>Grupos</span>
                            <strong>${totalGrupos}</strong>
                        </article>
                        <article class="stat-card">
                            <span>Mensajes</span>
                            <strong>${totalMensajes}</strong>
                        </article>
                    </section>

                    <section class="panel-grid">
                        <article class="panel">
                            <div class="panel-head">
                                <h2>Guardias activos</h2>
                                <a href="${pageContext.request.contextPath}/grupos">Ver grupos</a>
                            </div>
                            <p class="panel-intro">Lista rapida de personas activas y su estado actual.</p>
                            <div class="list">
                                <c:forEach items="${guardias}" var="guardia">
                                    <div class="list-item">
                                        <div>
                                            <strong>${guardia.apodo}</strong>
                                            <p>${guardia.nombreReal} · ${guardia.rango}</p>
                                        </div>
                                        <span
                                            class="badge ${guardia.estadoHonor == 'Infame' ? 'badge-danger' : 'badge-ok'}">${guardia.estadoHonor}</span>
                                    </div>
                                </c:forEach>
                            </div>
                        </article>

                        <article class="panel">
                            <div class="panel-head">
                                <h2>Grupos y misiones</h2>
                                <a href="${pageContext.request.contextPath}/mensajes">Abrir mensajería</a>
                            </div>
                            <p class="panel-intro">Resumen de equipos para ubicar rapidamente el grupo correcto.</p>
                            <div class="list">
                                <c:forEach items="${grupos}" var="grupo">
                                    <div class="list-item">
                                        <div>
                                            <strong>${grupo.nombreGrupo}</strong>
                                            <p>${grupo.descripcion}</p>
                                        </div>
                                        <span class="badge badge-neutral">${grupo.tipo}</span>
                                    </div>
                                </c:forEach>
                            </div>
                        </article>
                    </section>

                    <section class="panel">
                        <div class="panel-head">
                            <h2>Mensajes recientes</h2>
                            <a href="${pageContext.request.contextPath}/mensajes">Ver todos</a>
                        </div>
                        <p class="panel-intro">Ultimas comunicaciones para ponerte al dia antes de actuar.</p>
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
                    </section>
            </div>
        </body>

        </html>