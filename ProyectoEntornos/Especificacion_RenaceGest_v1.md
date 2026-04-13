# Especificacion funcional RenaceGest v1

## 1. Alcance

RenaceGest es un sistema para gestionar activos historicos, prestamos y control de honor de la Asociacion Guardias de Santiago.

Objetivos:

1. Gestion de miembros, maestranza, pertrechos y alardes.
2. Clasificacion asistida por IA en altas de pertrechos.
3. Control de gracia/infamia como regla de preservacion.
4. Acceso publico mediante QR sin exponer informacion interna.

## 2. Roles y permisos

1. Maestre (administracion y validacion global).
2. Sargento (operacion y control logistico).
3. Guardia (solicitudes y devoluciones).
4. Amigo (consulta publica por QR).

## 3. Requisitos funcionales

| ID | Nombre | Regla principal |
|---|---|---|
| RF-01 | Alta por IA | Si confianza > 80%, clasificacion automatica; en caso contrario requiere validacion manual o masiva. |
| RF-02 | Calculo de Honor | En devolucion, integridad < 70% aplica tacha y resta puntos de gracia. |
| RF-03 | Bloqueo por Infamia | Si puntos_gracia < 20 o estado_honor = Infame, se bloquea prestamo de Armeria y Caballeria. |
| RF-04 | Acceso QR Publico | El amigo visualiza historia del pertrecho y contenido publico, ocultando precio, ubicacion y datos internos. |

## 4. Requisitos no funcionales

| ID | Nombre | Criterio |
|---|---|---|
| RNF-01 | Seguridad | RBAC por rol y trazabilidad de cambios (auditoria). |
| RNF-02 | Disponibilidad | Flujo operativo compatible con uso movil en campamentos. |
| RNF-03 | Escalabilidad social | Capacidad para interacciones de galeria y etiquetado social. |

## 4.1 Capa de comunicacion interna

| ID | Nombre | Criterio |
|---|---|---|
| RF-05 | Misiones y grupos de trabajo | Los miembros pueden agruparse en misiones con un jefe de equipo que puede dar o quitar miembros. |
| RF-06 | Mensajeria de grupo | El Maestre puede enviar mensajes a un grupo o a todos los grupos, y los miembros reciben notificacion. |
| RF-07 | Permisos de administracion de grupo | Solo el jefe de equipo o el Maestre pueden modificar la composicion de un grupo. |

Reglas de negocio adicionales:

1. Un grupo puede estar activo o inactivo.
2. Cada grupo tiene un jefe de equipo responsable.
3. El Maestre puede publicar un mensaje con alcance global.
4. Todo cambio de composicion de grupo deja traza de auditoria.

## 5. Reglas de negocio consolidadas

1. Puntos de gracia acotados entre 0 y 100.
2. La clasificacion de IA debe registrar su confianza para auditoria.
3. Todo cambio relevante en prestamos, incidencias o validaciones genera notificacion.
4. Todo proceso de correccion manual de IA deja traza de auditoria.

## 6. Trazabilidad a artefactos actuales

1. Casos de uso: ProyectoEntornos/casosDeUsos.puml
2. Secuencia: ProyectoEntornos/disenoProcedimental.puml
3. Entidad relacion: ProyectoEntornos/entidadRelacion.puml

## 7. Criterio de terminado para cada nuevo punto del enunciado

1. Requisito identificado y numerado.
2. Impacto reflejado en casos de uso, secuencia y ER.
3. Escenario de prueba de caja negra definido.
4. Regla de seguridad o validacion documentada.

5. Si es comunicacion interna, definir permisos, alcance y trazabilidad del mensaje.
