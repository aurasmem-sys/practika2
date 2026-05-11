package com.mysuperproject.atelier.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import java.util.List;
import com.mysuperproject.atelier.entity.Client;
import com.mysuperproject.atelier.entity.Order;
import com.mysuperproject.atelier.repository.OrderRepository;
import com.mysuperproject.atelier.util.UserSession;

public class ClientPortalController {

    @FXML private Label welcomeLabel;
    @FXML private VBox ordersContainer;

    @FXML
    public void initialize() {
        Client currentClient = UserSession.getCurrentClient();
        if (currentClient != null) {
            welcomeLabel.setText("Вітаємо, " + currentClient.getFirstName() + "!");
            loadClientOrders(currentClient.getId());
        }
    }
    
    private void loadClientOrders(int clientId) {
        ordersContainer.getChildren().clear();
        OrderRepository orderRepo = new OrderRepository();
        List<Order> clientOrders = orderRepo.findAll().stream()
                .filter(o -> o.getClientId() == clientId)
                .toList();
                
        if (clientOrders.isEmpty()) {
            Label noOrdersLabel = new Label("У вас поки немає активних замовлень.");
            noOrdersLabel.setStyle("-fx-text-fill: -color-fg-muted;");
            ordersContainer.getChildren().add(noOrdersLabel);
            return;
        }
        
        for (Order order : clientOrders) {
            VBox orderBox = new VBox(10);
            orderBox.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 8; -fx-padding: 15;");
            
            HBox headerBox = new HBox(10);
            headerBox.setAlignment(Pos.CENTER_LEFT);
            
            Label titleLabel = new Label("Замовлення №" + order.getId());
            titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-fg-default;");
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            
            Label statusLabel = new Label(order.getStatus());
            // Колір статусу залежить від тексту
            if (order.getStatus().equalsIgnoreCase("Готово")) {
                statusLabel.setStyle("-fx-background-color: -color-success-emphasis; -fx-text-fill: white; -fx-padding: 3 8 3 8; -fx-background-radius: 10;");
            } else if (order.getStatus().contains("Очікує")) {
                statusLabel.setStyle("-fx-background-color: -color-warning-emphasis; -fx-text-fill: white; -fx-padding: 3 8 3 8; -fx-background-radius: 10;");
            } else {
                statusLabel.setStyle("-fx-background-color: -color-accent-emphasis; -fx-text-fill: white; -fx-padding: 3 8 3 8; -fx-background-radius: 10;");
            }
            
            headerBox.getChildren().addAll(titleLabel, spacer, statusLabel);
            
            Label dateLabel = new Label("Дата оформлення: " + (order.getOrderDate() != null ? order.getOrderDate().toString() : "Невідомо"));
            dateLabel.setStyle("-fx-text-fill: -color-fg-muted;");
            
            Label priceLabel = new Label("Сума: " + order.getTotalPrice() + " грн");
            priceLabel.setStyle("-fx-text-fill: -color-fg-default; -fx-font-weight: bold;");
            
            orderBox.getChildren().addAll(headerBox, dateLabel, priceLabel);
            ordersContainer.getChildren().add(orderBox);
        }
    }

    @FXML
    public void onLogout() {
        UserSession.setCurrentClient(null);
        SceneManager.switchScene("/com/mysuperproject/atelier/view/login_view.fxml", "Вхід в систему", 400, 600);
    }
    
    @FXML
    public void onToggleTheme() {
        SceneManager.toggleTheme();
    }
}
