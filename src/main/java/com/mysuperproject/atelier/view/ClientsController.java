package com.mysuperproject.atelier.view;

import com.mysuperproject.atelier.pool.ConnectionPool;
import com.mysuperproject.atelier.repository.ClientRepository;
import com.mysuperproject.atelier.service.ClientService;
import com.mysuperproject.atelier.viewmodel.ClientViewModel;
import com.mysuperproject.atelier.viewmodel.ClientsViewModel;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClientsController {

    @FXML private TableView<ClientViewModel> clientsTable;
    @FXML private TableColumn<ClientViewModel, Integer> idCol;
    @FXML private TableColumn<ClientViewModel, String> firstNameCol;
    @FXML private TableColumn<ClientViewModel, String> lastNameCol;
    @FXML private TableColumn<ClientViewModel, String> phoneCol;
    @FXML private TableColumn<ClientViewModel, String> emailCol;

    @FXML private TextField searchField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private ClientsViewModel viewModel;

    @FXML
    public void initialize() {
        // Initialize backend (In real app, this should be injected e.g. via Guice/Spring)
        ClientRepository repo = new ClientRepository();
        ClientService service = new ClientService(repo);
        viewModel = new ClientsViewModel(service);

        // Bind columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Setup table data
        SortedList<ClientViewModel> sortedData = viewModel.getSortedAndFilteredClients();
        sortedData.comparatorProperty().bind(clientsTable.comparatorProperty());
        clientsTable.setItems(sortedData);

        // Search listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.filterClients(newValue);
        });

        // Table selection listener
        clientsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                firstNameField.setText(newSelection.getFirstName());
                lastNameField.setText(newSelection.getLastName());
                phoneField.setText(newSelection.getPhoneNumber());
                emailField.setText(newSelection.getEmail());
            }
        });
    }

    @FXML
    public void onAdd() {
        ClientViewModel newClient = new ClientViewModel();
        newClient.setFirstName(firstNameField.getText());
        newClient.setLastName(lastNameField.getText());
        newClient.setPhoneNumber(phoneField.getText());
        newClient.setEmail(emailField.getText());
        
        viewModel.addClient(newClient);
        clearFields();
    }

    @FXML
    public void onUpdate() {
        ClientViewModel selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setFirstName(firstNameField.getText());
            selected.setLastName(lastNameField.getText());
            selected.setPhoneNumber(phoneField.getText());
            selected.setEmail(emailField.getText());
            
            viewModel.updateClient(selected);
            clientsTable.refresh();
        }
    }

    @FXML
    public void onDelete() {
        ClientViewModel selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            viewModel.deleteClient(selected);
            clearFields();
        }
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
        emailField.clear();
        clientsTable.getSelectionModel().clearSelection();
    }
}
