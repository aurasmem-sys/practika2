package com.mysuperproject.atelier.service;

import com.mysuperproject.atelier.entity.Service;
import com.mysuperproject.atelier.repository.ServiceRepository;

import java.util.List;
import java.util.Optional;

public class ServiceCatalogService {
    
    private final ServiceRepository serviceRepository;

    public ServiceCatalogService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public Service create(Service service) {
        return serviceRepository.save(service);
    }

    public void update(Service service) {
        serviceRepository.update(service);
    }

    public boolean delete(Integer id) {
        return serviceRepository.delete(id);
    }

    public Optional<Service> getById(Integer id) {
        return serviceRepository.findById(id);
    }

    public List<Service> getAll() {
        return serviceRepository.findAll();
    }
}
