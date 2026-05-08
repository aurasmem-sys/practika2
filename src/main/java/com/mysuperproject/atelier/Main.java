package com.mysuperproject.atelier;

import com.mysuperproject.atelier.entity.Client;
import com.mysuperproject.atelier.infrastructure.mail.ConsoleEmailSender;
import com.mysuperproject.atelier.infrastructure.mail.EmailSender;
import com.mysuperproject.atelier.infrastructure.security.PasswordHasher;
import com.mysuperproject.atelier.infrastructure.security.SHA256PasswordHasher;
import com.mysuperproject.atelier.pool.ConnectionPool;
import com.mysuperproject.atelier.repository.ClientRepository;
import com.mysuperproject.atelier.service.AuthService;
import com.mysuperproject.atelier.service.ClientService;

public class Main {

    public static void main(String[] args) {
        System.out.println("Atelier Management System started!");

        try {
            // 1. Ініціалізація інфраструктури та репозиторіїв
            ClientRepository clientRepository = new ClientRepository();
            PasswordHasher passwordHasher = new SHA256PasswordHasher();
            EmailSender emailSender = new ConsoleEmailSender();

            // 2. Ініціалізація сервісів
            AuthService authService = new AuthService(clientRepository, passwordHasher, emailSender);
            ClientService clientService = new ClientService(clientRepository);

            // --- ТЕСТУВАННЯ АУТЕНТИФІКАЦІЇ ТА РЕЄСТРАЦІЇ ---
            System.out.println("\n--- ТЕСТУВАННЯ РЕЄСТРАЦІЇ ---");
            Client newClient = Client.builder()
                    .firstName("Петро")
                    .lastName("Коваленко")
                    .phoneNumber("+380509998877")
                    .email("petro.kovalenko@example.com")
                    .build();

            // Реєструємо клієнта
            String rawPassword = "MySecurePassword123";
            System.out.println("Реєструємо користувача: " + newClient.getEmail());
            String verificationCode = authService.register(newClient, rawPassword);
            System.out.println("Очікуваний код підтвердження: " + verificationCode);

            // Перевіряємо верифікацію пошти
            boolean isVerified = authService.verifyEmail(newClient.getEmail(), verificationCode, verificationCode);
            System.out.println("Чи підтверджена пошта? " + isVerified);

            System.out.println("\n--- ТЕСТУВАННЯ ЛОГІНУ ---");
            // Пробуємо залогінитись з правильним паролем
            System.out.println("Спроба входу з правильним паролем...");
            Client loggedInClient = authService.login("petro.kovalenko@example.com", "MySecurePassword123");
            System.out.println("Успішний вхід! Вітаємо, " + loggedInClient.getFirstName());

            // Пробуємо залогінитись з неправильним паролем
            System.out.println("\nСпроба входу з неправильним паролем...");
            try {
                authService.login("petro.kovalenko@example.com", "WrongPassword");
            } catch (IllegalArgumentException e) {
                System.out.println("Помилка входу (як і очікувалося): " + e.getMessage());
            }

            // --- ТЕСТУВАННЯ ЗВИЧАЙНИХ СЕРВІСІВ ---
            System.out.println("\n--- ТЕСТУВАННЯ CLIENT SERVICE ---");
            System.out.println("Всі клієнти в базі:");
            for (Client c : clientService.getAll()) {
                System.out.println(c.getId() + " | " + c.getFirstName() + " " + c.getLastName() + " | " + c.getEmail());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionPool.getInstance().closePool();
        }
    }
}
