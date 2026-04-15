<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>RenaceGest | Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<div class="app-shell">
    <header class="page-head">
        <div>
            <div class="brand-lockup">
                <img class="brand-mark" src="${pageContext.request.contextPath}/assets/img/emblema-guardia.svg" alt="Emblema de la guardia">
                <p class="eyebrow">Control de acceso</p>
            </div>
            <h1>Acceso por rol</h1>
            <p class="lead">Selecciona rol y usuario para entrar al entorno interno o vista publica.</p>
        </div>
    </header>

    <c:if test="${param.error == 'acceso'}">
        <div class="notice">No tienes permisos para acceder a esa seccion.</div>
    </c:if>
    <c:if test="${param.error == 'rol'}">
        <div class="notice">Debes seleccionar un rol valido.</div>
    </c:if>
    <c:if test="${param.error == 'usuario'}">
        <div class="notice">Debes seleccionar un usuario valido.</div>
    </c:if>
    <c:if test="${param.error == 'rango'}">
        <div class="notice">El rol seleccionado no coincide con el rango del usuario.</div>
    </c:if>
    <c:if test="${param.error == 'clave'}">
        <div class="notice">La clave de acceso es obligatoria o no es correcta.</div>
    </c:if>
    <c:if test="${param.error == 'recuperada'}">
        <div class="notice">La clave se ha actualizado correctamente. Ya puedes entrar con la nueva clave.</div>
    </c:if>

    <section class="panel" style="max-width: 620px; margin: 22px auto 0;">
        <h2>Iniciar sesion</h2>
        <form class="form-grid" method="post" action="${pageContext.request.contextPath}/login">
            <label>
                Entorno de datos
                <select name="dbProfile" required>
                    <option value="PRUEBA" ${selectedDbProfile == 'PRUEBA' ? 'selected' : ''}>PRUEBA (recomendado para test)</option>
                    <option value="REAL" ${selectedDbProfile == 'REAL' ? 'selected' : ''}>REAL (datos definitivos)</option>
                </select>
            </label>
            <label>
                Rol
                <select name="role" required>
                    <option value="">Selecciona rol</option>
                    <option value="Maestre">Maestre</option>
                    <option value="Sargento">Sargento</option>
                    <option value="Guardia">Guardia</option>
                    <option value="Amigo">Amigo (publico)</option>
                </select>
            </label>
            <label>
                Usuario (escribe apodo o id)
                <input type="text" name="guardiaId" list="guardias-list" autocomplete="off" autocapitalize="off" autocorrect="off" spellcheck="false" placeholder="MaestreLupo">
                <datalist id="guardias-list">
                    <c:forEach items="${guardias}" var="guardia">
                        <option value="${guardia.apodo}">${guardia.apodo} - ${guardia.rango}</option>
                    </c:forEach>
                </datalist>
            </label>
            <label>
                Clave de acceso (no aplica para Amigo)
                <div class="password-field">
                    <input id="claveAcceso" type="password" name="claveAcceso" autocomplete="off" autocapitalize="off" autocorrect="off" spellcheck="false">
                    <button class="password-toggle" type="button" data-target="claveAcceso" aria-label="Mostrar u ocultar la clave">Ojo</button>
                </div>
            </label>
            <button class="button button-primary" type="submit">Entrar</button>
        </form>
        <p class="lead"><a href="${pageContext.request.contextPath}/recuperar-clave">Recuperar o cambiar clave</a></p>
        <p class="lead">Selecciona PRUEBA para validar flujos sin afectar el entorno REAL.</p>
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
