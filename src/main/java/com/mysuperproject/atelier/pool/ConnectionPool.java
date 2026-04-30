package com.mysuperproject.atelier.pool;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPool {
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final String POOL_SIZE_KEY = "db.pool.size";

    private static ConnectionPool instance;
    private BlockingQueue<Connection> pool;
    private List<Connection> sourceConnections;

    private ConnectionPool() {
        initConnectionPool();
    }

    public static synchronized ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    private void initConnectionPool() {
        String poolSize = DatabaseProperties.get(POOL_SIZE_KEY);
        int size = poolSize == null ? 10 : Integer.parseInt(poolSize);
        pool = new ArrayBlockingQueue<>(size);
        sourceConnections = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Connection connection = open();
            Connection proxyConnection =
                    (Connection)
                            Proxy.newProxyInstance(
                                    ConnectionPool.class.getClassLoader(),
                                    new Class[] {Connection.class},
                                    (proxy, method, args) -> {
                                        if (method.getName().equals("close")) {
                                            pool.add((Connection) proxy);
                                            return null;
                                        }
                                        return method.invoke(connection, args);
                                    });
            pool.add(proxyConnection);
            sourceConnections.add(connection);
        }
    }

    private Connection open() {
        try {
            return DriverManager.getConnection(
                    DatabaseProperties.get(URL_KEY),
                    DatabaseProperties.get(USERNAME_KEY),
                    DatabaseProperties.get(PASSWORD_KEY));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to open connection", e);
        }
    }

    public Connection getConnection() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to take connection from the pool", e);
        }
    }

    public void closePool() {
        try {
            for (Connection sourceConnection : sourceConnections) {
                sourceConnection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while closing pool", e);
        }
    }
}
