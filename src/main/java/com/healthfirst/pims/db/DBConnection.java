package com.healthfirst.pims.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Ugeshan Naicker Student No.402313594
 * Centralized JDBC connection factory for the PIMS application.
 * Reads connection details from db.properties on the classpath so the
 * database host/name/credentials can be changed without recompiling.
 */
public final class DBConnection {

    private static final String PROPS_FILE = "/db.properties";
    private static Properties props;

    private DBConnection() {}

    private static Properties loadProps() {
        if (props != null) return props;
        props = new Properties();
        try (InputStream in = DBConnection.class.getResourceAsStream(PROPS_FILE)) {
            if (in == null) {
                throw new RuntimeException("db.properties not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties: " + e.getMessage(), e);
        }
        return props;
    }

    public static Connection getConnection() throws SQLException {
        Properties p = loadProps();
        String host = p.getProperty("db.host", "localhost");
        String port = p.getProperty("db.port", "3306");
        String name = p.getProperty("db.name", "pims_db");
        String user = p.getProperty("db.user", "root");
        String pass = p.getProperty("db.password", "");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + name
                + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found on classpath", e);
        }

        return DriverManager.getConnection(url, user, pass);
    }
}
