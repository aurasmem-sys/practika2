package com.mysuperproject.atelier.pool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class DatabaseProperties {
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private DatabaseProperties() {}

    private static void loadProperties() {
        try (InputStream inputStream =
                DatabaseProperties.class
                        .getClassLoader()
                        .getResourceAsStream("application.properties")) {
            if (inputStream != null) {
                PROPERTIES.load(inputStream);
            } else {
                // Default fallback properties for standalone testing
                PROPERTIES.setProperty("db.url", "jdbc:sqlite:atelier.db");
                PROPERTIES.setProperty("db.username", "");
                PROPERTIES.setProperty("db.password", "");
                PROPERTIES.setProperty("db.pool.size", "1");
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot load properties file", e);
        }
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }
}
