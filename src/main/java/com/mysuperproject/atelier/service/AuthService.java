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

        Optional<Client> existingClient = clientRepository.findByEmail(client.getEmail());
        if (existingClient.isPresent()) {
            throw new IllegalArgumentException("Client with this email already exists");
        }

        String hashedPassword = passwordHasher.hash(rawPassword);
        client.setPassword(hashedPassword);

        Client savedClient = clientRepository.save(client);

        // Генеруємо випадковий код підтвердження (для симуляції)
        String verificationCode = String.format("%06d", new java.util.Random().nextInt(999999));

        emailSender.sendEmail(
                savedClient.getEmail(),
                "Підтвердження пошти",
                "Вітаємо, " + savedClient.getFirstName() + "!\nВаш код підтвердження: " + verificationCode
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
