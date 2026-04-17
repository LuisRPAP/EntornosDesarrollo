<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>RenaceGest | Permisos</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
        </head>

        <body>
            <div class="app-shell app-shell-fixed">
                <%@ include file="_topnav.jspf" %>

                    <header class="page-head">
                        <div>
                            <p class="eyebrow">Control de accesos</p>
                            <h1>Permisos por rol e individuales</h1>
                            <p class="lead">Los permisos de usuario tienen prioridad sobre los permisos de rol.</p>
                        </div>
                    </header>

                    <c:if test="${not empty estado}">
                        <div class="notice notice-info">${estado}</div>
                    </c:if>

                    <section class="panel-grid">
                        <article class="panel">
                            <h2>Asignar por rol</h2>
                            <form class="form-grid" method="post" action="${pageContext.request.contextPath}/permisos">
                                <input type="hidden" name="alcance" value="rol">
                                <label>
                                    Rol objetivo
                                    <select name="rolObjetivo" required>
                                        <c:forEach items="${roles}" var="rol">
                                            <option value="${rol}">${rol}</option>
                                        </c:forEach>
                                    </select>
                                </label>
                                <label>
                                    Seccion
                                    <select name="seccion" required>
                                        <c:forEach items="${secciones}" var="seccion">
                                            <option value="${seccion}">${seccion}</option>
                                        </c:forEach>
                                    </select>
                                </label>
                                <label>
                                    Accion
                                    <select name="valor" required>
                                        <option value="permitir">Permitir</option>
                                        <option value="denegar">Denegar</option>
                                    </select>
                                </label>
                                <button class="button button-primary" type="submit">Guardar permiso de rol</button>
                            </form>
                        </article>

                        <article class="panel">
                            <h2>Asignar por usuario</h2>
                            <form class="form-grid" method="post" action="${pageContext.request.contextPath}/permisos">
                                <input type="hidden" name="alcance" value="usuario">
                                <label>
                                    Guardia
                                    <select name="guardiaId" required>
                                        <c:forEach items="${guardias}" var="guardia">
                                            <option value="${guardia.id}">${guardia.apodo} (${guardia.rango})</option>
                                        </c:forEach>
                                    </select>
                                </label>
                                <label>
                                    Seccion
                                    <select name="seccion" required>
                                        <c:forEach items="${secciones}" var="seccion">
                                            <option value="${seccion}">${seccion}</option>
                                        </c:forEach>
                                    </select>
                                </label>
                                <label>
                                    Accion
                                    <select name="valor" required>
                                        <option value="permitir">Permitir</option>
                                        <option value="denegar">Denegar</option>
                                    </select>
                                </label>
                                <button class="button button-secondary" type="submit">Guardar permiso
                                    individual</button>
                            </form>
                        </article>
                    </section>

                    <section class="panel">
                        <h2>Overrides por rol</h2>
                        <div class="timeline">
                            <article class="message-card">
                                <strong>Maestre</strong>
                                <p>${roleOverridesMaestre}</p>
                            </article>
                            <article class="message-card">
                                <strong>Sargento</strong>
                                <p>${roleOverridesSargento}</p>
                            </article>
                            <article class="message-card">
                                <strong>Guardia</strong>
                                <p>${roleOverridesGuardia}</p>
                            </article>
                        </div>
                    </section>

                    <section class="panel">
                        <h2>Overrides por usuario</h2>
                        <form class="form-grid" method="get" action="${pageContext.request.contextPath}/permisos">
                            <label>
                                Ver usuario
                                <select name="guardiaIdVista" required>
                                    <c:forEach items="${guardias}" var="guardia">
                                        <option value="${guardia.id}" ${guardia.id==guardiaIdVista ? 'selected' : '' }>
                                            ${guardia.apodo} (${guardia.rango})</option>
                                    </c:forEach>
                                </select>
                            </label>
                            <button class="button button-secondary" type="submit">Consultar</button>
                        </form>
                        <div class="notice">${userOverridesVista}</div>
                    </section>
            </div>
        </body>

        </html>