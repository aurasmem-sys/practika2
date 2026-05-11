package com.mysuperproject.atelier.view;

import com.mysuperproject.atelier.entity.Client;
import com.mysuperproject.atelier.infrastructure.mail.SmtpEmailSender;
import com.mysuperproject.atelier.infrastructure.security.SHA256PasswordHasher;
import com.mysuperproject.atelier.repository.ClientRepository;
import com.mysuperproject.atelier.service.AuthService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class RegisterController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    
    @FXML private VBox registerFormBox;
    @FXML private VBox verificationBox;
    @FXML private TextField codeField;
    
    @FXML private Label errorLabel;
    @FXML private Button registerButton;

    private AuthService authService;
    private String actualCode;
    private Client pendingClient;

    @FXML
    public void initialize() {
        ClientRepository repo = new ClientRepository();
        authService = new AuthService(repo, new SHA256PasswordHasher(), new SmtpEmailSender());
    }

    @FXML
    public void onRegister() {
        errorLabel.setText("");
        String fname = firstNameField.getText();
        String lname = lastNameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String pwd = passwordField.getText();

        if (fname == null || fname.isEmpty() || lname == null || lname.isEmpty() || phone == null || phone.isEmpty() || email == null || email.isEmpty() || pwd == null || pwd.isEmpty()) {
            errorLabel.setText("Усі поля є обов'язковими!");
            return;
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errorLabel.setText("Невірний формат email.");
            return;
        }

        pendingClient = Client.builder()
                .firstName(fname)
                .lastName(lname)
                .phoneNumber(phone)
                .email(email)
                .build();

        registerButton.setDisable(true);
        registerButton.setText("Відправка листа...");

        Task<String> registerTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return authService.register(pendingClient, pwd);
            }
        };

        registerTask.setOnSucceeded(e -> {
            actualCode = registerTask.getValue();
            registerFormBox.setVisible(false);
            registerFormBox.setManaged(false);
            registerButton.setVisible(false);
            registerButton.setManaged(false);
            verificationBox.setVisible(true);
            verificationBox.setManaged(true);
        });

        registerTask.setOnFailed(e -> {
            errorLabel.setText(registerTask.getException().getMessage());
            registerButton.setDisable(false);
            registerButton.setText("Зареєструватися");
        });

        new Thread(registerTask).start();
    }

    @FXML
    public void onVerify() {
        String inputCode = codeField.getText();
        if (authService.verifyEmail(pendingClient.getEmail(), inputCode, actualCode)) {
            SceneManager.switchScene("/com/mysuperproject/atelier/view/login_view.fxml", "Вхід в систему", 400, 600);
        } else {
            errorLabel.setText("Невірний код!");
        }
    }

    @FXML
    public void onBack() {
        SceneManager.switchScene("/com/mysuperproject/atelier/view/login_view.fxml", "Вхід в систему", 400, 600);
    }
    
    @FXML
    public void onToggleTheme() {
        SceneManager.toggleTheme();
    }
}
