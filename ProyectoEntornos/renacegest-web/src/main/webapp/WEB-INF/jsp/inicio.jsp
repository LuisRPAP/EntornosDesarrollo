<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>RenaceGest | Inicio</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
        </head>

        <body>
            <div class="app-shell app-shell-fixed inicio-shell">
                <%@ include file="_topnav.jspf" %>

                    <header class="hero hero-compact">
                        <div>
                            <p class="eyebrow">Portal principal</p>
                            <h1>Inicio RenaceGest</h1>
                            <p class="lead">Accede a cada seccion desde aqui. Los botones se activan segun rol y
                                permisos.</p>
                        </div>
                    </header>

                    <c:if test="${param.error == 'permiso'}">
                        <div class="notice notice-danger">No tienes permiso para esa seccion.</div>
                    </c:if>

                    <section class="panel panel-compact">
                        <h2>Accesos</h2>
                        <c:if test="${empty currentRole}">
                            <p class="panel-intro">Sesion no iniciada. Entra con tu usuario o usa la vista publica.</p>
                            <div class="button-row">
                                <a class="button button-primary" href="${pageContext.request.contextPath}/login">Iniciar
                                    sesion</a>
                                <a class="button button-secondary"
                                    href="${pageContext.request.contextPath}/amigos">Vista publica</a>
                            </div>
                        </c:if>
                        <c:if test="${not empty currentRole}">
                            <p class="panel-intro">Usuario: ${currentUserName} | Rol: ${currentRole}</p>
                        </c:if>

                        <div class="panel-grid access-grid access-grid-tight">
                            <a class="button ${sectionAccess['home'] ? 'button-primary' : 'button-disabled'}"
                                href="${pageContext.request.contextPath}/home">Panel</a>
                            <a class="button ${sectionAccess['inventario'] ? 'button-primary' : 'button-disabled'}"
                                href="${pageContext.request.contextPath}/inventario">Inventario y prestamos</a>
                            <a class="button ${sectionAccess['grupos'] ? 'button-primary' : 'button-disabled'}"
                                href="${pageContext.request.contextPath}/grupos">Gestion de grupos</a>
                            <a class="button ${sectionAccess['gruposResumen'] ? 'button-primary' : 'button-disabled'}"
                                href="${pageContext.request.contextPath}/grupos-resumen">Resumen de grupos</a>
                            <a class="button ${sectionAccess['mensajes'] ? 'button-primary' : 'button-disabled'}"
                                href="${pageContext.request.contextPath}/mensajes">Mensajes</a>
                            <a class="button ${sectionAccess['listados'] ? 'button-primary' : 'button-disabled'}"
                                href="${pageContext.request.contextPath}/listados">Listados</a>
                            <a class="button ${sectionAccess['importacion'] ? 'button-primary' : 'button-disabled'}"
                                href="${pageContext.request.contextPath}/importacion">Importacion</a>
                            <a class="button ${sectionAccess['guardias'] ? 'button-primary' : 'button-disabled'}"
                                href="${pageContext.request.contextPath}/guardias">Guardias</a>
                            <a class="button ${sectionAccess['permisos'] ? 'button-primary' : 'button-disabled'}"
                                href="${pageContext.request.contextPath}/permisos">Permisos</a>
                        </div>
                        <p class="field-note">Si un boton aparece desactivado, no tienes permiso en este momento.</p>
                    </section>
            </div>
        </body>

        </html>