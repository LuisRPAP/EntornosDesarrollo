-- Proyecto RenaceGest - Esquema base consolidado
CREATE DATABASE IF NOT EXISTS RENAGEST;
USE RENAGEST;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS auditoria_cambio;
DROP TABLE IF EXISTS mensaje_comunicacion;
DROP TABLE IF EXISTS grupo_mision_miembro;
DROP TABLE IF EXISTS grupo_mision;
DROP TABLE IF EXISTS notificacion;
DROP TABLE IF EXISTS incidencia_alarde;
DROP TABLE IF EXISTS acceso_qr;
DROP TABLE IF EXISTS historico_alarde;
DROP TABLE IF EXISTS permiso_suplencia;
DROP TABLE IF EXISTS pertrecho;
DROP TABLE IF EXISTS seccion_maestranza;
DROP TABLE IF EXISTS miembro;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE miembro (
    id_miembro INT AUTO_INCREMENT PRIMARY KEY,
    nombre_real VARCHAR(100),
    apodo VARCHAR(50) UNIQUE NOT NULL,
    rango ENUM('Maestre', 'Emerito', 'Sargento', 'Guardia') NOT NULL,
    es_maestre_activo BOOLEAN DEFAULT FALSE,
    puntos_gracia INT DEFAULT 100,
    estado_honor ENUM('Activo', 'Infame') DEFAULT 'Activo',
    CONSTRAINT chk_puntos CHECK (puntos_gracia BETWEEN 0 AND 100)
) ENGINE=InnoDB;

CREATE TABLE seccion_maestranza (
    id_seccion INT AUTO_INCREMENT PRIMARY KEY,
    nombre_seccion VARCHAR(50) NOT NULL,
    id_responsable INT,
    CONSTRAINT fk_seccion_responsable FOREIGN KEY (id_responsable)
        REFERENCES miembro(id_miembro) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE pertrecho (
    id_pertrecho INT AUTO_INCREMENT PRIMARY KEY,
    id_seccion INT,
    descripcion TEXT,
    integridad INT DEFAULT 100,
    tipo_sensible ENUM('General', 'Armeria', 'Caballeria') DEFAULT 'General',
    estado_ia ENUM('Validado', 'Dudoso', 'Pendiente') DEFAULT 'Pendiente',
    confianza_ia DECIMAL(4,3) DEFAULT NULL,
    token_qr VARCHAR(100) UNIQUE,
    CONSTRAINT fk_pertrecho_seccion FOREIGN KEY (id_seccion)
        REFERENCES seccion_maestranza(id_seccion)
) ENGINE=InnoDB;

CREATE TABLE permiso_suplencia (
    id_permiso INT AUTO_INCREMENT PRIMARY KEY,
    id_emisor_maestre INT,
    id_receptor_emerito INT,
    fecha_inicio DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_fin DATETIME NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_permiso_emisor FOREIGN KEY (id_emisor_maestre) REFERENCES miembro(id_miembro),
    CONSTRAINT fk_permiso_receptor FOREIGN KEY (id_receptor_emerito) REFERENCES miembro(id_miembro)
) ENGINE=InnoDB;

CREATE TABLE historico_alarde (
    id_alarde INT AUTO_INCREMENT PRIMARY KEY,
    id_guardia INT,
    id_pertrecho INT,
    id_autorizador INT,
    fecha_salida DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_entrada DATETIME,
    evidencia_foto_salida VARCHAR(255),
    evidencia_foto_entrada VARCHAR(255),
    penalizacion_aplicada INT DEFAULT 0,
    observaciones TEXT,
    CONSTRAINT fk_alarde_guardia FOREIGN KEY (id_guardia) REFERENCES miembro(id_miembro),
    CONSTRAINT fk_alarde_pertrecho FOREIGN KEY (id_pertrecho) REFERENCES pertrecho(id_pertrecho),
    CONSTRAINT fk_alarde_autorizador FOREIGN KEY (id_autorizador) REFERENCES miembro(id_miembro)
) ENGINE=InnoDB;

CREATE TABLE incidencia_alarde (
    id_incidencia INT AUTO_INCREMENT PRIMARY KEY,
    id_alarde INT NOT NULL,
    id_reportante INT NOT NULL,
    tipo ENUM('Retraso', 'Dano', 'Perdida', 'Observacion') NOT NULL,
    descripcion TEXT,
    severidad ENUM('Baja', 'Media', 'Alta') DEFAULT 'Media',
    estado ENUM('Abierta', 'EnRevision', 'Cerrada') DEFAULT 'Abierta',
    fecha_reporte DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_incidencia_alarde FOREIGN KEY (id_alarde) REFERENCES historico_alarde(id_alarde),
    CONSTRAINT fk_incidencia_reportante FOREIGN KEY (id_reportante) REFERENCES miembro(id_miembro)
) ENGINE=InnoDB;

CREATE TABLE notificacion (
    id_notificacion INT AUTO_INCREMENT PRIMARY KEY,
    id_miembro_destino INT NOT NULL,
    categoria ENUM('Prestamo', 'Incidencia', 'Acto', 'Sistema') NOT NULL,
    mensaje VARCHAR(255) NOT NULL,
    leida BOOLEAN DEFAULT FALSE,
    fecha_envio DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notificacion_destino FOREIGN KEY (id_miembro_destino) REFERENCES miembro(id_miembro)
) ENGINE=InnoDB;

CREATE TABLE auditoria_cambio (
    id_auditoria INT AUTO_INCREMENT PRIMARY KEY,
    id_actor INT NOT NULL,
    entidad_afectada VARCHAR(50) NOT NULL,
    id_registro_afectado INT NOT NULL,
    accion ENUM('Alta', 'Actualizacion', 'Baja', 'Validacion') NOT NULL,
    detalle TEXT,
    fecha_cambio DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_auditoria_actor FOREIGN KEY (id_actor) REFERENCES miembro(id_miembro)
) ENGINE=InnoDB;

CREATE TABLE acceso_qr (
    id_acceso INT AUTO_INCREMENT PRIMARY KEY,
    id_pertrecho INT NOT NULL,
    tipo_vista ENUM('Publica', 'Interna') DEFAULT 'Publica',
    muestra_historia BOOLEAN DEFAULT TRUE,
    oculta_datos_internos BOOLEAN DEFAULT TRUE,
    fecha_ultimo_acceso DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_qr_pertrecho FOREIGN KEY (id_pertrecho) REFERENCES pertrecho(id_pertrecho)
) ENGINE=InnoDB;

CREATE TABLE grupo_mision (
    id_grupo INT AUTO_INCREMENT PRIMARY KEY,
    nombre_grupo VARCHAR(80) NOT NULL,
    descripcion TEXT,
    tipo ENUM('GrupoTrabajo', 'Mision') DEFAULT 'GrupoTrabajo',
    id_jefe_equipo INT NOT NULL,
    creado_por INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_grupo_jefe FOREIGN KEY (id_jefe_equipo) REFERENCES miembro(id_miembro),
    CONSTRAINT fk_grupo_creado_por FOREIGN KEY (creado_por) REFERENCES miembro(id_miembro)
) ENGINE=InnoDB;

CREATE TABLE grupo_mision_miembro (
    id_grupo_miembro INT AUTO_INCREMENT PRIMARY KEY,
    id_grupo INT NOT NULL,
    id_miembro INT NOT NULL,
    rol_en_grupo ENUM('JefeEquipo', 'Miembro') DEFAULT 'Miembro',
    puede_modificar_miembros BOOLEAN DEFAULT FALSE,
    fecha_alta DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_grupo_miembro_grupo FOREIGN KEY (id_grupo) REFERENCES grupo_mision(id_grupo) ON DELETE CASCADE,
    CONSTRAINT fk_grupo_miembro_miembro FOREIGN KEY (id_miembro) REFERENCES miembro(id_miembro) ON DELETE CASCADE,
    CONSTRAINT uq_grupo_miembro UNIQUE (id_grupo, id_miembro)
) ENGINE=InnoDB;

CREATE TABLE mensaje_comunicacion (
    id_mensaje INT AUTO_INCREMENT PRIMARY KEY,
    id_emisor INT NOT NULL,
    id_grupo INT NULL,
    contenido TEXT NOT NULL,
    es_broadcast BOOLEAN DEFAULT FALSE,
    visible_para_todos BOOLEAN DEFAULT FALSE,
    fecha_envio DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mensaje_emisor FOREIGN KEY (id_emisor) REFERENCES miembro(id_miembro),
    CONSTRAINT fk_mensaje_grupo FOREIGN KEY (id_grupo) REFERENCES grupo_mision(id_grupo)
) ENGINE=InnoDB;

DELIMITER //

DROP TRIGGER IF EXISTS trg_verificar_autorizacion //
CREATE TRIGGER trg_verificar_autorizacion
BEFORE INSERT ON historico_alarde
FOR EACH ROW
BEGIN
    DECLARE v_rango VARCHAR(20);
    DECLARE v_autorizado INT;

    SELECT rango INTO v_rango FROM miembro WHERE id_miembro = NEW.id_autorizador;

    IF v_rango = 'Emerito' THEN
        SELECT COUNT(*) INTO v_autorizado
        FROM permiso_suplencia
        WHERE id_receptor_emerito = NEW.id_autorizador
        AND activo = TRUE
        AND NOW() BETWEEN fecha_inicio AND fecha_fin;

        IF v_autorizado = 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Error: Maestre Emerito sin suplencia activa.';
        END IF;
    END IF;
END //

DROP TRIGGER IF EXISTS trg_bloqueo_infamia //
CREATE TRIGGER trg_bloqueo_infamia
BEFORE INSERT ON historico_alarde
FOR EACH ROW
BEGIN
    DECLARE v_puntos INT;
    DECLARE v_estado VARCHAR(20);
    DECLARE v_tipo VARCHAR(20);

    SELECT puntos_gracia, estado_honor INTO v_puntos, v_estado
    FROM miembro WHERE id_miembro = NEW.id_guardia;

    SELECT tipo_sensible INTO v_tipo
    FROM pertrecho WHERE id_pertrecho = NEW.id_pertrecho;

    IF (v_puntos < 20 OR v_estado = 'Infame') AND v_tipo IN ('Armeria', 'Caballeria') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: Guardia bloqueado por infamia para este tipo de pertrecho.';
    END IF;
END //

DROP TRIGGER IF EXISTS trg_control_grupo_mision //
CREATE TRIGGER trg_control_grupo_mision
BEFORE INSERT ON grupo_mision_miembro
FOR EACH ROW
BEGIN
    DECLARE v_rango_jefe VARCHAR(20);
    DECLARE v_es_jefe INT;

    SELECT COUNT(*) INTO v_es_jefe
    FROM grupo_mision
    WHERE id_grupo = NEW.id_grupo AND id_jefe_equipo = NEW.id_miembro;

    SELECT rango INTO v_rango_jefe
    FROM miembro
    WHERE id_miembro = NEW.id_miembro;

    IF NEW.rol_en_grupo = 'JefeEquipo' AND v_rango_jefe NOT IN ('Maestre', 'Sargento') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: solo Maestre o Sargento puede ser jefe de equipo.';
    END IF;

    IF NEW.puede_modificar_miembros = TRUE AND v_rango_jefe = 'Guardia' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: un Guardia no puede administrar miembros del grupo.';
    END IF;
END //

DROP TRIGGER IF EXISTS trg_mensaje_broadcast //
CREATE TRIGGER trg_mensaje_broadcast
BEFORE INSERT ON mensaje_comunicacion
FOR EACH ROW
BEGIN
    IF NEW.es_broadcast = TRUE AND NEW.visible_para_todos = FALSE THEN
        SET NEW.visible_para_todos = TRUE;
    END IF;
END //

DROP PROCEDURE IF EXISTS sp_jubilar_maestre //
CREATE PROCEDURE sp_jubilar_maestre(
    IN p_id_actual INT,
    IN p_id_sucesor INT
)
BEGIN
    UPDATE miembro
    SET es_maestre_activo = FALSE,
        rango = 'Emerito'
    WHERE id_miembro = p_id_actual;

    UPDATE miembro
    SET es_maestre_activo = TRUE,
        rango = 'Maestre'
    WHERE id_miembro = p_id_sucesor;

    UPDATE permiso_suplencia
    SET activo = FALSE
    WHERE id_emisor_maestre = p_id_actual;
END //

DELIMITER ;

SELECT 'Esquema RENAGEST creado con exito' AS Resultado;
