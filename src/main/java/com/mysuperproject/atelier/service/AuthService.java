package com.mysuperproject.atelier.service;

import com.mysuperproject.atelier.entity.Client;
import com.mysuperproject.atelier.infrastructure.mail.EmailSender;
import com.mysuperproject.atelier.infrastructure.security.PasswordHasher;
import com.mysuperproject.atelier.repository.ClientRepository;

import java.util.Optional;

public class AuthService {

    private final ClientRepository clientRepository;
    private final PasswordHasher passwordHasher;
    private final EmailSender emailSender;

    public AuthService(ClientRepository clientRepository, PasswordHasher passwordHasher, EmailSender emailSender) {
        this.clientRepository = clientRepository;
        this.passwordHasher = passwordHasher;
        this.emailSender = emailSender;
    }

    public String register(Client client, String rawPassword) {
        if (client.getEmail() == null || client.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required for registration");
        }
        if (!client.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        Optional<Client> existingClient = clientRepository.findByEmail(client.getEmail());
        if (existingClient.isPresent()) {
            throw new IllegalArgumentException("Client with this email already exists");
        }

        String hashedPassword = passwordHasher.hash(rawPassword);
        client.setPassword(hashedPassword);
        if (client.getRole() == null) {
            client.setRole("CLIENT");
        }

        Client savedClient = clientRepository.save(client);

        // Генеруємо випадкове замовлення для демонстрації!
        try {
            com.mysuperproject.atelier.repository.OrderRepository orderRepo = new com.mysuperproject.atelier.repository.OrderRepository();
            String[] randomServices = {"Ремонт штанів (заміна блискавки)", "Пошиття піджака на замовлення", "Вкорочення сукні", "Заміна підкладки в пальто"};
            String randomService = randomServices[new java.util.Random().nextInt(randomServices.length)];
            
            com.mysuperproject.atelier.entity.Order initialOrder = com.mysuperproject.atelier.entity.Order.builder()
                .clientId(savedClient.getId())
                .employeeId(1) // Дефолтний майстер
                .orderDate(java.time.LocalDate.now())
                .status("Очікує примірки")
                .totalPrice(new java.math.BigDecimal(new java.util.Random().nextInt(2000) + 500))
                .build();
            orderRepo.save(initialOrder);
        } catch (Exception ex) {
            System.err.println("Failed to create random order: " + ex.getMessage());
        }

        // Генеруємо випадковий код підтвердження (для симуляції)
        String verificationCode = String.format("%06d", new java.util.Random().nextInt(999999));

        String htmlBody = "<div style='font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f9; color: #333;'>"
                + "<div style='max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0px 4px 10px rgba(0,0,0,0.1);'>"
                + "<h2 style='color: #2c3e50; text-align: center;'>Вітаємо в Ательє!</h2>"
                + "<p style='font-size: 16px;'>Шановний(а) <b>" + savedClient.getFirstName() + "</b>,</p>"
                + "<p style='font-size: 16px;'>Дякуємо за реєстрацію у нашій системі управління. Ми раді вітати вас у нашому особистому кабінеті!</p>"
                + "<p style='font-size: 16px;'>Для завершення реєстрації, будь ласка, скористайтеся цим кодом підтвердження:</p>"
                + "<div style='text-align: center; margin: 30px 0;'>"
                + "<span style='font-size: 28px; font-weight: bold; background-color: #3498db; color: white; padding: 15px 25px; border-radius: 8px; letter-spacing: 5px;'>" + verificationCode + "</span>"
                + "</div>"
                + "<p style='font-size: 14px; color: #7f8c8d; text-align: center;'>Якщо ви не реєструвалися в нашій системі, просто проігноруйте цей лист.</p>"
                + "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'/>"
                + "<p style='font-size: 12px; color: #95a5a6; text-align: center;'>З повагою,<br/><b style='color: #2c3e50;'>Команда Atelier Management System</b><br/>Obchodná 42, 811 06 Bratislava, Словаччина<br/>📞 +421 950 635 747 | ✉️ slavaborsak67@gmail.com</p>"
                + "</div></div>";

        emailSender.sendEmail(
                savedClient.getEmail(),
                "Підтвердження реєстрації - Atelier Management System",
                htmlBody
        );

        return verificationCode;
    }

    public boolean verifyEmail(String email, String inputCode, String actualCode) {
        if (inputCode != null && inputCode.equals(actualCode)) {
            // В реальному проекті тут ми б оновлювали статус клієнта (is_verified = true)
            return true;
        }
        return false;
    }

    public Client login(String email, String rawPassword) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        Optional<Client> clientOpt = clientRepository.findByEmail(email);
        
        if (clientOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        Client client = clientOpt.get();
        if (!passwordHasher.verify(rawPassword, client.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return client;
    }
}
