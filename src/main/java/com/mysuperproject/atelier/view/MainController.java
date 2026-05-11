package com.mysuperproject.atelier.view;

import com.mysuperproject.atelier.entity.*;
import com.mysuperproject.atelier.repository.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MainController {

    // Клієнти
    @FXML private TableView<Client> clientsTable;
    @FXML private TableColumn<Client, Integer> clientIdCol;
    @FXML private TableColumn<Client, String> clientFirstNameCol;
    @FXML private TableColumn<Client, String> clientLastNameCol;
    @FXML private TableColumn<Client, String> clientPhoneCol;
    @FXML private TableColumn<Client, String> clientEmailCol;
    @FXML private TextField clientFirstNameField, clientLastNameField, clientPhoneField, clientEmailField;
    @FXML private TextField clientSearchField;

    // Працівники
    @FXML private TableView<Employee> employeesTable;
    @FXML private TableColumn<Employee, Integer> empIdCol;
    @FXML private TableColumn<Employee, String> empFirstNameCol, empLastNameCol, empPositionCol, empPhoneCol;
    @FXML private TextField empFirstNameField, empLastNameField, empPositionField, empPhoneField;

    // Матеріали
    @FXML private TableView<Material> materialsTable;
    @FXML private TableColumn<Material, Integer> matIdCol;
    @FXML private TableColumn<Material, String> matNameCol, matUnitCol;
    @FXML private TableColumn<Material, BigDecimal> matPriceCol;
    @FXML private TextField matNameField, matUnitField, matPriceField;

    // Послуги
    @FXML private TableView<Service> servicesTable;
    @FXML private TableColumn<Service, Integer> srvIdCol;
    @FXML private TableColumn<Service, String> srvNameCol, srvDescCol;
    @FXML private TableColumn<Service, BigDecimal> srvPriceCol;
    @FXML private TextField srvNameField, srvDescField, srvPriceField;

    // Замовлення
    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> ordIdCol, ordClientCol, ordEmpCol;
    @FXML private TableColumn<Order, LocalDate> ordDateCol;
    @FXML private TableColumn<Order, String> ordStatusCol;
    @FXML private TableColumn<Order, BigDecimal> ordTotalCol;
    @FXML private TextField ordClientField, ordEmpField, ordStatusField, ordTotalField;

    private ClientRepository clientRepo = new ClientRepository();
    private EmployeeRepository empRepo = new EmployeeRepository();
    private MaterialRepository matRepo = new MaterialRepository();
    private ServiceRepository srvRepo = new ServiceRepository();
    private OrderRepository ordRepo = new OrderRepository();

    private ObservableList<Client> clientList = FXCollections.observableArrayList();
    private ObservableList<Employee> empList = FXCollections.observableArrayList();
    private ObservableList<Material> matList = FXCollections.observableArrayList();
    private ObservableList<Service> srvList = FXCollections.observableArrayList();
    private ObservableList<Order> ordList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initClients();
        initEmployees();
        initMaterials();
        initServices();
        initOrders();
    }

    private void initClients() {
        clientIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientFirstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        clientLastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        clientPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        clientEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        refreshClients();

        clientsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                clientFirstNameField.setText(newSel.getFirstName());
                clientLastNameField.setText(newSel.getLastName());
                clientPhoneField.setText(newSel.getPhoneNumber());
                clientEmailField.setText(newSel.getEmail());
            }
        });
        
        // Додаємо лісенер для пошуку
        if (clientSearchField != null) {
            clientSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filterClients(newVal);
            });
        }
    }

    private void refreshClients() {
        // Витягуємо всіх клієнтів з БД, але фільтруємо адміна, щоб він не світився в таблиці
        List<Client> onlyClients = clientRepo.findAll().stream()
                .filter(c -> !"ADMIN".equals(c.getRole()))
                .toList();
        clientList.setAll(onlyClients);
        clientsTable.setItems(clientList);
    }

    private void filterClients(String query) {
        // Якщо поле пошуку пусте - показуємо всіх
        if (query == null || query.trim().isEmpty()) {
            refreshClients();
            return;
        }
        String lowerCaseQuery = query.toLowerCase();
        
        // Шукаємо по імені або прізвищу (без урахування регістру)
        List<Client> filtered = clientRepo.findAll().stream()
                .filter(c -> !"ADMIN".equals(c.getRole()))
                .filter(c -> c.getFirstName().toLowerCase().contains(lowerCaseQuery) || 
                             c.getLastName().toLowerCase().contains(lowerCaseQuery))
                .toList();
        clientList.setAll(filtered);
    }

    @FXML public void onClientAdd() {
        Client c = Client.builder().firstName(clientFirstNameField.getText()).lastName(clientLastNameField.getText())
                .phoneNumber(clientPhoneField.getText()).email(clientEmailField.getText()).build();
        clientRepo.save(c); refreshClients(); clearClientFields();
    }

    @FXML public void onClientUpdate() {
        Client c = clientsTable.getSelectionModel().getSelectedItem();
        if (c != null) {
            c.setFirstName(clientFirstNameField.getText());
            c.setLastName(clientLastNameField.getText());
            c.setPhoneNumber(clientPhoneField.getText());
            c.setEmail(clientEmailField.getText());
            clientRepo.update(c); refreshClients();
        }
    }

    @FXML public void onClientDelete() {
        Client c = clientsTable.getSelectionModel().getSelectedItem();
        if (c != null) { clientRepo.delete(c.getId()); refreshClients(); clearClientFields(); }
    }
    private void clearClientFields() { clientFirstNameField.clear(); clientLastNameField.clear(); clientPhoneField.clear(); clientEmailField.clear(); }


    private void initEmployees() {
        empIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        empFirstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        empLastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        empPositionCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        empPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        refreshEmployees();
        employeesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                empFirstNameField.setText(newSel.getFirstName()); empLastNameField.setText(newSel.getLastName());
                empPositionField.setText(newSel.getPosition()); empPhoneField.setText(newSel.getPhoneNumber());
            }
        });
    }

    private void refreshEmployees() { empList.setAll(empRepo.findAll()); employeesTable.setItems(empList); }

    @FXML public void onEmpAdd() {
        Employee e = Employee.builder().firstName(empFirstNameField.getText()).lastName(empLastNameField.getText())
                .position(empPositionField.getText()).phoneNumber(empPhoneField.getText()).build();
        empRepo.save(e); refreshEmployees(); clearEmpFields();
    }
    @FXML public void onEmpUpdate() {
        Employee e = employeesTable.getSelectionModel().getSelectedItem();
        if (e != null) {
            e.setFirstName(empFirstNameField.getText()); e.setLastName(empLastNameField.getText());
            e.setPosition(empPositionField.getText()); e.setPhoneNumber(empPhoneField.getText());
            empRepo.update(e); refreshEmployees();
        }
    }
    @FXML public void onEmpDelete() {
        Employee e = employeesTable.getSelectionModel().getSelectedItem();
        if (e != null) { empRepo.delete(e.getId()); refreshEmployees(); clearEmpFields(); }
    }
    private void clearEmpFields() { empFirstNameField.clear(); empLastNameField.clear(); empPositionField.clear(); empPhoneField.clear(); }


    private void initMaterials() {
        matIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        matNameCol.setCellValueFactory(new PropertyValueFactory<>("materialName"));
        matUnitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
        matPriceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        refreshMaterials();
        materialsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, n) -> {
            if (n != null) {
                matNameField.setText(n.getMaterialName()); matUnitField.setText(n.getUnit()); matPriceField.setText(n.getPricePerUnit().toString());
            }
        });
    }
    private void refreshMaterials() { matList.setAll(matRepo.findAll()); materialsTable.setItems(matList); }

    @FXML public void onMatAdd() {
        Material m = Material.builder().materialName(matNameField.getText()).unit(matUnitField.getText())
                .pricePerUnit(new BigDecimal(matPriceField.getText())).build();
        matRepo.save(m); refreshMaterials(); clearMatFields();
    }
    @FXML public void onMatUpdate() {
        Material m = materialsTable.getSelectionModel().getSelectedItem();
        if (m != null) {
            m.setMaterialName(matNameField.getText()); m.setUnit(matUnitField.getText());
            m.setPricePerUnit(new BigDecimal(matPriceField.getText()));
            matRepo.update(m); refreshMaterials();
        }
    }
    @FXML public void onMatDelete() {
        Material m = materialsTable.getSelectionModel().getSelectedItem();
        if (m != null) { matRepo.delete(m.getId()); refreshMaterials(); clearMatFields(); }
    }
    private void clearMatFields() { matNameField.clear(); matUnitField.clear(); matPriceField.clear(); }

    private void initServices() {
        srvIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        srvNameCol.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        srvDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        srvPriceCol.setCellValueFactory(new PropertyValueFactory<>("basePrice"));
        refreshServices();
        servicesTable.getSelectionModel().selectedItemProperty().addListener((obs, old, n) -> {
            if (n != null) {
                srvNameField.setText(n.getServiceName()); srvDescField.setText(n.getDescription()); srvPriceField.setText(n.getBasePrice().toString());
            }
        });
    }
    private void refreshServices() { srvList.setAll(srvRepo.findAll()); servicesTable.setItems(srvList); }

    @FXML public void onSrvAdd() {
        Service s = Service.builder().serviceName(srvNameField.getText()).description(srvDescField.getText())
                .basePrice(new BigDecimal(srvPriceField.getText())).build();
        srvRepo.save(s); refreshServices(); clearSrvFields();
    }
    @FXML public void onSrvUpdate() {
        Service s = servicesTable.getSelectionModel().getSelectedItem();
        if (s != null) {
            s.setServiceName(srvNameField.getText()); s.setDescription(srvDescField.getText());
            s.setBasePrice(new BigDecimal(srvPriceField.getText()));
            srvRepo.update(s); refreshServices();
        }
    }
    @FXML public void onSrvDelete() {
        Service s = servicesTable.getSelectionModel().getSelectedItem();
        if (s != null) { srvRepo.delete(s.getId()); refreshServices(); clearSrvFields(); }
    }
    private void clearSrvFields() { srvNameField.clear(); srvDescField.clear(); srvPriceField.clear(); }

    private void initOrders() {
        ordIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        ordClientCol.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        ordEmpCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        ordDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        ordStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        ordTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        refreshOrders();
        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, old, n) -> {
            if (n != null) {
                ordClientField.setText(String.valueOf(n.getClientId()));
                ordEmpField.setText(String.valueOf(n.getEmployeeId()));
                ordStatusField.setText(n.getStatus());
                ordTotalField.setText(n.getTotalPrice() != null ? n.getTotalPrice().toString() : "0");
            }
        });
    }
    private void refreshOrders() { ordList.setAll(ordRepo.findAll()); ordersTable.setItems(ordList); }

    @FXML public void onOrdAdd() {
        Order o = Order.builder().clientId(Integer.parseInt(ordClientField.getText()))
                .employeeId(Integer.parseInt(ordEmpField.getText())).orderDate(LocalDate.now())
                .status(ordStatusField.getText()).totalPrice(new BigDecimal(ordTotalField.getText())).build();
        ordRepo.save(o); refreshOrders(); clearOrdFields();
    }
    @FXML public void onOrdUpdate() {
        Order o = ordersTable.getSelectionModel().getSelectedItem();
        if (o != null) {
            o.setClientId(Integer.parseInt(ordClientField.getText()));
            o.setEmployeeId(Integer.parseInt(ordEmpField.getText()));
            o.setStatus(ordStatusField.getText());
            o.setTotalPrice(new BigDecimal(ordTotalField.getText()));
            ordRepo.update(o); refreshOrders();
        }
    }
    @FXML public void onOrdDelete() {
        Order o = ordersTable.getSelectionModel().getSelectedItem();
        if (o != null) { ordRepo.delete(o.getId()); refreshOrders(); clearOrdFields(); }
    }
    private void clearOrdFields() { ordClientField.clear(); ordEmpField.clear(); ordStatusField.clear(); ordTotalField.clear(); }

    @FXML
    public void onExportData() {
        try {
            java.io.File file = new java.io.File(System.getProperty("user.home") + "/Desktop/atelier_clients_export.csv");
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file, java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write('\ufeff'); // BOM for Excel
                writer.println("ID;Ім'я;Прізвище;Телефон;Email");
                for (com.mysuperproject.atelier.entity.Client c : clientList) {
                    writer.printf("%d;%s;%s;%s;%s%n", 
                        c.getId(), c.getFirstName(), c.getLastName(), c.getPhoneNumber(), c.getEmail());
                }
            }
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Експорт успішний");
            alert.setHeaderText(null);
            alert.setContentText("Дані успішно експортовано на Робочий стіл у файл atelier_clients_export.csv (відкривається в Excel).");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onLogout() {
        SceneManager.switchScene("/com/mysuperproject/atelier/view/login_view.fxml", "Вхід в систему", 400, 600);
    }
    
    @FXML
    public void onReset2FA() {
        java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(LoginController.class);
        prefs.putBoolean("admin_2fa_setup_done", false);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("2FA Скинуто");
        alert.setHeaderText(null);
        alert.setContentText("Налаштування 2FA для адміністратора скинуто.\nВас буде виведено з системи. При наступному вході вам буде показано секретний ключ для підключення нового пристрою (телефону).");
        alert.showAndWait();
        
        onLogout();
    }
    
    @FXML
    public void onToggleTheme() {
        SceneManager.toggleTheme();
    }
}
