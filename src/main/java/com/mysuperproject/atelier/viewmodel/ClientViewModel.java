package com.mysuperproject.atelier.viewmodel;

import com.mysuperproject.atelier.entity.Client;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ClientViewModel {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty phoneNumber = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();

    public ClientViewModel(Client client) {
        if (client.getId() != null) {
            this.id.set(client.getId());
        }
        this.firstName.set(client.getFirstName());
        this.lastName.set(client.getLastName());
        this.phoneNumber.set(client.getPhoneNumber());
        this.email.set(client.getEmail());
    }

    public ClientViewModel() {
    }

    public Client toEntity() {
        return Client.builder()
                .id(id.get() == 0 ? null : id.get())
                .firstName(firstName.get())
                .lastName(lastName.get())
                .phoneNumber(phoneNumber.get())
                .email(email.get())
                .build();
    }

    public IntegerProperty idProperty() { return id; }
    public StringProperty firstNameProperty() { return firstName; }
    public StringProperty lastNameProperty() { return lastName; }
    public StringProperty phoneNumberProperty() { return phoneNumber; }
    public StringProperty emailProperty() { return email; }

    public int getId() { return id.get(); }
    public String getFirstName() { return firstName.get(); }
    public String getLastName() { return lastName.get(); }
    public String getPhoneNumber() { return phoneNumber.get(); }
    public String getEmail() { return email.get(); }

    public void setId(int id) { this.id.set(id); }
    public void setFirstName(String firstName) { this.firstName.set(firstName); }
    public void setLastName(String lastName) { this.lastName.set(lastName); }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber.set(phoneNumber); }
    public void setEmail(String email) { this.email.set(email); }
}
