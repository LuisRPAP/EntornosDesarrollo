# Plan de ejecucion Proyecto Eval3

Este plan esta pensado para trabajar el documento de evaluacion por iteraciones cortas, asegurando que cada cambio se actualiza en todos los artefactos del proyecto.

## Regla de trabajo (modo responsive de analisis)

Cada requisito nuevo se refleja en:

1. Casos de uso.
2. Diseno procedimental (secuencia o actividad).
3. Modelo entidad-relacion.
4. Glosario de reglas y estados (este documento).

Si un requisito no impacta en uno de los cuatro, se deja una nota justificando por que.

## Estado actual (base ya implementada)

Se han propagado capacidades transversales:

1. Registro de incidencias.
2. Calendario de actos.
3. Notificaciones de cambios.
4. Auditoria de trazabilidad.

## Plantilla por punto del enunciado

Copia este bloque para cada punto del PDF y completalo:

### Punto X - [Titulo del enunciado]

- Objetivo funcional:
- Actores implicados:
- Reglas de negocio:
- Datos nuevos o cambios de datos:
- Riesgos:

Impacto por artefacto:

1. Casos de uso: [pendiente]
2. Secuencia/actividad: [pendiente]
3. Entidad-relacion: [pendiente]
4. Pruebas/validacion: [pendiente]

Criterio de terminado:

- [ ] Caso de uso actualizado
- [ ] Secuencia actualizada
- [ ] ER actualizado
- [ ] Regla de negocio documentada
- [ ] Sin contradicciones con puntos anteriores

## Backlog tecnico propuesto

1. Homogeneizar nombres de estados en todo el modelo.
2. Anadir diagrama de estados para vida de pertrecho e incidencia.
3. Anadir restricciones de integridad para auditoria y notificaciones.
4. Definir matriz de permisos por rango (Maestre, Sargento, Guardia, Publico).
5. Anadir casos excepcionales y alternativos en secuencias.

## Cronograma de trabajo acordado (a partir de tu memoria tecnica)

### Iteracion 1 - Base funcional y reglas nucleares

1. RF-01 Alta IA con umbral de confianza.
2. RF-02 Calculo de gracia/infamia por integridad.
3. RF-03 Bloqueo de prestamos sensibles por infamia.
4. RF-04 Acceso QR publico con ocultacion interna.

Estado: completado en diagramas y especificacion v1.

### Iteracion 2 - Capa de datos y script tecnico

1. Consolidar script SQL unico del proyecto.
2. Incluir triggers de autorizacion y reglas de honor.
3. Validar coherencia entre SQL y modelo ER.

Estado: pendiente.

### Iteracion 3 - Calidad y defensa

1. Bateria de pruebas de caja negra trazada a RF/RNF.
2. Preparar guion de defensa tecnica (objetivos, arquitectura, pruebas).
3. Checklist de entrega final.

Estado: en progreso (pruebas base creadas).
