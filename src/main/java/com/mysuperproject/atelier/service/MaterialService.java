package com.mysuperproject.atelier.service;

import com.mysuperproject.atelier.entity.Material;
import com.mysuperproject.atelier.repository.MaterialRepository;

import java.util.List;
import java.util.Optional;

public class MaterialService {
    
    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public Material create(Material material) {
        return materialRepository.save(material);
    }

    public void update(Material material) {
        materialRepository.update(material);
    }

    public boolean delete(Integer id) {
        return materialRepository.delete(id);
    }

    public Optional<Material> getById(Integer id) {
        return materialRepository.findById(id);
    }

    public List<Material> getAll() {
        return materialRepository.findAll();
    }
}
