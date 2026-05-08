package com.mysuperproject.atelier.service;

import com.mysuperproject.atelier.entity.OrderService;
import com.mysuperproject.atelier.repository.OrderServiceRepository;

import java.util.List;
import java.util.Optional;

public class OrderServiceItemService {
    
    private final OrderServiceRepository orderServiceRepository;

    public OrderServiceItemService(OrderServiceRepository orderServiceRepository) {
        this.orderServiceRepository = orderServiceRepository;
    }

    public OrderService create(OrderService orderService) {
        return orderServiceRepository.save(orderService);
    }

    public void update(OrderService orderService) {
        orderServiceRepository.update(orderService);
    }

    public boolean delete(String compositeId) {
        return orderServiceRepository.delete(compositeId);
    }

    public Optional<OrderService> getById(String compositeId) {
        return orderServiceRepository.findById(compositeId);
    }

    public List<OrderService> getAll() {
        return orderServiceRepository.findAll();
    }
}
