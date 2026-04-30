package com.mysuperproject.atelier.repository;

import com.mysuperproject.atelier.entity.OrderService;
import com.mysuperproject.atelier.pool.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderServiceRepository implements Dao<String, OrderService> {

    private static final String DELETE_SQL =
            "DELETE FROM order_services WHERE order_id = ? AND service_id = ?";
    private static final String SAVE_SQL =
            "INSERT INTO order_services (order_id, service_id, quantity, actual_price) VALUES (?,"
                    + " ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE order_services SET quantity = ?, actual_price = ? WHERE order_id = ? AND"
                    + " service_id = ?";
    private static final String FIND_BY_ID_SQL =
            "SELECT order_id, service_id, quantity, actual_price FROM order_services WHERE order_id"
                    + " = ? AND service_id = ?";
    private static final String FIND_ALL_SQL =
            "SELECT order_id, service_id, quantity, actual_price FROM order_services";

    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public boolean delete(String compositeId) {
        String[] parts = compositeId.split("_");
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, Integer.parseInt(parts[0]));
            preparedStatement.setInt(2, Integer.parseInt(parts[1]));
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting OrderService", e);
        }
    }

    @Override
    public OrderService save(OrderService entity) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL)) {
            preparedStatement.setInt(1, entity.getOrderId());
            preparedStatement.setInt(2, entity.getServiceId());
            preparedStatement.setInt(3, entity.getQuantity());
            preparedStatement.setBigDecimal(4, entity.getActualPrice());
            preparedStatement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving OrderService", e);
        }
    }

    @Override
    public void update(OrderService entity) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setInt(1, entity.getQuantity());
            preparedStatement.setBigDecimal(2, entity.getActualPrice());
            preparedStatement.setInt(3, entity.getOrderId());
            preparedStatement.setInt(4, entity.getServiceId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating OrderService", e);
        }
    }

    @Override
    public Optional<OrderService> findById(String compositeId) {
        String[] parts = compositeId.split("_");
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, Integer.parseInt(parts[0]));
            preparedStatement.setInt(2, Integer.parseInt(parts[1]));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildOrderService(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding OrderService by id", e);
        }
    }

    @Override
    public List<OrderService> findAll() {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<OrderService> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(buildOrderService(resultSet));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all OrderServices", e);
        }
    }

    private OrderService buildOrderService(ResultSet resultSet) throws SQLException {
        return OrderService.builder()
                .orderId(resultSet.getInt("order_id"))
                .serviceId(resultSet.getInt("service_id"))
                .quantity(resultSet.getInt("quantity"))
                .actualPrice(resultSet.getBigDecimal("actual_price"))
                .build();
    }
}
