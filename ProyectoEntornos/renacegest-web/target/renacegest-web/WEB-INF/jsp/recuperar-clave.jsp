<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>RenaceGest | Recuperar clave</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
        </head>

        <body>
            <div class="app-shell">
                <%@ include file="_topnav.jspf" %>
                    <header class="page-head">
                        <div>
                            <p class="eyebrow">Acceso seguro</p>
                            <h1>Recuperar o cambiar clave</h1>
                            <p class="lead">Usa tu apodo y la frase de recuperación para establecer una nueva
                                contraseña.</p>
                        </div>
                    </header>

                    <c:if test="${param.estado == 'usuario'}">
                        <div class="notice">Debes indicar un usuario válido.</div>
                    </c:if>
                    <c:if test="${param.estado == 'frase'}">
                        <div class="notice">La frase de recuperación no coincide o falta.</div>
                    </c:if>
                    <c:if test="${param.estado == 'clave'}">
                        <div class="notice">La nueva clave es obligatoria.</div>
                    </c:if>
                    <c:if test="${param.estado == 'confirma'}">
                        <div class="notice">Las dos claves no coinciden.</div>
                    </c:if>

                    <section class="panel" style="max-width: 620px; margin: 22px auto 0;">
                        <h2>Formulario de recuperación</h2>
                        <form class="form-grid" method="post"
                            action="${pageContext.request.contextPath}/recuperar-clave">
                            <label>
                                Entorno de datos
                                <select name="dbProfile" required>
                                    <option value="PRUEBA" ${selectedDbProfile=='PRUEBA' ? 'selected' : '' }>PRUEBA
                                    </option>
                                    <option value="REAL" ${selectedDbProfile=='REAL' ? 'selected' : '' }>REAL</option>
                                </select>
                            </label>
                            <label>
                                Usuario
                                <input type="text" name="apodo" autocomplete="off" autocapitalize="off"
                                    autocorrect="off" spellcheck="false" required>
                            </label>
                            <label>
                                Frase de recuperación
                                <input type="text" name="fraseRecuperacion" autocomplete="off" autocapitalize="off"
                                    autocorrect="off" spellcheck="false" required>
                            </label>
                            <label>
                                Nueva clave
                                <div class="password-field">
                                    <input id="nuevaClave" type="password" name="nuevaClave" autocomplete="new-password"
                                        autocapitalize="off" autocorrect="off" spellcheck="false" required>
                                    <button class="password-toggle" type="button" data-target="nuevaClave"
                                        aria-label="Mostrar u ocultar la clave">Ojo</button>
                                </div>
                            </label>
                            <label>
                                Confirmar nueva clave
                                <div class="password-field">
                                    <input id="confirmaClave" type="password" name="confirmaClave"
                                        autocomplete="new-password" autocapitalize="off" autocorrect="off"
                                        spellcheck="false" required>
                                    <button class="password-toggle" type="button" data-target="confirmaClave"
                                        aria-label="Mostrar u ocultar la clave">Ojo</button>
                                </div>
                            </label>
                            <button class="button button-primary" type="submit">Cambiar clave</button>
                        </form>
                        <p class="lead">Si más adelante activas correo, esta misma pantalla puede convertirse en el paso
                            1 del flujo por email.</p>
                        <p class="lead"><a href="${pageContext.request.contextPath}/login">Volver al login</a></p>
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