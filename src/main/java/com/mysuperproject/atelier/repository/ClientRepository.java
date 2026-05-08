package com.mysuperproject.atelier.repository;

import com.mysuperproject.atelier.entity.Client;
import com.mysuperproject.atelier.pool.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientRepository implements Dao<Integer, Client> {

    private static final String DELETE_SQL = "DELETE FROM clients WHERE id = ?";
    private static final String SAVE_SQL =
            "INSERT INTO clients (first_name, last_name, phone_number, email, password) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE clients SET first_name = ?, last_name = ?, phone_number = ?, email = ?, password = ? WHERE id"
                    + " = ?";
    private static final String FIND_BY_ID_SQL =
            "SELECT id, first_name, last_name, phone_number, email, password FROM clients WHERE id = ?";
    private static final String FIND_ALL_SQL =
            "SELECT id, first_name, last_name, phone_number, email, password FROM clients";
    private static final String FIND_BY_EMAIL_SQL =
            "SELECT id, first_name, last_name, phone_number, email, password FROM clients WHERE email = ?";

    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public boolean delete(Integer id) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting client", e);
        }
    }

    @Override
    public Client save(Client entity) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getPhoneNumber());
            preparedStatement.setString(4, entity.getEmail());
            preparedStatement.setString(5, entity.getPassword());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getInt(1));
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving client", e);
        }
    }

    @Override
    public void update(Client entity) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getPhoneNumber());
            preparedStatement.setString(4, entity.getEmail());
            preparedStatement.setString(5, entity.getPassword());
            preparedStatement.setInt(6, entity.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating client", e);
        }
    }

    @Override
    public Optional<Client> findById(Integer id) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildClient(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding client by id", e);
        }
    }

    @Override
    public List<Client> findAll() {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Client> clients = new ArrayList<>();
            while (resultSet.next()) {
                clients.add(buildClient(resultSet));
            }
            return clients;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all clients", e);
        }
    }

    public Optional<Client> findByEmail(String email) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_EMAIL_SQL)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildClient(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding client by email", e);
        }
    }

    private Client buildClient(ResultSet resultSet) throws SQLException {
        return Client.builder()
                .id(resultSet.getInt("id"))
                .firstName(resultSet.getString("first_name"))
                .lastName(resultSet.getString("last_name"))
                .phoneNumber(resultSet.getString("phone_number"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .build();
    }
}
