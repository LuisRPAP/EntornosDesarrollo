<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>RenaceGest | Resumen de grupos</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
        </head>

        <body>
            <div class="app-shell app-shell-fixed">
                <%@ include file="_topnav.jspf" %>

                    <header class="page-head">
                        <div>
                            <p class="eyebrow">Vista de consulta</p>
                            <h1>Resumen de grupos</h1>
                            <p class="lead">Pantalla ligera de previsualizacion para no sobrecargar la gestion de
                                grupos.</p>
                        </div>
                    </header>

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