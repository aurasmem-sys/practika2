package com.mysuperproject.atelier.uow;

import java.util.HashMap;
import java.util.Map;

public class IdentityMap {
    private final Map<Class<?>, Map<Object, Object>> map = new HashMap<>();

    public <T> void put(Class<T> clazz, Object id, T entity) {
        map.computeIfAbsent(clazz, k -> new HashMap<>()).put(id, entity);
    }

    public <T> T get(Class<T> clazz, Object id) {
        Map<Object, Object> classMap = map.get(clazz);
        if (classMap != null) {
            return clazz.cast(classMap.get(id));
        }
        return null;
    }

    public <T> void remove(Class<T> clazz, Object id) {
        Map<Object, Object> classMap = map.get(clazz);
        if (classMap != null) {
            classMap.remove(id);
        }
    }

    public void clear() {
        map.clear();
    }
}
