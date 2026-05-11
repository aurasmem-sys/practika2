package com.mysuperproject.atelier.view;

import com.mysuperproject.atelier.entity.Client;
import com.mysuperproject.atelier.infrastructure.mail.SmtpEmailSender;
import com.mysuperproject.atelier.infrastructure.security.SHA256PasswordHasher;
import com.mysuperproject.atelier.repository.ClientRepository;
import com.mysuperproject.atelier.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.prefs.Preferences;
import javafx.scene.layout.VBox;
import com.warrenstrange.googleauth.GoogleAuthenticator;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    
    @FXML private VBox mfaBox;
    @FXML private TextField mfaCodeField;

    private AuthService authService;
    private GoogleAuthenticator gAuth = new GoogleAuthenticator();
    private final String ADMIN_2FA_SECRET = "ABCDEFGHIJKLMNOP"; // 16 symbol Base32 for Demo

    @FXML
    public void initialize() {
        ClientRepository repo = new ClientRepository();
        authService = new AuthService(repo, new SHA256PasswordHasher(), new SmtpEmailSender());
    }

    @FXML
    public void onLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setStyle("-fx-text-fill: -color-danger-fg;");
            errorLabel.setText("Будь ласка, заповніть всі поля.");
            return;
        }

        try {
            Client client = authService.login(email, password);
            com.mysuperproject.atelier.util.UserSession.setCurrentClient(client);
            System.out.println("Login successful for: " + client.getEmail() + " | Role: " + client.getRole());
            
            if ("ADMIN".equals(client.getRole())) {
                SceneManager.switchScene("/com/mysuperproject/atelier/view/main_view.fxml", "Atelier Management System (ADMIN)", 900, 700);
            } else {
                SceneManager.switchScene("/com/mysuperproject/atelier/view/client_portal.fxml", "Клієнтський портал", 800, 600);
            }
        } catch (IllegalArgumentException e) {
            errorLabel.setStyle("-fx-text-fill: -color-danger-fg;");
            errorLabel.setText(e.getMessage());
        } catch (Exception e) {
            errorLabel.setStyle("-fx-text-fill: -color-danger-fg;");
            errorLabel.setText("Помилка підключення до БД.");
        }
    }

    @FXML
    public void onAdminLoginRequest() {
        mfaBox.setVisible(true);
        mfaBox.setManaged(true);
        
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        boolean isSetupDone = prefs.getBoolean("admin_2fa_setup_done", false);
        
        if (!isSetupDone) {
            errorLabel.setStyle("-fx-text-fill: -color-warning-fg;");
            errorLabel.setText("ЗБЕРЕЖІТЬ КЛЮЧ: " + ADMIN_2FA_SECRET + " (більше не покажеться)");
        } else {
            errorLabel.setStyle("-fx-text-fill: -color-fg-default;");
            errorLabel.setText("Введіть 6-значний код з додатку");
        }
    }

    @FXML
    public void onVerifyAdminCode() {
        String codeText = mfaCodeField.getText();
        if (codeText == null || codeText.isEmpty()) {
            errorLabel.setStyle("-fx-text-fill: -color-danger-fg;");
            errorLabel.setText("Введіть код!");
            return;
        }
        
        try {
            int code = Integer.parseInt(codeText);
            boolean isCodeValid = gAuth.authorize(ADMIN_2FA_SECRET, code);
            
            if (isCodeValid) {
                // Зберігаємо інфу, що адмін вже налаштував 2FA
                Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
                prefs.putBoolean("admin_2fa_setup_done", true);
                
                System.out.println("Google Auth successful!");
                SceneManager.switchScene("/com/mysuperproject/atelier/view/main_view.fxml", "Atelier Management System (ADMIN)", 900, 700);
            } else {
                errorLabel.setStyle("-fx-text-fill: -color-danger-fg;");
                errorLabel.setText("Невірний код! Спробуйте ще раз.");
            }
        } catch (NumberFormatException e) {
            errorLabel.setStyle("-fx-text-fill: -color-danger-fg;");
            errorLabel.setText("Код має містити лише цифри!");
        }
    }

    @FXML
    public void onGoToRegister() {
        SceneManager.switchScene("/com/mysuperproject/atelier/view/register_view.fxml", "Реєстрація", 400, 550);
    }
    
    @FXML
    public void onToggleTheme() {
        SceneManager.toggleTheme();
    }
}
