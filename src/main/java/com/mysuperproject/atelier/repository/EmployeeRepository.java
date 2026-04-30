package com.mysuperproject.atelier.repository;

import com.mysuperproject.atelier.entity.Employee;
import com.mysuperproject.atelier.pool.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeRepository implements Dao<Integer, Employee> {

    private static final String DELETE_SQL = "DELETE FROM employees WHERE id = ?";
    private static final String SAVE_SQL =
            "INSERT INTO employees (first_name, last_name, position, phone_number) VALUES (?, ?, ?,"
                    + " ?)";
    private static final String UPDATE_SQL =
            "UPDATE employees SET first_name = ?, last_name = ?, position = ?, phone_number = ?"
                    + " WHERE id = ?";
    private static final String FIND_BY_ID_SQL =
            "SELECT id, first_name, last_name, position, phone_number FROM employees WHERE id = ?";
    private static final String FIND_ALL_SQL =
            "SELECT id, first_name, last_name, position, phone_number FROM employees";

    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public boolean delete(Integer id) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting employee", e);
        }
    }

    @Override
    public Employee save(Employee entity) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getPosition());
            preparedStatement.setString(4, entity.getPhoneNumber());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getInt(1));
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving employee", e);
        }
    }

    @Override
    public void update(Employee entity) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getPosition());
            preparedStatement.setString(4, entity.getPhoneNumber());
            preparedStatement.setInt(5, entity.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating employee", e);
        }
    }

    @Override
    public Optional<Employee> findById(Integer id) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildEmployee(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding employee by id", e);
        }
    }

    @Override
    public List<Employee> findAll() {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Employee> employees = new ArrayList<>();
            while (resultSet.next()) {
                employees.add(buildEmployee(resultSet));
            }
            return employees;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all employees", e);
        }
    }

    private Employee buildEmployee(ResultSet resultSet) throws SQLException {
        return Employee.builder()
                .id(resultSet.getInt("id"))
                .firstName(resultSet.getString("first_name"))
                .lastName(resultSet.getString("last_name"))
                .position(resultSet.getString("position"))
                .phoneNumber(resultSet.getString("phone_number"))
                .build();
    }
}
