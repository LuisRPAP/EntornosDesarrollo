<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>RenaceGest | Amigos de la Guardia</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
        </head>

        <body>
            <div class="app-shell">
                <%@ include file="_topnav.jspf" %>
                    <header class="page-head">
                        <div>
                            <p class="eyebrow">Acceso cultural</p>
                            <h1>Galeria publica</h1>
                            <p class="lead">Consulta publica para Amigos de la Guardia, con galeria social, etiquetado y
                                valoraciones.</p>
                        </div>
                        <div class="hero-actions">
                            <a class="button button-secondary" href="${pageContext.request.contextPath}/home">Portal
                                interno</a>
                            <a class="button button-primary" href="${pageContext.request.contextPath}/login">Login por
                                rol</a>
                        </div>
                    </header>

                    <c:if test="${not empty estado}">
                        <div class="notice">${estado}</div>
                    </c:if>

                    <section class="panel" style="margin-top: 18px;">
                        <h2>Comunidad y redes</h2>
                        <p class="lead">Etiqueta personas en las fotos, comparte tu usuario de red y deja una valoracion
                            para amplificar el alcance social de la asociacion.</p>
                    </section>

                    <section class="gallery-grid">
                        <c:forEach items="${fotosGaleria}" var="foto">
                            <article class="gallery-card">
                                <img class="gallery-image" src="${pageContext.request.contextPath}/${foto.urlImagen}"
                                    alt="${foto.titulo}">
                                <div class="gallery-body">
                                    <h3>${foto.titulo}</h3>
                                    <p>${foto.descripcion}</p>
                                    <p><strong>Lugar:</strong> ${foto.lugarEvento}</p>
                                    <p><strong>Fecha:</strong> ${foto.fechaEvento}</p>
                                    <p><strong>Valoracion media:</strong> ${foto.mediaValoracion} / 5
                                        (${foto.valoraciones.size()} valoraciones)</p>

                                    <div class="tag-cloud">
                                        <c:forEach items="${foto.etiquetas}" var="etiqueta">
                                            <span class="member-pill">
                                                ${etiqueta.nombrePersona}
                                                <c:if test="${not empty etiqueta.usuarioRed}">· ${etiqueta.usuarioRed}
                                                </c:if>
                                            </span>
                                        </c:forEach>
                                    </div>

                                    <form class="form-grid" method="post"
                                        action="${pageContext.request.contextPath}/amigos">
                                        <input type="hidden" name="accion" value="etiquetar">
                                        <input type="hidden" name="fotoId" value="${foto.id}">
                                        <label>
                                            Persona en la foto
                                            <input type="text" name="nombrePersona" required>
                                        </label>
                                        <label>
                                            Tu nombre
                                            <input type="text" name="etiquetadoPor" placeholder="Visitante">
                                        </label>
                                        <label>
                                            Usuario red social
                                            <input type="text" name="usuarioRedEtiqueta" placeholder="@usuario">
                                        </label>
                                        <button class="button button-secondary" type="submit">Etiquetar persona</button>
                                    </form>

                                    <form class="form-grid" method="post"
                                        action="${pageContext.request.contextPath}/amigos">
                                        <input type="hidden" name="accion" value="valorar">
                                        <input type="hidden" name="fotoId" value="${foto.id}">
                                        <label>
                                            Puntuacion
                                            <select name="puntuacion" required>
                                                <option value="5">5 - Excelente</option>
                                                <option value="4">4 - Muy buena</option>
                                                <option value="3">3 - Buena</option>
                                                <option value="2">2 - Mejorable</option>
                                                <option value="1">1 - Baja</option>
                                            </select>
                                        </label>
                                        <label>
                                            Comentario
                                            <textarea name="comentario" rows="2"></textarea>
                                        </label>
                                        <label>
                                            Nombre
                                            <input type="text" name="visitante" placeholder="Visitante">
                                        </label>
                                        <label>
                                            Usuario red social
                                            <input type="text" name="usuarioRedValoracion" placeholder="@usuario">
                                        </label>
                                        <button class="button button-primary" type="submit">Publicar valoracion</button>
                                    </form>

                                    <div class="timeline">
                                        <c:forEach items="${foto.valoraciones}" var="valoracion">
                                            <article class="message-card">
                                                <div class="message-meta">
                                                    <strong>${valoracion.visitante}</strong>
                                                    <span>${valoracion.puntuacion} / 5</span>
                                                </div>
                                                <p>${valoracion.comentario}</p>
                                                <small>
                                                    <c:if test="${not empty valoracion.usuarioRed}">
                                                        ${valoracion.usuarioRed} · </c:if>${valoracion.fechaValoracion}
                                                </small>
                                            </article>
                                        </c:forEach>
                                    </div>
                                </div>
                            </article>
                        </c:forEach>
                    </section>

                    <section class="group-grid">
                        <c:forEach items="${pertrechos}" var="pertrecho">
                            <article class="group-card">
                                <strong>${pertrecho.descripcion}</strong>
                                <p>Seccion historica: ${pertrecho.seccionNombre}</p>
                                <p>Token de visita: ${pertrecho.tokenQr}</p>
                                <a class="button button-primary"
                                    href="${pageContext.request.contextPath}/qr?token=${pertrecho.tokenQr}">Abrir ficha
                                    QR</a>
                            </article>
                        </c:forEach>
                    </section>
            </div>
        </body>

        </html>