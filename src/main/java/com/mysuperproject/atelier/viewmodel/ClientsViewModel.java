package com.mysuperproject.atelier.viewmodel;

import com.mysuperproject.atelier.entity.Client;
import com.mysuperproject.atelier.service.ClientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.List;
import java.util.stream.Collectors;

public class ClientsViewModel {

    private final ClientService clientService;
    private final ObservableList<ClientViewModel> clientsList;
    private final FilteredList<ClientViewModel> filteredClients;

    public ClientsViewModel(ClientService clientService) {
        this.clientService = clientService;
        this.clientsList = FXCollections.observableArrayList();
        this.filteredClients = new FilteredList<>(this.clientsList, p -> true);
        loadClients();
    }

    public void loadClients() {
        List<ClientViewModel> loaded = clientService.getAll().stream()
                .map(ClientViewModel::new)
                .collect(Collectors.toList());
        clientsList.setAll(loaded);
    }

    public SortedList<ClientViewModel> getSortedAndFilteredClients() {
        return new SortedList<>(filteredClients);
    }

    public void filterClients(String query) {
        if (query == null || query.isEmpty()) {
            filteredClients.setPredicate(client -> true);
        } else {
            String lowerCaseFilter = query.toLowerCase();
            filteredClients.setPredicate(client -> {
                if (client.getFirstName() != null && client.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (client.getLastName() != null && client.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (client.getPhoneNumber() != null && client.getPhoneNumber().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (client.getEmail() != null && client.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        }
    }

    public void addClient(ClientViewModel clientVM) {
        Client entity = clientVM.toEntity();
        Client saved = clientService.create(entity);
        clientsList.add(new ClientViewModel(saved));
    }

    public void updateClient(ClientViewModel clientVM) {
        Client entity = clientVM.toEntity();
        clientService.update(entity);
        loadClients(); // reload to refresh
    }

    public void deleteClient(ClientViewModel clientVM) {
        if (clientService.delete(clientVM.getId())) {
            clientsList.remove(clientVM);
        }
    }
}
