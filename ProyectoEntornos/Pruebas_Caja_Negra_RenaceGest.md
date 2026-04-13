# Pruebas caja negra RenaceGest v1

## Matriz de escenarios

| Escenario | Entrada | Resultado esperado | RF/RNF |
|---|---|---|---|
| Prueba de Infamia | Devolucion con integridad < 70% | Resta puntos de gracia, genera incidencia, registra auditoria y notifica responsables | RF-02, RNF-01 |
| Prueba de Bloqueo | Guardia con puntos_gracia < 20 solicita Armeria | Prestamo denegado por regla de infamia | RF-03, RNF-01 |
| Prueba IA automatica | Foto de morrion con confianza 0.91 | Clasificacion automatica en Armeria | RF-01 |
| Prueba IA manual | Foto ambigua con confianza 0.62 | Solicita validacion manual/masiva por Maestre | RF-01 |
| Prueba masiva | Seleccion de 20 items pendientes | Cambio de estado simultaneo y registro de auditoria | RF-01, RNF-01 |
| Prueba QR publico | Escaneo externo de QR | Muestra historia del pertrecho y oculta datos internos | RF-04, RNF-01 |
| Prueba de trazabilidad | Correccion manual de categoria IA | Existe registro en auditoria con actor, accion y fecha | RNF-01 |
| Prueba grupo | Jefe de equipo añade un Guardia a una mision | El Guardia queda vinculado al grupo y el cambio se audita | RF-05, RF-07 |
| Prueba permiso grupo | Guardia intenta expulsar otro miembro | Acceso denegado por falta de permiso | RF-07, RNF-01 |
| Prueba broadcast | Maestre envia mensaje a todos los grupos | Todos los grupos activos reciben el mensaje | RF-06 |
| Prueba mensajeria grupo | Maestre envia mensaje a una mision concreta | Solo los miembros de ese grupo reciben el mensaje | RF-06 |

## Datos minimos para ejecucion

1. Al menos 3 miembros por rol (Maestre, Sargento, Guardia).
2. Pertrechos de Armeria, Sastreria y Caballeria.
3. 1 guardia con estado activo y otro en infamia.
4. Dataset de imagenes con etiquetas de referencia.

## Criterios de aceptacion de la iteracion 1

1. 100% de escenarios RF criticos ejecutados sin bloqueos tecnicos.
2. Registros de auditoria presentes en operaciones de IA y prestamos.
3. Acceso QR validado desde contexto no autenticado.
