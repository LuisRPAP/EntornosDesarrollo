package com.renacegest.dao;

import com.renacegest.db.DBConnection;
import com.renacegest.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Implementación de RenaceGestRepository usando MySQL como persistencia.
 * Reemplaza la versión en memoria (InMemoryRenaceGestRepository).
 */
public class MySQLRenaceGestRepository implements RenaceGestRepository {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final MySQLRenaceGestRepository INSTANCE_REAL = new MySQLRenaceGestRepository(DBConnection.PROFILE_REAL);
    private static final MySQLRenaceGestRepository INSTANCE_PRUEBA = new MySQLRenaceGestRepository(DBConnection.PROFILE_PRUEBA);

    private final String profile;

    public static MySQLRenaceGestRepository getInstance() {
        return INSTANCE_REAL;
    }

    public static MySQLRenaceGestRepository getRealInstance() {
        return INSTANCE_REAL;
    }

    public static MySQLRenaceGestRepository getPruebaInstance() {
        return INSTANCE_PRUEBA;
    }

    private MySQLRenaceGestRepository(String profile) {
        this.profile = profile;
        try {
            DBConnection.getConnection(profile).close();
            System.out.println("MySQLRenaceGestRepository inicializado para perfil: " + profile);
        } catch (SQLException e) {
            System.err.println("Error en inicialización de DAO: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection(profile);
    }

    // ==================== GUARDIAS ====================

    @Override
    public synchronized List<Guardia> findAllGuardias() {
        String sql = "SELECT id, nombre_real, apodo, rango, clave_acceso, puntos_gracia, estado_honor, maestre_activo FROM guardias ORDER BY id";
        List<Guardia> guardias = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Guardia g = new Guardia(
                        rs.getLong("id"),
                        rs.getString("nombre_real"),
                        rs.getString("apodo"),
                        rs.getString("rango"),
                        rs.getString("clave_acceso"),
                        rs.getInt("puntos_gracia"),
                        rs.getString("estado_honor"),
                        rs.getBoolean("maestre_activo")
                );
                if (!DBConnection.HIDDEN_SUPERUSER_APODO.equalsIgnoreCase(g.getApodo())) {
                    guardias.add(g);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener guardias: " + e.getMessage());
        }

        return guardias;
    }

    @Override
    public synchronized Guardia findGuardiaById(Long guardiaId) {
        String sql = "SELECT id, nombre_real, apodo, rango, clave_acceso, puntos_gracia, estado_honor, maestre_activo FROM guardias WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, guardiaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Guardia(
                            rs.getLong("id"),
                            rs.getString("nombre_real"),
                            rs.getString("apodo"),
                            rs.getString("rango"),
                            rs.getString("clave_acceso"),
                            rs.getInt("puntos_gracia"),
                            rs.getString("estado_honor"),
                            rs.getBoolean("maestre_activo")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener guardia: " + e.getMessage());
        }

        return null;
    }

    @Override
    public synchronized Guardia crearGuardia(String nombreReal, String apodo, String rango, String claveAcceso, boolean maestreActivo, Long solicitanteId) {
        if (!esMaestre(solicitanteId)) {
            throw new IllegalArgumentException("Solo el Maestre puede crear guardias.");
        }

        if (apodo == null || apodo.isBlank()) {
            throw new IllegalArgumentException("El apodo es obligatorio.");
        }

        if (claveAcceso == null || claveAcceso.isBlank() || claveAcceso.trim().length() < 4) {
            throw new IllegalArgumentException("La clave de acceso debe tener al menos 4 caracteres.");
        }

        String sql = "INSERT INTO guardias (nombre_real, apodo, rango, clave_acceso, puntos_gracia, estado_honor, maestre_activo) VALUES (?, ?, ?, ?, 100, 'Activo', ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, nombreReal == null ? "" : nombreReal.trim());
            pstmt.setString(2, apodo.trim());
            pstmt.setString(3, rango == null || rango.isBlank() ? "Guardia" : rango.trim());
            pstmt.setString(4, claveAcceso.trim());
            pstmt.setBoolean(5, maestreActivo);

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    return findGuardiaById(id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear guardia: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public synchronized Guardia actualizarGuardia(Long guardiaId, String nombreReal, String apodo, String rango, String claveAcceso, int puntosGracia, String estadoHonor, boolean maestreActivo, Long solicitanteId) {
        if (!esMaestre(solicitanteId)) {
            throw new IllegalArgumentException("Solo el Maestre puede editar guardias.");
        }

        Guardia guardia = findGuardiaById(guardiaId);
        if (guardia == null) {
            throw new IllegalArgumentException("Guardia no encontrado.");
        }

        String sqlUpdate = "UPDATE guardias SET nombre_real = ?, apodo = ?, rango = ?, clave_acceso = ?, puntos_gracia = ?, estado_honor = ?, maestre_activo = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {

            pstmt.setString(1, nombreReal == null ? guardia.getNombreReal() : nombreReal.trim());
            pstmt.setString(2, apodo == null ? guardia.getApodo() : apodo.trim());
            pstmt.setString(3, rango == null || rango.isBlank() ? guardia.getRango() : rango.trim());

            if (claveAcceso != null && !claveAcceso.isBlank()) {
                if (claveAcceso.trim().length() < 4) {
                    throw new IllegalArgumentException("La clave de acceso debe tener al menos 4 caracteres.");
                }
                pstmt.setString(4, claveAcceso.trim());
            } else {
                pstmt.setString(4, guardia.getClaveAcceso());
            }

            int puntosActualizados = acotar(puntosGracia, 0, 100);
            pstmt.setInt(5, puntosActualizados);

            String estadoFinal = puntosActualizados < 20 ? "Infame" : (estadoHonor == null || estadoHonor.isBlank() ? "Activo" : estadoHonor);
            pstmt.setString(6, estadoFinal);
            pstmt.setBoolean(7, maestreActivo);
            pstmt.setLong(8, guardiaId);

            pstmt.executeUpdate();
            return findGuardiaById(guardiaId);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar guardia: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized boolean eliminarGuardia(Long guardiaId, Long solicitanteId) {
        if (!esMaestre(solicitanteId)) {
            throw new IllegalArgumentException("Solo el Maestre puede eliminar guardias.");
        }

        Guardia guardia = findGuardiaById(guardiaId);
        if (guardia == null) {
            return false;
        }

        // Verificar que no tenga alardes
        String sqlCheck = "SELECT COUNT(*) as count FROM historico_alardes WHERE guardia_id = ? OR autorizador_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlCheck)) {

            pstmt.setLong(1, guardiaId);
            pstmt.setLong(2, guardiaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt("count") > 0) {
                    throw new IllegalArgumentException("No se puede eliminar un guardia con historico de alardes.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar alardes: " + e.getMessage(), e);
        }

        // Eliminar
        String sqlDelete = "DELETE FROM guardias WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {

            pstmt.setLong(1, guardiaId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar guardia: " + e.getMessage(), e);
        }
    }

    // ==================== GRUPOS Y MIEMBROS ====================

    @Override
    public synchronized List<GrupoMision> findAllGrupos() {
        String sql = "SELECT id, nombre_grupo, descripcion, tipo, jefe_equipo, creado_por, activo FROM grupos_mision ORDER BY id";
        List<GrupoMision> grupos = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                GrupoMision g = new GrupoMision(
                        rs.getLong("id"),
                        rs.getString("nombre_grupo"),
                        rs.getString("descripcion"),
                        rs.getString("tipo"),
                        rs.getString("jefe_equipo"),
                        rs.getString("creado_por"),
                        rs.getBoolean("activo")
                );
                grupos.add(g);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener grupos: " + e.getMessage());
        }

        return grupos;
    }

    @Override
    public synchronized GrupoMision findGrupoById(Long grupoId) {
        String sql = "SELECT id, nombre_grupo, descripcion, tipo, jefe_equipo, creado_por, activo FROM grupos_mision WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, grupoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new GrupoMision(
                            rs.getLong("id"),
                            rs.getString("nombre_grupo"),
                            rs.getString("descripcion"),
                            rs.getString("tipo"),
                            rs.getString("jefe_equipo"),
                            rs.getString("creado_por"),
                            rs.getBoolean("activo")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener grupo: " + e.getMessage());
        }

        return null;
    }

    @Override
    public synchronized GrupoMision crearGrupo(String nombreGrupo, String descripcion, String tipo, Long jefeEquipoId, Long creadoPorId) {
        Guardia jefeEquipo = findGuardiaById(jefeEquipoId);
        Guardia creador = findGuardiaById(creadoPorId);

        if (jefeEquipo == null || creador == null) {
            throw new IllegalArgumentException("No se ha podido crear el grupo por datos invalidos.");
        }

        String sql = "INSERT INTO grupos_mision (nombre_grupo, descripcion, tipo, jefe_equipo, creado_por, activo) VALUES (?, ?, ?, ?, ?, TRUE)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, nombreGrupo);
            pstmt.setString(2, descripcion);
            pstmt.setString(3, tipo);
            pstmt.setString(4, jefeEquipo.getApodo());
            pstmt.setString(5, creador.getApodo());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long grupoId = generatedKeys.getLong(1);
                    GrupoMision grupo = findGrupoById(grupoId);

                    // Agregar jefe como miembro automáticamente
                    if (grupo != null) {
                        agregarMiembro(grupoId, jefeEquipoId, creadoPorId);
                    }

                    return grupo;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear grupo: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public synchronized List<MiembroGrupo> findMiembrosByGrupo(Long grupoId) {
        String sql = "SELECT id, grupo_id, miembro_id, apodo, nombre_real, rol_en_grupo, puede_modificar_miembros, fecha_alta FROM miembros_grupo WHERE grupo_id = ? ORDER BY fecha_alta DESC";
        List<MiembroGrupo> miembros = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, grupoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MiembroGrupo m = new MiembroGrupo(
                            rs.getLong("id"),
                            rs.getLong("grupo_id"),
                            rs.getLong("miembro_id"),
                            rs.getString("apodo"),
                            rs.getString("nombre_real"),
                            rs.getString("rol_en_grupo"),
                            rs.getBoolean("puede_modificar_miembros"),
                            rs.getString("fecha_alta")
                    );
                    miembros.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener miembros del grupo: " + e.getMessage());
        }

        return miembros;
    }

    @Override
    public synchronized boolean agregarMiembro(Long grupoId, Long miembroId, Long solicitanteId) {
        if (!puedeModificarGrupo(grupoId, solicitanteId)) {
            return false;
        }

        // Verificar que no esté ya
        String sqlCheck = "SELECT COUNT(*) as count FROM miembros_grupo WHERE grupo_id = ? AND miembro_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlCheck)) {

            pstmt.setLong(1, grupoId);
            pstmt.setLong(2, miembroId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt("count") > 0) {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar membresía: " + e.getMessage());
            return false;
        }

        Guardia guardia = findGuardiaById(miembroId);
        if (guardia == null) {
            return false;
        }

        String sql = "INSERT INTO miembros_grupo (grupo_id, miembro_id, apodo, nombre_real, rol_en_grupo, puede_modificar_miembros, fecha_alta) VALUES (?, ?, ?, ?, 'Miembro', ?, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            boolean puedeModificar = esMaestre(solicitanteId) || esJefeEquipo(grupoId, solicitanteId);

            pstmt.setLong(1, grupoId);
            pstmt.setLong(2, miembroId);
            pstmt.setString(3, guardia.getApodo());
            pstmt.setString(4, guardia.getNombreReal());
            pstmt.setBoolean(5, puedeModificar);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar miembro al grupo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public synchronized boolean quitarMiembro(Long grupoId, Long miembroId, Long solicitanteId) {
        if (!puedeModificarGrupo(grupoId, solicitanteId)) {
            return false;
        }

        String sql = "DELETE FROM miembros_grupo WHERE grupo_id = ? AND miembro_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, grupoId);
            pstmt.setLong(2, miembroId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al quitar miembro del grupo: " + e.getMessage());
            return false;
        }
    }

    // ==================== MENSAJES ====================

    @Override
    public synchronized List<MensajeComunicacion> findAllMensajes() {
        String sql = "SELECT id, emisor_id, emisor_apodo, grupo_id, grupo_nombre, contenido, es_broadcast, visible, fecha_mensaje FROM mensajes_comunicacion ORDER BY fecha_mensaje DESC LIMIT 100";
        List<MensajeComunicacion> mensajes = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MensajeComunicacion m = new MensajeComunicacion(
                        rs.getLong("id"),
                        rs.getLong("emisor_id"),
                        rs.getString("emisor_apodo"),
                        rs.getObject("grupo_id") != null ? rs.getLong("grupo_id") : null,
                        rs.getString("grupo_nombre"),
                        rs.getString("contenido"),
                        rs.getBoolean("es_broadcast"),
                        rs.getBoolean("visible"),
                        rs.getString("fecha_mensaje")
                );
                mensajes.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener mensajes: " + e.getMessage());
        }

        return mensajes;
    }

    @Override
    public synchronized MensajeComunicacion enviarMensaje(Long emisorId, Long grupoId, String contenido, boolean broadcast) {
        Guardia emisor = findGuardiaById(emisorId);
        if (emisor == null) {
            throw new IllegalArgumentException("Emisor invalido");
        }

        if (broadcast && !esMaestre(emisorId)) {
            throw new IllegalArgumentException("Solo el Maestre puede enviar mensajes globales.");
        }

        String grupoNombre = null;
        if (grupoId != null) {
            GrupoMision grupo = findGrupoById(grupoId);
            if (grupo == null) {
                throw new IllegalArgumentException("Grupo no encontrado.");
            }
            grupoNombre = grupo.getNombreGrupo();
            if (!broadcast && !esMaestre(emisorId) && !perteneceAGrupo(grupoId, emisorId)) {
                throw new IllegalArgumentException("Solo miembros del grupo pueden publicar en esta mision.");
            }
        }

        String sql = "INSERT INTO mensajes_comunicacion (emisor_id, emisor_apodo, grupo_id, grupo_nombre, contenido, es_broadcast, visible, fecha_mensaje) VALUES (?, ?, ?, ?, ?, ?, TRUE, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, emisorId);
            pstmt.setString(2, emisor.getApodo());
            if (grupoId != null) {
                pstmt.setLong(3, grupoId);
            } else {
                pstmt.setNull(3, Types.BIGINT);
            }
            pstmt.setString(4, grupoNombre);
            pstmt.setString(5, contenido);
            pstmt.setBoolean(6, broadcast);

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    return new MensajeComunicacion(
                            id,
                            emisorId,
                            emisor.getApodo(),
                            grupoId,
                            grupoNombre,
                            contenido,
                            broadcast,
                            true,
                            timestampNow()
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al enviar mensaje: " + e.getMessage(), e);
        }

        return null;
    }

    // ==================== SECCIONES ====================

    @Override
    public synchronized List<SeccionMaestranza> findAllSecciones() {
        String sql = "SELECT id, nombre_seccion, responsable_id, responsable_apodo FROM secciones_maestranza ORDER BY nombre_seccion";
        List<SeccionMaestranza> secciones = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SeccionMaestranza s = new SeccionMaestranza(
                        rs.getLong("id"),
                        rs.getString("nombre_seccion"),
                        rs.getLong("responsable_id"),
                        rs.getString("responsable_apodo")
                );
                secciones.add(s);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener secciones: " + e.getMessage());
        }

        return secciones;
    }

    @Override
    public synchronized SeccionMaestranza crearSeccion(String nombreSeccion, Long responsableId, Long solicitanteId) {
        if (!puedeGestionarInventario(solicitanteId)) {
            throw new IllegalArgumentException("Solo Maestre o Sargento pueden crear secciones.");
        }

        if (nombreSeccion == null || nombreSeccion.isBlank()) {
            throw new IllegalArgumentException("El nombre de seccion es obligatorio.");
        }

        Guardia responsable = findGuardiaById(responsableId);
        if (responsable == null) {
            throw new IllegalArgumentException("Responsable no valido.");
        }

        String sql = "INSERT INTO secciones_maestranza (nombre_seccion, responsable_id, responsable_apodo) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, nombreSeccion.trim());
            pstmt.setLong(2, responsableId);
            pstmt.setString(3, responsable.getApodo());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    return new SeccionMaestranza(id, nombreSeccion.trim(), responsableId, responsable.getApodo());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear sección: " + e.getMessage(), e);
        }

        return null;
    }

    // ==================== PERTRECHOS ====================

    @Override
    public synchronized List<Pertrecho> findAllPertrechos() {
        String sql = "SELECT id, seccion_id, seccion_nombre, descripcion, integridad, estado_ia, token_qr, disponible FROM pertrechos ORDER BY id";
        List<Pertrecho> pertrechos = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Pertrecho p = new Pertrecho(
                        rs.getLong("id"),
                        rs.getLong("seccion_id"),
                        rs.getString("seccion_nombre"),
                        rs.getString("descripcion"),
                        rs.getInt("integridad"),
                        rs.getString("estado_ia"),
                        rs.getString("token_qr"),
                        rs.getBoolean("disponible")
                );
                pertrechos.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pertrechos: " + e.getMessage());
        }

        return pertrechos;
    }

    @Override
    public synchronized Pertrecho findPertrechoById(Long pertrechoId) {
        String sql = "SELECT id, seccion_id, seccion_nombre, descripcion, integridad, estado_ia, token_qr, disponible FROM pertrechos WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, pertrechoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Pertrecho(
                            rs.getLong("id"),
                            rs.getLong("seccion_id"),
                            rs.getString("seccion_nombre"),
                            rs.getString("descripcion"),
                            rs.getInt("integridad"),
                            rs.getString("estado_ia"),
                            rs.getString("token_qr"),
                            rs.getBoolean("disponible")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pertrecho: " + e.getMessage());
        }

        return null;
    }

    @Override
    public synchronized Pertrecho findPertrechoByTokenQr(String tokenQr) {
        if (tokenQr == null || tokenQr.isBlank()) {
            return null;
        }

        String sql = "SELECT id, seccion_id, seccion_nombre, descripcion, integridad, estado_ia, token_qr, disponible FROM pertrechos WHERE token_qr = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tokenQr);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Pertrecho(
                            rs.getLong("id"),
                            rs.getLong("seccion_id"),
                            rs.getString("seccion_nombre"),
                            rs.getString("descripcion"),
                            rs.getInt("integridad"),
                            rs.getString("estado_ia"),
                            rs.getString("token_qr"),
                            rs.getBoolean("disponible")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pertrecho por QR: " + e.getMessage());
        }

        return null;
    }

    @Override
    public synchronized Pertrecho crearPertrechoManual(Long seccionId, String descripcion, int integridad, String estadoIa, boolean disponible, Long solicitanteId) {
        if (!puedeGestionarInventario(solicitanteId)) {
            throw new IllegalArgumentException("Solo Maestre o Sargento pueden crear pertrechos.");
        }

        SeccionMaestranza seccion = findSeccionById(seccionId);
        if (seccion == null) {
            throw new IllegalArgumentException("Sección no encontrada.");
        }

        String sql = "INSERT INTO pertrechos (seccion_id, seccion_nombre, descripcion, integridad, estado_ia, token_qr, disponible) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String tokenQr = generarTokenQr();

            pstmt.setLong(1, seccionId);
            pstmt.setString(2, seccion.getNombreSeccion());
            pstmt.setString(3, descripcion == null ? "" : descripcion.trim());
            pstmt.setInt(4, acotar(integridad, 0, 100));
            pstmt.setString(5, estadoIa == null || estadoIa.isBlank() ? "Pendiente" : estadoIa);
            pstmt.setString(6, tokenQr);
            pstmt.setBoolean(7, disponible);

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    return findPertrechoById(id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear pertrecho: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public synchronized Pertrecho actualizarPertrecho(Long pertrechoId, Long seccionId, String descripcion, int integridad, String estadoIa, boolean disponible, Long solicitanteId) {
        if (!puedeGestionarInventario(solicitanteId)) {
            throw new IllegalArgumentException("Solo Maestre o Sargento pueden editar pertrechos.");
        }

        Pertrecho pertrecho = findPertrechoById(pertrechoId);
        if (pertrecho == null) {
            throw new IllegalArgumentException("Pertrecho no encontrado.");
        }

        SeccionMaestranza seccion = findSeccionById(seccionId);
        if (seccion == null) {
            throw new IllegalArgumentException("Seccion no encontrada.");
        }

        String sql = "UPDATE pertrechos SET seccion_id = ?, seccion_nombre = ?, descripcion = ?, integridad = ?, estado_ia = ?, disponible = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, seccionId);
            pstmt.setString(2, seccion.getNombreSeccion());
            pstmt.setString(3, descripcion == null ? pertrecho.getDescripcion() : descripcion.trim());
            pstmt.setInt(4, acotar(integridad, 0, 100));
            pstmt.setString(5, estadoIa == null || estadoIa.isBlank() ? pertrecho.getEstadoIa() : estadoIa);
            pstmt.setBoolean(6, disponible);
            pstmt.setLong(7, pertrechoId);

            pstmt.executeUpdate();
            return findPertrechoById(pertrechoId);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar pertrecho: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized boolean eliminarPertrecho(Long pertrechoId, Long solicitanteId) {
        if (!puedeGestionarInventario(solicitanteId)) {
            throw new IllegalArgumentException("Solo Maestre o Sargento pueden eliminar pertrechos.");
        }

        Pertrecho pertrecho = findPertrechoById(pertrechoId);
        if (pertrecho == null) {
            return false;
        }

        // Verificar que no este en use
        String sqlCheck = "SELECT COUNT(*) as count FROM historico_alardes WHERE pertrecho_id = ? AND fecha_entrada IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlCheck)) {

            pstmt.setLong(1, pertrechoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt("count") > 0) {
                    throw new IllegalArgumentException("No se puede eliminar un pertrecho con alarde abierto.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar alardes: " + e.getMessage(), e);
        }

        String sql = "DELETE FROM pertrechos WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, pertrechoId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar pertrecho: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized ResultadoClasificacionIa altaPertrechoConIa(String descripcion, Long solicitanteId) {
        if (!puedeGestionarInventario(solicitanteId)) {
            throw new IllegalArgumentException("Solo Maestre o Sargento pueden dar de alta pertrechos.");
        }

        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripcion del pertrecho es obligatoria.");
        }

        IaSugerencia sugerencia = sugerirSeccionPorTexto(descripcion);
        SeccionMaestranza seccion = findAllSecciones().stream()
                .filter(item -> item.getNombreSeccion().equalsIgnoreCase(sugerencia.getNombreSeccion()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe una seccion valida para la clasificacion IA."));

        boolean autoValidado = sugerencia.getConfianza() > 80;
        String estadoIa = autoValidado ? "Validado" : "Pendiente";
        Pertrecho pertrecho = crearPertrechoManual(seccion.getId(), descripcion.trim(), 100, estadoIa, true, solicitanteId);

        return new ResultadoClasificacionIa(pertrecho, sugerencia.getNombreSeccion(), sugerencia.getConfianza(), autoValidado);
    }

    @Override
    public synchronized boolean validarEstadoIaMasivo(List<Long> idsPertrechos, String estadoIa, Long revisorId) {
        if (!esMaestre(revisorId)) {
            throw new IllegalArgumentException("Solo el Maestre puede validar estados IA de forma masiva.");
        }

        if (idsPertrechos == null || idsPertrechos.isEmpty()) {
            return false;
        }

        String sql = "UPDATE pertrechos SET estado_ia = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int actualizados = 0;
            for (Long idPertrecho : idsPertrechos) {
                pstmt.setString(1, estadoIa);
                pstmt.setLong(2, idPertrecho);
                actualizados += pstmt.executeUpdate();
            }

            return actualizados > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al validar estados IA: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized List<Pertrecho> findPertrechosPublicos() {
        return findAllPertrechos();
    }

    // ==================== ALARDES ====================

    @Override
    public synchronized List<HistoricoAlarde> findAllAlardes() {
        String sql = "SELECT id, guardia_id, guardia_apodo, pertrecho_id, pertrecho_descripcion, autorizador_id, autorizador_apodo, fecha_salida, fecha_entrada, observaciones, ticket_maestranza, delta_gracia, integridad_salida, integridad_entrada FROM historico_alardes ORDER BY fecha_salida DESC";
        List<HistoricoAlarde> alardes = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                HistoricoAlarde a = new HistoricoAlarde(
                        rs.getLong("id"),
                        rs.getLong("guardia_id"),
                        rs.getString("guardia_apodo"),
                        rs.getLong("pertrecho_id"),
                        rs.getString("pertrecho_descripcion"),
                        rs.getLong("autorizador_id"),
                        rs.getString("autorizador_apodo"),
                        rs.getString("fecha_salida"),
                        rs.getString("fecha_entrada"),
                        rs.getString("observaciones"),
                        rs.getBoolean("ticket_maestranza"),
                        rs.getInt("delta_gracia"),
                        rs.getInt("integridad_salida"),
                        rs.getInt("integridad_entrada")
                );
                alardes.add(a);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener alardes: " + e.getMessage());
        }

        return alardes;
    }

    @Override
    public synchronized List<HistoricoAlarde> findAlardesByPertrecho(Long pertrechoId) {
        String sql = "SELECT id, guardia_id, guardia_apodo, pertrecho_id, pertrecho_descripcion, autorizador_id, autorizador_apodo, fecha_salida, fecha_entrada, observaciones, ticket_maestranza, delta_gracia, integridad_salida, integridad_entrada FROM historico_alardes WHERE pertrecho_id = ? ORDER BY fecha_salida DESC";
        List<HistoricoAlarde> alardes = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, pertrechoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HistoricoAlarde a = new HistoricoAlarde(
                            rs.getLong("id"),
                            rs.getLong("guardia_id"),
                            rs.getString("guardia_apodo"),
                            rs.getLong("pertrecho_id"),
                            rs.getString("pertrecho_descripcion"),
                            rs.getLong("autorizador_id"),
                            rs.getString("autorizador_apodo"),
                            rs.getString("fecha_salida"),
                            rs.getString("fecha_entrada"),
                            rs.getString("observaciones"),
                            rs.getBoolean("ticket_maestranza"),
                            rs.getInt("delta_gracia"),
                            rs.getInt("integridad_salida"),
                            rs.getInt("integridad_entrada")
                    );
                    alardes.add(a);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener alardes por pertrecho: " + e.getMessage());
        }

        return alardes;
    }

    @Override
    public synchronized HistoricoAlarde prestarPertrecho(Long guardiaId, Long pertrechoId, Long autorizadorId, String observaciones) {
        Guardia guardia = findGuardiaById(guardiaId);
        Guardia autorizador = findGuardiaById(autorizadorId);
        Pertrecho pertrecho = findPertrechoById(pertrechoId);

        if (guardia == null || autorizador == null || pertrecho == null) {
            throw new IllegalArgumentException("Datos de alarde no validos.");
        }

        if (!puedeAutorizarAlarde(autorizadorId)) {
            throw new IllegalArgumentException("Solo Maestre o Sargento pueden autorizar un alarde.");
        }

        if (!pertrecho.isDisponible()) {
            throw new IllegalArgumentException("El pertrecho ya esta prestado en otro alarde.");
        }

        if (guardiaBloqueadoParaCategoria(guardia, pertrecho)) {
            throw new IllegalArgumentException("Guardia bloqueado por infamia para pertrechos de Armeria.");
        }

        String sql = "INSERT INTO historico_alardes (guardia_id, guardia_apodo, pertrecho_id, pertrecho_descripcion, autorizador_id, autorizador_apodo, fecha_salida, observaciones, integridad_salida) VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, guardiaId);
            pstmt.setString(2, guardia.getApodo());
            pstmt.setLong(3, pertrechoId);
            pstmt.setString(4, pertrecho.getDescripcion());
            pstmt.setLong(5, autorizadorId);
            pstmt.setString(6, autorizador.getApodo());
            pstmt.setString(7, observaciones == null ? "" : observaciones.trim());
            pstmt.setInt(8, pertrecho.getIntegridad());

            pstmt.executeUpdate();

            // Marcar pertrecho como no disponible
            String sqlUpdate = "UPDATE pertrechos SET disponible = FALSE WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate)) {
                updateStmt.setLong(1, pertrechoId);
                updateStmt.executeUpdate();
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    return new HistoricoAlarde(id, guardiaId, guardia.getApodo(), pertrechoId, pertrecho.getDescripcion(), autorizadorId, autorizador.getApodo(), timestampNow(), null, observaciones, false, 0, pertrecho.getIntegridad(), -1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al prestar pertrecho: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public synchronized HistoricoAlarde registrarDevolucion(Long alardeId, int integridadDevuelta, String observaciones) {
        HistoricoAlarde alarde = findAllAlardes().stream()
                .filter(item -> alardeId.equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Alarde no encontrado."));

        if (alarde.getFechaEntrada() != null) {
            throw new IllegalArgumentException("El alarde ya fue cerrado.");
        }

        if (integridadDevuelta < 0 || integridadDevuelta > 100) {
            throw new IllegalArgumentException("La integridad de devolucion debe estar entre 0 y 100.");
        }

        Guardia guardia = findGuardiaById(alarde.getGuardiaId());
        Pertrecho pertrecho = findPertrechoById(alarde.getPertrechoId());

        if (guardia == null || pertrecho == null) {
            throw new IllegalArgumentException("No se han encontrado los datos del alarde para cerrar la devolucion.");
        }

        int dano = Math.max(0, alarde.getIntegridadSalida() - integridadDevuelta);
        boolean ticketMaestranza = dano > 20;
        int deltaGracia = 0;

        if (ticketMaestranza) {
            deltaGracia = -30;
        } else if (dano == 0) {
            deltaGracia = 5;
        }

        int puntosActualizados = acotar(guardia.getPuntosGracia() + deltaGracia, 0, 100);

        // Actualizar alarde
        String sqlUpdateAlarde = "UPDATE historico_alardes SET fecha_entrada = NOW(), integridad_entrada = ?, observaciones = ?, ticket_maestranza = ?, delta_gracia = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdateAlarde)) {

            pstmt.setInt(1, integridadDevuelta);
            pstmt.setString(2, observaciones == null ? alarde.getObservaciones() : observaciones.trim());
            pstmt.setBoolean(3, ticketMaestranza);
            pstmt.setInt(4, deltaGracia);
            pstmt.setLong(5, alardeId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar alarde: " + e.getMessage(), e);
        }

        // Actualizar guardia
        String sqlUpdateGuardia = "UPDATE guardias SET puntos_gracia = ?, estado_honor = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdateGuardia)) {

            pstmt.setInt(1, puntosActualizados);
            pstmt.setString(2, puntosActualizados < 20 ? "Infame" : "Activo");
            pstmt.setLong(3, alarde.getGuardiaId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar guardia: " + e.getMessage(), e);
        }

        // Actualizar pertrecho
        String sqlUpdatePertrecho = "UPDATE pertrechos SET integridad = ?, disponible = TRUE WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdatePertrecho)) {

            pstmt.setInt(1, integridadDevuelta);
            pstmt.setLong(2, alarde.getPertrechoId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar pertrecho: " + e.getMessage(), e);
        }

        return new HistoricoAlarde(alardeId, alarde.getGuardiaId(), alarde.getGuardiaApodo(), alarde.getPertrechoId(), alarde.getPertrechoDescripcion(), alarde.getAutorizadorId(), alarde.getAutorizadorApodo(), alarde.getFechaSalida(), timestampNow(), observaciones, ticketMaestranza, deltaGracia, alarde.getIntegridadSalida(), integridadDevuelta);
    }

    @Override
    public synchronized int getTotalTicketsMaestranza() {
        String sql = "SELECT COUNT(*) as count FROM historico_alardes WHERE ticket_maestranza = TRUE";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener total de tickets: " + e.getMessage());
        }

        return 0;
    }

    // ==================== GALERIA PUBLICA ====================

    @Override
    public synchronized List<FotoPublica> findGaleriaPublica() {
        String sql = "SELECT id, titulo, descripcion, lugar_evento, fecha_evento, url_imagen FROM fotos_publicas ORDER BY id";
        List<FotoPublica> fotos = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                FotoPublica foto = new FotoPublica(
                        rs.getLong("id"),
                        rs.getString("titulo"),
                        rs.getString("descripcion"),
                        rs.getString("lugar_evento"),
                        rs.getString("fecha_evento"),
                        rs.getString("url_imagen")
                );

                // Cargar etiquetas
                loadEtiquetas(foto.getId(), foto);

                // Cargar valoraciones
                loadValoraciones(foto.getId(), foto);

                fotos.add(foto);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener galería: " + e.getMessage());
        }

        return fotos;
    }

    @Override
    public synchronized boolean etiquetarPersonaEnFoto(Long fotoId, String nombrePersona, String etiquetadoPor, String usuarioRed) {
        FotoPublica foto = findFotoById(fotoId);
        if (foto == null || nombrePersona == null || nombrePersona.isBlank()) {
            return false;
        }

        String nombreNormalizado = nombrePersona.trim();

        // Verificar que no esté duplicada
        String sqlCheck = "SELECT COUNT(*) as count FROM etiquetas_fotos WHERE foto_id = ? AND nombre_persona = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlCheck)) {

            pstmt.setLong(1, fotoId);
            pstmt.setString(2, nombreNormalizado);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt("count") > 0) {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar etiqueta: " + e.getMessage());
            return false;
        }

        // Insertar
        String sql = "INSERT INTO etiquetas_fotos (foto_id, nombre_persona, etiquetado_por, usuario_red, fecha_etiqueta) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, fotoId);
            pstmt.setString(2, nombreNormalizado);
            pstmt.setString(3, etiquetadoPor == null || etiquetadoPor.isBlank() ? "Visitante" : etiquetadoPor.trim());
            pstmt.setString(4, usuarioRed == null ? "" : usuarioRed.trim());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear etiqueta: " + e.getMessage());
            return false;
        }
    }

    @Override
    public synchronized boolean valorarFotoPublica(Long fotoId, int puntuacion, String comentario, String visitante, String usuarioRed) {
        FotoPublica foto = findFotoById(fotoId);
        if (foto == null || puntuacion < 1 || puntuacion > 5) {
            return false;
        }

        String sql = "INSERT INTO valoraciones_fotos (foto_id, puntuacion, comentario, visitante, usuario_red, fecha_valoracion) VALUES (?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, fotoId);
            pstmt.setInt(2, puntuacion);
            pstmt.setString(3, comentario == null ? "" : comentario.trim());
            pstmt.setString(4, visitante == null || visitante.isBlank() ? "Visitante" : visitante.trim());
            pstmt.setString(5, usuarioRed == null ? "" : usuarioRed.trim());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear valoracion: " + e.getMessage());
            return false;
        }
    }

    // ==================== METODOS PRIVADOS ====================

    private FotoPublica findFotoById(Long fotoId) {
        String sql = "SELECT id, titulo, descripcion, lugar_evento, fecha_evento, url_imagen FROM fotos_publicas WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, fotoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    FotoPublica foto = new FotoPublica(
                            rs.getLong("id"),
                            rs.getString("titulo"),
                            rs.getString("descripcion"),
                            rs.getString("lugar_evento"),
                            rs.getString("fecha_evento"),
                            rs.getString("url_imagen")
                    );
                    loadEtiquetas(fotoId, foto);
                    loadValoraciones(fotoId, foto);
                    return foto;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener foto: " + e.getMessage());
        }

        return null;
    }

    private void loadEtiquetas(Long fotoId, FotoPublica foto) {
        String sql = "SELECT nombre_persona, etiquetado_por, usuario_red, fecha_etiqueta FROM etiquetas_fotos WHERE foto_id = ? ORDER BY fecha_etiqueta DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, fotoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    EtiquetaPersonaPublica etiqueta = new EtiquetaPersonaPublica(
                            rs.getString("nombre_persona"),
                            rs.getString("etiquetado_por"),
                            rs.getString("usuario_red"),
                            rs.getString("fecha_etiqueta")
                    );
                    foto.addEtiqueta(etiqueta);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar etiquetas: " + e.getMessage());
        }
    }

    private void loadValoraciones(Long fotoId, FotoPublica foto) {
        String sql = "SELECT id, puntuacion, comentario, visitante, usuario_red, fecha_valoracion FROM valoraciones_fotos WHERE foto_id = ? ORDER BY fecha_valoracion DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, fotoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ValoracionFotoPublica valoracion = new ValoracionFotoPublica(
                            rs.getLong("id"),
                            fotoId,
                            rs.getInt("puntuacion"),
                            rs.getString("comentario"),
                            rs.getString("visitante"),
                            rs.getString("usuario_red"),
                            rs.getString("fecha_valoracion")
                    );
                    foto.addValoracion(valoracion);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar valoraciones: " + e.getMessage());
        }
    }

    private SeccionMaestranza findSeccionById(Long seccionId) {
        String sql = "SELECT id, nombre_seccion, responsable_id, responsable_apodo FROM secciones_maestranza WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, seccionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new SeccionMaestranza(
                            rs.getLong("id"),
                            rs.getString("nombre_seccion"),
                            rs.getLong("responsable_id"),
                            rs.getString("responsable_apodo")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener seccion: " + e.getMessage());
        }

        return null;
    }

    private boolean puedeModificarGrupo(Long grupoId, Long solicitanteId) {
        return esMaestre(solicitanteId) || esJefeEquipo(grupoId, solicitanteId);
    }

    private boolean esMaestre(Long miembroId) {
        Guardia guardia = findGuardiaById(miembroId);
        return guardia != null && "Maestre".equalsIgnoreCase(guardia.getRango());
    }

    private boolean esSargento(Long miembroId) {
        Guardia guardia = findGuardiaById(miembroId);
        return guardia != null && "Sargento".equalsIgnoreCase(guardia.getRango());
    }

    private boolean puedeGestionarInventario(Long miembroId) {
        return esMaestre(miembroId) || esSargento(miembroId);
    }

    private boolean puedeAutorizarAlarde(Long miembroId) {
        return esMaestre(miembroId) || esSargento(miembroId);
    }

    private boolean guardiaBloqueadoParaCategoria(Guardia guardia, Pertrecho pertrecho) {
        boolean bloqueadoPorPuntos = guardia.getPuntosGracia() < 20 || "Infame".equalsIgnoreCase(guardia.getEstadoHonor());
        boolean categoriaRestringida = "Armeria".equalsIgnoreCase(pertrecho.getSeccionNombre())
                || contienePalabra(pertrecho.getDescripcion(), "arcabuz")
                || contienePalabra(pertrecho.getDescripcion(), "mosquete");
        return bloqueadoPorPuntos && categoriaRestringida;
    }

    private boolean esJefeEquipo(Long grupoId, Long miembroId) {
        String sql = "SELECT COUNT(*) as count FROM miembros_grupo WHERE grupo_id = ? AND miembro_id = ? AND rol_en_grupo = 'JefeEquipo'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, grupoId);
            pstmt.setLong(2, miembroId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar jefe equipo: " + e.getMessage());
        }

        return false;
    }

    private boolean perteneceAGrupo(Long grupoId, Long miembroId) {
        String sql = "SELECT COUNT(*) as count FROM miembros_grupo WHERE grupo_id = ? AND miembro_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, grupoId);
            pstmt.setLong(2, miembroId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar pertenencia: " + e.getMessage());
        }

        return false;
    }

    private IaSugerencia sugerirSeccionPorTexto(String descripcion) {
        String texto = descripcion == null ? "" : descripcion.toLowerCase();
        if (texto.contains("arcabuz") || texto.contains("morrion") || texto.contains("espada") || texto.contains("arma")) {
            return new IaSugerencia("Armeria", 92);
        }
        if (texto.contains("jubon") || texto.contains("capa") || texto.contains("bota") || texto.contains("terciopelo")) {
            return new IaSugerencia("Sastreria", 87);
        }
        if (texto.contains("estandarte") || texto.contains("escudo") || texto.contains("insignia")) {
            return new IaSugerencia("Ornamentos", 84);
        }
        if (texto.contains("cuero") || texto.contains("correa") || texto.contains("tahali")) {
            return new IaSugerencia("Teneria", 83);
        }
        return new IaSugerencia("Aposentos", 64);
    }

    private int acotar(int valor, int minimo, int maximo) {
        return Math.max(minimo, Math.min(valor, maximo));
    }

    private boolean contienePalabra(String texto, String patron) {
        return texto != null && texto.toLowerCase().contains(patron.toLowerCase());
    }

    private String generarTokenQr() {
        return "RG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String timestampNow() {
        return LocalDateTime.now().format(FORMATTER);
    }

    private static final class IaSugerencia {
        private final String nombreSeccion;
        private final int confianza;

        private IaSugerencia(String nombreSeccion, int confianza) {
            this.nombreSeccion = nombreSeccion;
            this.confianza = confianza;
        }

        private String getNombreSeccion() {
            return nombreSeccion;
        }

        private int getConfianza() {
            return confianza;
        }
    }
}
