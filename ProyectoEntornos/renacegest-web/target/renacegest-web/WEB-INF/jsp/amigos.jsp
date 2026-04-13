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
    <header class="page-head">
        <div>
            <p class="eyebrow">Acceso cultural</p>
            <h1>Galeria publica</h1>
            <p class="lead">Consulta publica para Amigos de la Guardia. Sin datos internos de inventario.</p>
        </div>
        <div class="hero-actions">
            <a class="button button-secondary" href="${pageContext.request.contextPath}/home">Portal interno</a>
            <a class="button button-primary" href="${pageContext.request.contextPath}/login">Login por rol</a>
        </div>
    </header>

    <section class="group-grid">
        <c:forEach items="${pertrechos}" var="pertrecho">
            <article class="group-card">
                <strong>${pertrecho.descripcion}</strong>
                <p>Seccion historica: ${pertrecho.seccionNombre}</p>
                <p>Token de visita: ${pertrecho.tokenQr}</p>
                <a class="button button-primary" href="${pageContext.request.contextPath}/qr?token=${pertrecho.tokenQr}">Abrir ficha QR</a>
            </article>
        </c:forEach>
    </section>
</div>
</body>
</html>
