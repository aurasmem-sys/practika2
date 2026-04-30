package com.mysuperproject.atelier.repository;

import com.mysuperproject.atelier.entity.OrderMaterial;
import com.mysuperproject.atelier.pool.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderMaterialRepository implements Dao<String, OrderMaterial> {

    private static final String DELETE_SQL =
            "DELETE FROM order_materials WHERE order_id = ? AND material_id = ?";
    private static final String SAVE_SQL =
            "INSERT INTO order_materials (order_id, material_id, quantity) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE order_materials SET quantity = ? WHERE order_id = ? AND material_id = ?";
    private static final String FIND_BY_ID_SQL =
            "SELECT order_id, material_id, quantity FROM order_materials WHERE order_id = ? AND"
                    + " material_id = ?";
    private static final String FIND_ALL_SQL =
            "SELECT order_id, material_id, quantity FROM order_materials";

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
            throw new RuntimeException("Error deleting OrderMaterial", e);
        }
    }

    @Override
    public OrderMaterial save(OrderMaterial entity) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL)) {
            preparedStatement.setInt(1, entity.getOrderId());
            preparedStatement.setInt(2, entity.getMaterialId());
            preparedStatement.setBigDecimal(3, entity.getQuantity());
            preparedStatement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving OrderMaterial", e);
        }
    }

    @Override
    public void update(OrderMaterial entity) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setBigDecimal(1, entity.getQuantity());
            preparedStatement.setInt(2, entity.getOrderId());
            preparedStatement.setInt(3, entity.getMaterialId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating OrderMaterial", e);
        }
    }

    @Override
    public Optional<OrderMaterial> findById(String compositeId) {
        String[] parts = compositeId.split("_");
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, Integer.parseInt(parts[0]));
            preparedStatement.setInt(2, Integer.parseInt(parts[1]));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildOrderMaterial(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding OrderMaterial by id", e);
        }
    }

    @Override
    public List<OrderMaterial> findAll() {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<OrderMaterial> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(buildOrderMaterial(resultSet));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all OrderMaterials", e);
        }
    }

    private OrderMaterial buildOrderMaterial(ResultSet resultSet) throws SQLException {
        return OrderMaterial.builder()
                .orderId(resultSet.getInt("order_id"))
                .materialId(resultSet.getInt("material_id"))
                .quantity(resultSet.getBigDecimal("quantity"))
                .build();
    }
}
