package com.mysuperproject.atelier;

/**
 * Main application class for the Atelier Management System.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Atelier Management System started!");
        
        // Тут в майбутньому можна ініціалізувати Flyway програмно, якщо не використовується Spring Boot:
        // Flyway flyway = Flyway.configure().dataSource("jdbc:postgresql://localhost:5432/atelier_db", "user", "password").load();
        // flyway.migrate();
    }
}
