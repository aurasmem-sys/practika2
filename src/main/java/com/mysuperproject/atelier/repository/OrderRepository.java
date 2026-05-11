package com.mysuperproject.atelier.repository;

import com.mysuperproject.atelier.entity.Order;
import com.mysuperproject.atelier.pool.ConnectionPool;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepository implements Dao<Integer, Order> {

    private static final String DELETE_SQL = "DELETE FROM orders WHERE id = ?";
    private static final String SAVE_SQL =
            "INSERT INTO orders (client_id, employee_id, order_date, status, total_price) VALUES"
                    + " (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE orders SET client_id = ?, employee_id = ?, order_date = ?, status = ?,"
                    + " total_price = ? WHERE id = ?";
    private static final String FIND_BY_ID_SQL =
            "SELECT id, client_id, employee_id, order_date, status, total_price FROM orders WHERE"
                    + " id = ?";
    private static final String FIND_ALL_SQL =
            "SELECT id, client_id, employee_id, order_date, status, total_price FROM orders";

    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public boolean delete(Integer id) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting order", e);
        }
    }

    @Override
    public Order save(Order entity) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, entity.getClientId());
            preparedStatement.setInt(2, entity.getEmployeeId());
            preparedStatement.setDate(
                    3, entity.getOrderDate() != null ? Date.valueOf(entity.getOrderDate()) : null);
            preparedStatement.setString(4, entity.getStatus());
            preparedStatement.setBigDecimal(5, entity.getTotalPrice());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getInt(1));
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving order", e);
        }
    }

    @Override
    public void update(Order entity) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setInt(1, entity.getClientId());
            preparedStatement.setInt(2, entity.getEmployeeId());
            preparedStatement.setDate(
                    3, entity.getOrderDate() != null ? Date.valueOf(entity.getOrderDate()) : null);
            preparedStatement.setString(4, entity.getStatus());
            preparedStatement.setBigDecimal(5, entity.getTotalPrice());
            preparedStatement.setInt(6, entity.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating order", e);
        }
    }

    @Override
    public Optional<Order> findById(Integer id) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildOrder(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding order by id", e);
        }
    }

    @Override
    public List<Order> findAll() {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Order> orders = new ArrayList<>();
            while (resultSet.next()) {
                orders.add(buildOrder(resultSet));
            }
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all orders", e);
        }
    }

    private Order buildOrder(ResultSet resultSet) throws SQLException {
        String dateStr = resultSet.getString("order_date");
        java.time.LocalDate orderDate = null;
        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                if (dateStr.contains(" ")) {
                    dateStr = dateStr.substring(0, dateStr.indexOf(" "));
                }
                orderDate = java.time.LocalDate.parse(dateStr);
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }
        
        return Order.builder()
                .id(resultSet.getInt("id"))
                .clientId(resultSet.getInt("client_id"))
                .employeeId(resultSet.getInt("employee_id"))
                .orderDate(orderDate)
                .status(resultSet.getString("status"))
                .totalPrice(resultSet.getBigDecimal("total_price"))
                .build();
    }
}
