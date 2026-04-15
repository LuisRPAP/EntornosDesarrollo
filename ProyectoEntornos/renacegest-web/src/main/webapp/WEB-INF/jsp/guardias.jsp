<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>RenaceGest | CRUD Guardias</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<div class="app-shell">
    <header class="page-head">
        <div>
            <p class="eyebrow">Administracion interna</p>
            <h1>CRUD de guardias</h1>
        </div>
        <a class="button button-secondary" href="${pageContext.request.contextPath}/home">Volver</a>
    </header>

    <c:if test="${not empty estado}">
        <div class="notice">${estado}</div>
    </c:if>

    <section class="panel-grid">
        <article class="panel">
            <h2>Crear guardia</h2>
            <form class="form-grid" method="post" action="${pageContext.request.contextPath}/guardias">
                <input type="hidden" name="accion" value="crear">
                <label>
                    Nombre real
                    <input type="text" name="nombreReal" required>
                </label>
                <label>
                    Apodo
                    <input type="text" name="apodo" required>
                </label>
                <label>
                    Rango
                    <select name="rango">
                        <option value="Guardia">Guardia</option>
                        <option value="Sargento">Sargento</option>
                        <option value="Maestre">Maestre</option>
                    </select>
                </label>
                <label>
                    Clave de acceso
                    <div class="password-field">
                        <input id="crearClaveAcceso" type="password" name="claveAcceso" autocomplete="new-password" autocapitalize="off" autocorrect="off" spellcheck="false" required>
                        <button class="password-toggle" type="button" data-target="crearClaveAcceso" aria-label="Mostrar u ocultar la clave">Ojo</button>
                    </div>
                </label>
                <label>
                    Correo de recuperacion (opcional)
                    <input type="email" name="correoRecuperacion" placeholder="tu-correo@dominio.com">
                </label>
                <label>
                    Frase de recuperacion
                    <input type="text" name="fraseRecuperacion" placeholder="Mi frase secreta" required>
                </label>
                <label class="checkbox-row">
                    <input type="checkbox" name="maestreActivo" value="true">
                    Maestre activo
                </label>
                <button class="button button-primary" type="submit">Crear</button>
            </form>
        </article>

        <article class="panel">
            <h2>Actualizar guardia</h2>
            <form class="form-grid" method="post" action="${pageContext.request.contextPath}/guardias">
                <input type="hidden" name="accion" value="actualizar">
                <label>
                    Guardia
                    <select name="guardiaId" required>
                        <c:forEach items="${guardias}" var="guardia">
                            <option value="${guardia.id}">${guardia.apodo} (${guardia.rango})</option>
                        </c:forEach>
                    </select>
                </label>
                <label>
                    Nombre real
                    <input type="text" name="nombreReal" required>
                </label>
                <label>
                    Apodo
                    <input type="text" name="apodo" required>
                </label>
                <label>
                    Rango
                    <select name="rango">
                        <option value="Guardia">Guardia</option>
                        <option value="Sargento">Sargento</option>
                        <option value="Maestre">Maestre</option>
                    </select>
                </label>
                <label>
                    Puntos de gracia (0-100)
                    <input type="text" name="puntosGracia" value="100" required>
                </label>
                <label>
                    Estado de honor
                    <select name="estadoHonor">
                        <option value="Activo">Activo</option>
                        <option value="Infame">Infame</option>
                    </select>
                </label>
                <label>
                    Nueva clave de acceso (opcional)
                    <div class="password-field">
                        <input id="actualizarClaveAcceso" type="password" name="claveAcceso" autocomplete="new-password" autocapitalize="off" autocorrect="off" spellcheck="false">
                        <button class="password-toggle" type="button" data-target="actualizarClaveAcceso" aria-label="Mostrar u ocultar la clave">Ojo</button>
                    </div>
                </label>
                <label>
                    Correo de recuperacion (opcional)
                    <input type="email" name="correoRecuperacion" placeholder="tu-correo@dominio.com">
                </label>
                <label>
                    Frase de recuperacion (opcional)
                    <input type="text" name="fraseRecuperacion" placeholder="Mi frase secreta">
                </label>
                <label class="checkbox-row">
                    <input type="checkbox" name="maestreActivo" value="true">
                    Maestre activo
                </label>
                <button class="button button-secondary" type="submit">Actualizar</button>
            </form>
        </article>
    </section>

    <section class="panel">
        <h2>Eliminar guardia</h2>
        <form class="form-grid" method="post" action="${pageContext.request.contextPath}/guardias">
            <input type="hidden" name="accion" value="eliminar">
            <label>
                Guardia
                <select name="guardiaId" required>
                    <c:forEach items="${guardias}" var="guardia">
                        <option value="${guardia.id}">${guardia.apodo} (${guardia.rango})</option>
                    </c:forEach>
                </select>
            </label>
            <button class="button button-secondary" type="submit">Eliminar</button>
        </form>
    </section>

    <section class="panel">
        <h2>Listado actual</h2>
        <div class="timeline">
            <c:forEach items="${guardias}" var="guardia">
                <article class="message-card">
                    <div class="message-meta">
                        <strong>${guardia.apodo} · ${guardia.rango}</strong>
                        <span>${guardia.puntosGracia} puntos</span>
                    </div>
                    <p>${guardia.nombreReal}</p>
                    <small>Estado: ${guardia.estadoHonor}</small>
                </article>
            </c:forEach>
        </div>
    </section>
</div>
<script>
document.querySelectorAll('.password-toggle').forEach(function(button) {
    button.addEventListener('click', function() {
        var target = document.getElementById(button.getAttribute('data-target'));
        if (!target) return;
        target.type = target.type === 'password' ? 'text' : 'password';
        button.textContent = target.type === 'password' ? 'Ojo' : 'Ocultar';
    });
});
</script>
</body>
</html>
