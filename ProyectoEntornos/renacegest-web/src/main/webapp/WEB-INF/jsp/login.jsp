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
                <%@ include file="_topnav.jspf" %>
                    <header class="page-head">
                        <div>
                            <div class="brand-lockup">
                                <img class="brand-mark"
                                    src="${pageContext.request.contextPath}/assets/img/brand/marca-gds-centrada.png"
                                    alt="Emblema de la guardia">
                                <p class="eyebrow">Control de acceso</p>
                            </div>
                            <h1>Acceso por rol</h1>
                            <p class="lead">Entra en tres pasos: elige entorno, elige tu rol y escribe tu usuario.</p>
                        </div>
                    </header>

                    <section class="panel" style="margin-top: 18px;">
                        <h2>Antes de empezar</h2>
                        <ol class="step-list">
                            <li>Si estas aprendiendo o probando, usa el entorno PRUEBA.</li>
                            <li>Si no sabes tu rol exacto, consulta primero con un Maestre o Sargento.</li>
                            <li>Si olvidas la clave, usa el enlace de recuperacion al final del formulario.</li>
                        </ol>
                    </section>

                    <c:if test="${param.error == 'acceso'}">
                        <div class="notice notice-danger">No tienes permisos para acceder a esa seccion.</div>
                    </c:if>
                    <c:if test="${param.error == 'rol'}">
                        <div class="notice notice-danger">Debes seleccionar un rol valido.</div>
                    </c:if>
                    <c:if test="${param.error == 'usuario'}">
                        <div class="notice notice-danger">Debes seleccionar un usuario valido.</div>
                    </c:if>
                    <c:if test="${param.error == 'rango'}">
                        <div class="notice notice-danger">El rol seleccionado no coincide con el rango del usuario.
                        </div>
                    </c:if>
                    <c:if test="${param.error == 'clave'}">
                        <div class="notice notice-danger">La clave de acceso es obligatoria o no es correcta.</div>
                    </c:if>
                    <c:if test="${param.error == 'recuperada'}">
                        <div class="notice notice-info">La clave se ha actualizado correctamente. Ya puedes entrar con
                            la
                            nueva clave.</div>
                    </c:if>
                    <c:if test="${param.error == 'db'}">
                        <div class="notice notice-danger">No se pudo conectar con la base de datos. Revisa credenciales
                            y
                            permisos MySQL.</div>
                    </c:if>
                    <c:if test="${not empty dbErrorMessage}">
                        <div class="notice notice-danger">${dbErrorMessage}</div>
                    </c:if>

                    <section class="panel" style="max-width: 620px; margin: 22px auto 0;">
                        <h2>Iniciar sesion</h2>
                        <p class="panel-intro">Completa cada campo de arriba a abajo. Si te equivocas, el sistema te
                            indicara exactamente que revisar.</p>
                        <form class="form-grid" method="post" action="${pageContext.request.contextPath}/login">
                            <label>
                                Entorno de datos
                                <p class="field-note">PRUEBA no afecta a los datos reales. REAL es para uso definitivo.
                                </p>
                                <select name="dbProfile" required>
                                    <option value="PRUEBA" ${selectedDbProfile=='PRUEBA' ? 'selected' : '' }>PRUEBA
                                        (recomendado para test)</option>
                                    <option value="REAL" ${selectedDbProfile=='REAL' ? 'selected' : '' }>REAL (datos
                                        definitivos)</option>
                                </select>
                            </label>
                            <label>
                                Rol
                                <p class="field-note">Selecciona tu funcion dentro de la aplicacion.</p>
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
                                <p class="field-note">Puedes escribir el apodo directamente. Ejemplo: MaestreLupo.</p>
                                <input type="text" name="guardiaId" list="guardias-list" autocomplete="off"
                                    autocapitalize="off" autocorrect="off" spellcheck="false" placeholder="MaestreLupo">
                                <datalist id="guardias-list">
                                    <c:forEach items="${guardias}" var="guardia">
                                        <option value="${guardia.apodo}">${guardia.apodo} - ${guardia.rango}</option>
                                    </c:forEach>
                                </datalist>
                            </label>
                            <label>
                                Clave de acceso (no aplica para Amigo)
                                <p class="field-note">Si eliges Amigo, puedes dejar la clave vacia.</p>
                                <div class="password-field">
                                    <input id="claveAcceso" type="password" name="claveAcceso" autocomplete="off"
                                        autocapitalize="off" autocorrect="off" spellcheck="false">
                                    <button class="password-toggle" type="button" data-target="claveAcceso"
                                        aria-label="Mostrar u ocultar la clave">Ojo</button>
                                </div>
                            </label>
                            <button class="button button-primary" type="submit">Entrar al sistema</button>
                        </form>
                        <div class="help-grid">
                            <article class="help-card">
                                <strong>¿Olvidaste la clave?</strong>
                                <p><a href="${pageContext.request.contextPath}/recuperar-clave">Recuperar o cambiar
                                        clave</a></p>
                            </article>
                            <article class="help-card">
                                <strong>Consejo</strong>
                                <p>Si es tu primera vez, entra en PRUEBA para aprender sin riesgo.</p>
                            </article>
                        </div>
                    </section>
            </div>
            <script>
                document.querySelectorAll('.password-toggle').forEach(function (button) {
                    button.addEventListener('click', function () {
                        var target = document.getElementById(button.getAttribute('data-target'));
                        if (!target) return;
                        target.type = target.type === 'password' ? 'text' : 'password';
                        button.textContent = target.type === 'password' ? 'Ojo' : 'Ocultar';
                    });
                });
            </script>
        </body>

        </html>