package com.mysuperproject.atelier.service;

import com.mysuperproject.atelier.entity.Client;
import com.mysuperproject.atelier.repository.ClientRepository;

import java.util.List;
import java.util.Optional;

public class ClientService {
    
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client create(Client client) {
        return clientRepository.save(client);
    }

    public void update(Client client) {
        clientRepository.update(client);
    }

    public boolean delete(Integer id) {
        return clientRepository.delete(id);
    }

    public Optional<Client> getById(Integer id) {
        return clientRepository.findById(id);
    }

    public List<Client> getAll() {
        return clientRepository.findAll();
    }
}
