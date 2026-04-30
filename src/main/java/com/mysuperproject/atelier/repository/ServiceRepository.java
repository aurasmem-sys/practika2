package com.mysuperproject.atelier.repository;

import com.mysuperproject.atelier.entity.Service;
import com.mysuperproject.atelier.pool.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceRepository implements Dao<Integer, Service> {

    private static final String DELETE_SQL = "DELETE FROM services WHERE id = ?";
    private static final String SAVE_SQL =
            "INSERT INTO services (service_name, description, base_price) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE services SET service_name = ?, description = ?, base_price = ? WHERE id = ?";
    private static final String FIND_BY_ID_SQL =
            "SELECT id, service_name, description, base_price FROM services WHERE id = ?";
    private static final String FIND_ALL_SQL =
            "SELECT id, service_name, description, base_price FROM services";

    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public boolean delete(Integer id) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting service", e);
        }
    }

    @Override
    public Service save(Service entity) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, entity.getServiceName());
            preparedStatement.setString(2, entity.getDescription());
            preparedStatement.setBigDecimal(3, entity.getBasePrice());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getInt(1));
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving service", e);
        }
    }

    @Override
    public void update(Service entity) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, entity.getServiceName());
            preparedStatement.setString(2, entity.getDescription());
            preparedStatement.setBigDecimal(3, entity.getBasePrice());
            preparedStatement.setInt(4, entity.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating service", e);
        }
    }

    @Override
    public Optional<Service> findById(Integer id) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildService(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding service by id", e);
        }
    }

    @Override
    public List<Service> findAll() {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Service> services = new ArrayList<>();
            while (resultSet.next()) {
                services.add(buildService(resultSet));
            }
            return services;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all services", e);
        }
    }

    private Service buildService(ResultSet resultSet) throws SQLException {
        return Service.builder()
                .id(resultSet.getInt("id"))
                .serviceName(resultSet.getString("service_name"))
                .description(resultSet.getString("description"))
                .basePrice(resultSet.getBigDecimal("base_price"))
                .build();
    }
}
