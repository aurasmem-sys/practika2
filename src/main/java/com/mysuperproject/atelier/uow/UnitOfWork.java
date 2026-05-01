package com.mysuperproject.atelier.uow;

import com.mysuperproject.atelier.pool.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnitOfWork implements AutoCloseable {
    private final Connection connection;
    private final IdentityMap identityMap;
    
    // In a full implementation, we'd have lists of new, dirty, and deleted entities
    // and commit them all at once. For now, we will provide a transaction wrapper 
    // and an IdentityMap context.
    
    public UnitOfWork() {
        this.connection = ConnectionPool.getInstance().getConnection();
        this.identityMap = new IdentityMap();
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to begin transaction", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public IdentityMap getIdentityMap() {
        return identityMap;
    }

    public void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to commit transaction", e);
        }
    }

    public void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to rollback transaction", e);
        }
    }

    @Override
    public void close() {
        try {
            connection.setAutoCommit(true);
            connection.close(); // Return to pool
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close UnitOfWork", e);
        }
    }
}
