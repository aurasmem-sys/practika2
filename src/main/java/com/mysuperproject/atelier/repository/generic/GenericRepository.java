package com.mysuperproject.atelier.repository.generic;

import com.mysuperproject.atelier.annotation.Column;
import com.mysuperproject.atelier.annotation.Id;
import com.mysuperproject.atelier.annotation.Table;
import com.mysuperproject.atelier.uow.UnitOfWork;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenericRepository<K, E> {
    private final Class<E> entityClass;
    private final UnitOfWork uow;

    public GenericRepository(Class<E> entityClass, UnitOfWork uow) {
        this.entityClass = entityClass;
        this.uow = uow;
    }

    private String getTableName() {
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null) {
            return table.name();
        }
        return entityClass.getSimpleName().toLowerCase() + "s";
    }

    private Field getIdField() {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }
        throw new RuntimeException("No @Id field found in " + entityClass.getName());
    }

    private String getColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !column.name().isEmpty()) {
            return column.name();
        }
        return field.getName();
    }

    public Optional<E> findById(K id) {
        E cached = uow.getIdentityMap().get(entityClass, id);
        if (cached != null) {
            return Optional.of(cached);
        }

        String tableName = getTableName();
        Field idField = getIdField();
        String idCol = getColumnName(idField);

        String sql = "SELECT * FROM " + tableName + " WHERE " + idCol + " = ?";

        try (PreparedStatement stmt = uow.getConnection().prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                E entity = mapResultSetToEntity(rs);
                uow.getIdentityMap().put(entityClass, id, entity);
                return Optional.of(entity);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to find entity", e);
        }
        return Optional.empty();
    }

    public E save(E entity) {
        String tableName = getTableName();
        Field idField = getIdField();
        idField.setAccessible(true);
        
        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();

        try {
            for (Field field : entityClass.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    columns.add(getColumnName(field));
                    values.add(field.get(entity));
                    placeholders.add("?");
                }
            }

            String sql = "INSERT INTO " + tableName + " (" + String.join(", ", columns) + ") VALUES (" + String.join(", ", placeholders) + ")";

            try (PreparedStatement stmt = uow.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < values.size(); i++) {
                    stmt.setObject(i + 1, values.get(i));
                }
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    Object generatedId = rs.getObject(1);
                    // Conversion logic if needed, simplify for now assuming Integer
                    idField.set(entity, ((Number)generatedId).intValue());
                }
            }

            // Cache it
            uow.getIdentityMap().put(entityClass, idField.get(entity), entity);
            return entity;

        } catch (Exception e) {
            throw new RuntimeException("Failed to save entity", e);
        }
    }

    private E mapResultSetToEntity(ResultSet rs) throws Exception {
        E entity = entityClass.getDeclaredConstructor().newInstance();
        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);
            String colName = getColumnName(field);
            Object value = rs.getObject(colName);
            field.set(entity, value);
        }
        return entity;
    }
}
