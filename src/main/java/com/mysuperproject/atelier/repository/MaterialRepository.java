package com.mysuperproject.atelier.repository;

import com.mysuperproject.atelier.entity.Material;
import com.mysuperproject.atelier.pool.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaterialRepository implements Dao<Integer, Material> {

    private static final String DELETE_SQL = "DELETE FROM materials WHERE id = ?";
    private static final String SAVE_SQL =
            "INSERT INTO materials (material_name, unit, price_per_unit) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE materials SET material_name = ?, unit = ?, price_per_unit = ? WHERE id = ?";
    private static final String FIND_BY_ID_SQL =
            "SELECT id, material_name, unit, price_per_unit FROM materials WHERE id = ?";
    private static final String FIND_ALL_SQL =
            "SELECT id, material_name, unit, price_per_unit FROM materials";

    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public boolean delete(Integer id) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting material", e);
        }
    }

    @Override
    public Material save(Material entity) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, entity.getMaterialName());
            preparedStatement.setString(2, entity.getUnit());
            preparedStatement.setBigDecimal(3, entity.getPricePerUnit());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getInt(1));
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving material", e);
        }
    }

    @Override
    public void update(Material entity) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, entity.getMaterialName());
            preparedStatement.setString(2, entity.getUnit());
            preparedStatement.setBigDecimal(3, entity.getPricePerUnit());
            preparedStatement.setInt(4, entity.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating material", e);
        }
    }

    @Override
    public Optional<Material> findById(Integer id) {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildMaterial(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding material by id", e);
        }
    }

    @Override
    public List<Material> findAll() {
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Material> materials = new ArrayList<>();
            while (resultSet.next()) {
                materials.add(buildMaterial(resultSet));
            }
            return materials;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all materials", e);
        }
    }

    private Material buildMaterial(ResultSet resultSet) throws SQLException {
        return Material.builder()
                .id(resultSet.getInt("id"))
                .materialName(resultSet.getString("material_name"))
                .unit(resultSet.getString("unit"))
                .pricePerUnit(resultSet.getBigDecimal("price_per_unit"))
                .build();
    }
}
