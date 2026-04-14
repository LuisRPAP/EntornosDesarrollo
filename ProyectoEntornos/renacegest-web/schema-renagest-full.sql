-- RenaceGest - Script completo de instalacion
-- Ejecutar una sola vez en MySQL Workbench.
-- Crea ambas bases de datos y todas las tablas necesarias.

CREATE DATABASE IF NOT EXISTS renagest
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS renagest_prueba
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- =========================
-- Base de datos REAL
-- =========================
USE renagest;

CREATE TABLE IF NOT EXISTS guardias (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_real VARCHAR(255) NOT NULL,
    apodo VARCHAR(100) UNIQUE NOT NULL,
    rango VARCHAR(50) NOT NULL,
    clave_acceso VARCHAR(255) NOT NULL,
    puntos_gracia INT DEFAULT 100,
    estado_honor VARCHAR(50) DEFAULT 'Activo',
    maestre_activo BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS secciones_maestranza (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_seccion VARCHAR(100) UNIQUE NOT NULL,
    responsable_id BIGINT NOT NULL,
    responsable_apodo VARCHAR(100) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (responsable_id) REFERENCES guardias(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS grupos_mision (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_grupo VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(50) NOT NULL,
    jefe_equipo VARCHAR(100) NOT NULL,
    creado_por VARCHAR(100) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS miembros_grupo (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    grupo_id BIGINT NOT NULL,
    miembro_id BIGINT NOT NULL,
    apodo VARCHAR(100) NOT NULL,
    nombre_real VARCHAR(255),
    rol_en_grupo VARCHAR(50) DEFAULT 'Miembro',
    puede_modificar_miembros BOOLEAN DEFAULT FALSE,
    fecha_alta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (grupo_id) REFERENCES grupos_mision(id) ON DELETE CASCADE,
    FOREIGN KEY (miembro_id) REFERENCES guardias(id) ON DELETE CASCADE,
    UNIQUE KEY unique_miembro_grupo (grupo_id, miembro_id)
);

CREATE TABLE IF NOT EXISTS mensajes_comunicacion (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    emisor_id BIGINT NOT NULL,
    emisor_apodo VARCHAR(100) NOT NULL,
    grupo_id BIGINT,
    grupo_nombre VARCHAR(100),
    contenido TEXT NOT NULL,
    es_broadcast BOOLEAN DEFAULT FALSE,
    visible BOOLEAN DEFAULT TRUE,
    fecha_mensaje TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (emisor_id) REFERENCES guardias(id) ON DELETE CASCADE,
    FOREIGN KEY (grupo_id) REFERENCES grupos_mision(id) ON DELETE SET NULL,
    INDEX idx_fecha_mensaje (fecha_mensaje DESC)
);

CREATE TABLE IF NOT EXISTS pertrechos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    seccion_id BIGINT NOT NULL,
    seccion_nombre VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    integridad INT DEFAULT 100,
    estado_ia VARCHAR(50) DEFAULT 'Pendiente',
    token_qr VARCHAR(50) UNIQUE,
    disponible BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seccion_id) REFERENCES secciones_maestranza(id) ON DELETE RESTRICT,
    INDEX idx_token_qr (token_qr)
);

CREATE TABLE IF NOT EXISTS historico_alardes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    guardia_id BIGINT NOT NULL,
    guardia_apodo VARCHAR(100) NOT NULL,
    pertrecho_id BIGINT NOT NULL,
    pertrecho_descripcion TEXT NOT NULL,
    autorizador_id BIGINT NOT NULL,
    autorizador_apodo VARCHAR(100) NOT NULL,
    fecha_salida TIMESTAMP NOT NULL,
    fecha_entrada TIMESTAMP,
    observaciones TEXT,
    ticket_maestranza BOOLEAN DEFAULT FALSE,
    delta_gracia INT DEFAULT 0,
    integridad_salida INT NOT NULL,
    integridad_entrada INT,
    FOREIGN KEY (guardia_id) REFERENCES guardias(id) ON DELETE RESTRICT,
    FOREIGN KEY (pertrecho_id) REFERENCES pertrechos(id) ON DELETE RESTRICT,
    FOREIGN KEY (autorizador_id) REFERENCES guardias(id) ON DELETE RESTRICT,
    INDEX idx_fecha_salida (fecha_salida DESC),
    INDEX idx_guardia_alarde (guardia_id)
);

CREATE TABLE IF NOT EXISTS fotos_publicas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    lugar_evento VARCHAR(255),
    fecha_evento VARCHAR(50),
    url_imagen VARCHAR(255) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_titulo (titulo)
);

CREATE TABLE IF NOT EXISTS etiquetas_fotos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    foto_id BIGINT NOT NULL,
    nombre_persona VARCHAR(255) NOT NULL,
    etiquetado_por VARCHAR(100) NOT NULL,
    usuario_red VARCHAR(100),
    fecha_etiqueta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (foto_id) REFERENCES fotos_publicas(id) ON DELETE CASCADE,
    UNIQUE KEY unique_etiqueta (foto_id, nombre_persona),
    INDEX idx_foto_etiqueta (foto_id)
);

CREATE TABLE IF NOT EXISTS valoraciones_fotos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    foto_id BIGINT NOT NULL,
    puntuacion INT NOT NULL CHECK (puntuacion >= 1 AND puntuacion <= 5),
    comentario TEXT,
    visitante VARCHAR(100) NOT NULL,
    usuario_red VARCHAR(100),
    fecha_valoracion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (foto_id) REFERENCES fotos_publicas(id) ON DELETE CASCADE,
    INDEX idx_foto_valoracion (foto_id),
    INDEX idx_fecha_valoracion (fecha_valoracion DESC)
);

-- =========================
-- Base de datos PRUEBA
-- =========================
USE renagest_prueba;

CREATE TABLE IF NOT EXISTS guardias (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_real VARCHAR(255) NOT NULL,
    apodo VARCHAR(100) UNIQUE NOT NULL,
    rango VARCHAR(50) NOT NULL,
    clave_acceso VARCHAR(255) NOT NULL,
    puntos_gracia INT DEFAULT 100,
    estado_honor VARCHAR(50) DEFAULT 'Activo',
    maestre_activo BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS secciones_maestranza (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_seccion VARCHAR(100) UNIQUE NOT NULL,
    responsable_id BIGINT NOT NULL,
    responsable_apodo VARCHAR(100) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (responsable_id) REFERENCES guardias(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS grupos_mision (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_grupo VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(50) NOT NULL,
    jefe_equipo VARCHAR(100) NOT NULL,
    creado_por VARCHAR(100) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS miembros_grupo (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    grupo_id BIGINT NOT NULL,
    miembro_id BIGINT NOT NULL,
    apodo VARCHAR(100) NOT NULL,
    nombre_real VARCHAR(255),
    rol_en_grupo VARCHAR(50) DEFAULT 'Miembro',
    puede_modificar_miembros BOOLEAN DEFAULT FALSE,
    fecha_alta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (grupo_id) REFERENCES grupos_mision(id) ON DELETE CASCADE,
    FOREIGN KEY (miembro_id) REFERENCES guardias(id) ON DELETE CASCADE,
    UNIQUE KEY unique_miembro_grupo (grupo_id, miembro_id)
);

CREATE TABLE IF NOT EXISTS mensajes_comunicacion (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    emisor_id BIGINT NOT NULL,
    emisor_apodo VARCHAR(100) NOT NULL,
    grupo_id BIGINT,
    grupo_nombre VARCHAR(100),
    contenido TEXT NOT NULL,
    es_broadcast BOOLEAN DEFAULT FALSE,
    visible BOOLEAN DEFAULT TRUE,
    fecha_mensaje TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (emisor_id) REFERENCES guardias(id) ON DELETE CASCADE,
    FOREIGN KEY (grupo_id) REFERENCES grupos_mision(id) ON DELETE SET NULL,
    INDEX idx_fecha_mensaje (fecha_mensaje DESC)
);

CREATE TABLE IF NOT EXISTS pertrechos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    seccion_id BIGINT NOT NULL,
    seccion_nombre VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    integridad INT DEFAULT 100,
    estado_ia VARCHAR(50) DEFAULT 'Pendiente',
    token_qr VARCHAR(50) UNIQUE,
    disponible BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seccion_id) REFERENCES secciones_maestranza(id) ON DELETE RESTRICT,
    INDEX idx_token_qr (token_qr)
);

CREATE TABLE IF NOT EXISTS historico_alardes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    guardia_id BIGINT NOT NULL,
    guardia_apodo VARCHAR(100) NOT NULL,
    pertrecho_id BIGINT NOT NULL,
    pertrecho_descripcion TEXT NOT NULL,
    autorizador_id BIGINT NOT NULL,
    autorizador_apodo VARCHAR(100) NOT NULL,
    fecha_salida TIMESTAMP NOT NULL,
    fecha_entrada TIMESTAMP,
    observaciones TEXT,
    ticket_maestranza BOOLEAN DEFAULT FALSE,
    delta_gracia INT DEFAULT 0,
    integridad_salida INT NOT NULL,
    integridad_entrada INT,
    FOREIGN KEY (guardia_id) REFERENCES guardias(id) ON DELETE RESTRICT,
    FOREIGN KEY (pertrecho_id) REFERENCES pertrechos(id) ON DELETE RESTRICT,
    FOREIGN KEY (autorizador_id) REFERENCES guardias(id) ON DELETE RESTRICT,
    INDEX idx_fecha_salida (fecha_salida DESC),
    INDEX idx_guardia_alarde (guardia_id)
);

CREATE TABLE IF NOT EXISTS fotos_publicas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    lugar_evento VARCHAR(255),
    fecha_evento VARCHAR(50),
    url_imagen VARCHAR(255) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_titulo (titulo)
);

CREATE TABLE IF NOT EXISTS etiquetas_fotos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    foto_id BIGINT NOT NULL,
    nombre_persona VARCHAR(255) NOT NULL,
    etiquetado_por VARCHAR(100) NOT NULL,
    usuario_red VARCHAR(100),
    fecha_etiqueta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (foto_id) REFERENCES fotos_publicas(id) ON DELETE CASCADE,
    UNIQUE KEY unique_etiqueta (foto_id, nombre_persona),
    INDEX idx_foto_etiqueta (foto_id)
);

CREATE TABLE IF NOT EXISTS valoraciones_fotos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    foto_id BIGINT NOT NULL,
    puntuacion INT NOT NULL CHECK (puntuacion >= 1 AND puntuacion <= 5),
    comentario TEXT,
    visitante VARCHAR(100) NOT NULL,
    usuario_red VARCHAR(100),
    fecha_valoracion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (foto_id) REFERENCES fotos_publicas(id) ON DELETE CASCADE,
    INDEX idx_foto_valoracion (foto_id),
    INDEX idx_fecha_valoracion (fecha_valoracion DESC)
);
