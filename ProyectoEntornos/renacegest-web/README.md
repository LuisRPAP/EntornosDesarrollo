# RenaceGest Web

Aplicacion web Maven para la gestion de Guardias de Santiago.

## Requisitos

- Java 17+
- Maven 3.9+
- Tomcat 11.x (para despliegue WAR)

## Ejecutar

```bash
mvn clean package
mvn jetty:run
```

Abrir:

- `http://localhost:8080/renacegest-web`

## Recuperacion de clave

Mientras no quede activo un proveedor de correo, la recuperacion funciona con una frase secreta guardada por guardia.

Flujo actual:

1. El Maestre crea o edita un guardia desde la pantalla de guardias.
2. Indica una frase de recuperacion.
3. El usuario entra en `/recuperar-clave`, escribe su apodo, la frase y la nueva clave.

Cuando quieras activar el envio por correo, el proyecto ya deja preparados:

1. El campo de correo de recuperacion en el CRUD de guardias.
2. La tabla `guardias_recuperacion`.
3. La pantalla pública de recuperacion en `/recuperar-clave`.

Con eso solo faltaria conectar un SMTP gratuito con sus credenciales y, si lo prefieres, añadir un paso extra de verificacion por codigo enviado al correo.

## Despliegue manual en Tomcat 11 (WAR)

1. Compilar y empaquetar en la raiz del proyecto:

```bash
mvn clean
mvn compile package
```

2. Verificar que existe el artefacto:

```text
target/renacegest-web.war
```

3. Detener el servicio de Tomcat.
4. Copiar `target/renacegest-web.war` a la carpeta `webapps` de Tomcat.
5. Arrancar de nuevo el servicio de Tomcat.
6. Abrir:

```text
http://localhost:8080/renacegest-web/login
http://localhost:8080/renacegest-web/home
```

### Si aparece 404

- Confirmar que el WAR se llama exactamente `renacegest-web.war` dentro de `webapps`.
- Confirmar que no hay otra app con el mismo contexto en otro Tomcat/puerto.
- Revisar logs de arranque de Tomcat (`logs/catalina*.log`) por errores de despliegue.
- Si hubo intentos fallidos previos, borrar la carpeta desplegada `webapps/renacegest-web` y volver a copiar el WAR con Tomcat detenido.

## Estructura

- `com.renacegest.model`: entidades de dominio.
- `com.renacegest.dao`: repositorio en memoria.
- `com.renacegest.servlet`: controladores web.
- `src/main/webapp/WEB-INF/jsp`: vistas JSP.
