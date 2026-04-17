<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>RenaceGest | Ficha QR</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
        </head>

        <body>
            <div class="app-shell">
                <%@ include file="_topnav.jspf" %>
                    <header class="page-head">
                        <div>
                            <p class="eyebrow">Ficha publica por QR</p>
                            <h1>Historia del pertrecho</h1>
                        </div>
                        <a class="button button-secondary" href="${pageContext.request.contextPath}/amigos">Volver a
                            galeria</a>
                    </header>

                    <c:choose>
                        <c:when test="${empty pertrecho}">
                            <section class="panel">
                                <h2>Token no valido</h2>
                                <p>No se encontro ningun pertrecho publico para el token: ${token}</p>
                            </section>
                        </c:when>
                        <c:otherwise>
                            <section class="panel">
                                <h2>${pertrecho.descripcion}</h2>
                                <p><strong>Seccion:</strong> ${pertrecho.seccionNombre}</p>
                                <p><strong>Token:</strong> ${pertrecho.tokenQr}</p>
                                <p>
                                    Esta ficha muestra solo informacion de difusion historica. No expone ubicaciones,
                                    autorizadores ni datos internos de custodia.
                                </p>
                            </section>

                            <section class="panel">
                                <div class="panel-head">
                                    <h2>Huellas de prestamo</h2>
                                </div>
                                <div class="timeline">
                                    <c:forEach items="${historial}" var="alarde">
                                        <article class="message-card">
                                            <div class="message-meta">
                                                <strong>Salida: ${alarde.fechaSalida}</strong>
                                                <span>Entrada: ${empty alarde.fechaEntrada ? 'Pendiente' :
                                                    alarde.fechaEntrada}</span>
                                            </div>
                                            <p>Guardia participante: ${alarde.guardiaApodo}</p>
                                            <small>
                                                ${alarde.ticketMaestranza ? 'La pieza paso por revision de maestranza.'
                                                : 'Custodia sin incidencias graves.'}
                                            </small>
                                        </article>
                                    </c:forEach>
                                </div>
                            </section>
                        </c:otherwise>
                    </c:choose>
            </div>
        </body>

        </html>