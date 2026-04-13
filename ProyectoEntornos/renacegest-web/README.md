# RenaceGest Web

Aplicacion web Maven para la gestion de Guardias de Santiago.

## Requisitos

- Java 11+
- Maven 3.9+

## Ejecutar

```bash
mvn clean package
mvn jetty:run
```

Abrir:

- `http://localhost:8080/renacegest`

## Estructura

- `com.renacegest.model`: entidades de dominio.
- `com.renacegest.dao`: repositorio en memoria.
- `com.renacegest.servlet`: controladores web.
- `src/main/webapp/WEB-INF/jsp`: vistas JSP.
