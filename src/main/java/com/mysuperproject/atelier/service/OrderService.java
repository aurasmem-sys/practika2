package com.mysuperproject.atelier.service;

import com.mysuperproject.atelier.entity.Order;
import com.mysuperproject.atelier.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

public class OrderService {
    
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order create(Order order) {
        return orderRepository.save(order);
    }

    public void update(Order order) {
        orderRepository.update(order);
    }

    public boolean delete(Integer id) {
        return orderRepository.delete(id);
    }

    public Optional<Order> getById(Integer id) {
        return orderRepository.findById(id);
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }
}
