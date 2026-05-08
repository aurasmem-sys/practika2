package com.mysuperproject.atelier.service;

import com.mysuperproject.atelier.entity.OrderMaterial;
import com.mysuperproject.atelier.repository.OrderMaterialRepository;

import java.util.List;
import java.util.Optional;

public class OrderMaterialService {
    
    private final OrderMaterialRepository orderMaterialRepository;

    public OrderMaterialService(OrderMaterialRepository orderMaterialRepository) {
        this.orderMaterialRepository = orderMaterialRepository;
    }

    public OrderMaterial create(OrderMaterial orderMaterial) {
        return orderMaterialRepository.save(orderMaterial);
    }

    public void update(OrderMaterial orderMaterial) {
        orderMaterialRepository.update(orderMaterial);
    }

    public boolean delete(String compositeId) {
        return orderMaterialRepository.delete(compositeId);
    }

    public Optional<OrderMaterial> getById(String compositeId) {
        return orderMaterialRepository.findById(compositeId);
    }

    public List<OrderMaterial> getAll() {
        return orderMaterialRepository.findAll();
    }
}
