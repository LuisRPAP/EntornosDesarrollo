# Checklist rapida de Workbench y RenaceGest

## 1) Preparar MySQL
- Abrir MySQL Workbench y conectar al servidor local.
- Verificar que MySQL esta arrancado.
- Confirmar credenciales usadas por la app:
  - host: `localhost`
  - puerto: `3306`
  - usuario: `root`
  - clave: `root`

## 2) Cargar el esquema
- Abrir el script [schema-renagest-full.sql](schema-renagest-full.sql).
- Ejecutarlo una sola vez.
- Comprobar que se crean estas bases:
  - `renagest`
  - `renagest_prueba`
- Comprobar que aparecen todas las tablas en ambas bases:
  - `guardias`
  - `secciones_maestranza`
  - `grupos_mision`
  - `miembros_grupo`
  - `mensajes_comunicacion`
  - `pertrechos`
  - `historico_alardes`
  - `fotos_publicas`
  - `etiquetas_fotos`
  - `valoraciones_fotos`

## 3) Verificar la app
- Arrancar la aplicacion web.
- Entrar por login.
- Elegir entorno:
  - `PRUEBA` para pruebas
  - `REAL` para datos definitivos
- Confirmar que los datos cargan y que no se mezclan entre entornos.

## 4) Pruebas rapidas
- Abrir el listado avanzado.
- Probar una busqueda simple.
- Probar una busqueda compleja con paréntesis.
- Probar una importacion CSV en entorno `PRUEBA`.
- Revisar que el resultado aparece en la tabla correcta.

## 5) Si algo falla
- Revisar que el script se ejecuto en la base correcta.
- Revisar que la URL JDBC coincide con tu MySQL local.
- Revisar que el usuario y la clave son correctos.
- Revisar que el entorno seleccionado en login es el esperado.
