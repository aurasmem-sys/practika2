package com.mysuperproject.atelier;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class InitDb {
    
    // Метод для ручного скидання (використовувався раніше)
    public static void main(String[] args) {
        try {
            java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get("atelier.db"));
            initializeGracefully();
            System.out.println("База даних успішно перестворена вручну!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Безпечний метод: перевіряє, чи є таблиці, і якщо немає — створює
    public static void initializeGracefully() {
        String dbFile = "atelier.db";
        String url = "jdbc:sqlite:" + dbFile;
        
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            
            // Перевіряємо чи є таблиця clients
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='clients'");
            rs.next();
            int count = rs.getInt(1);
            
            if (count == 0) {
                System.out.println("Базу даних не знайдено або вона порожня. Починаю ініціалізацію...");
                
                // Читаємо і виконуємо DDL
                executeSqlFile(stmt, "/db/DDL.sql");
                System.out.println("DDL виконано успішно (таблиці створено).");
                
                // Читаємо і виконуємо DML
                executeSqlFile(stmt, "/db/DML.sql");
                System.out.println("DML виконано успішно (базові дані додано).");
            } else {
                System.out.println("База даних вже існує та готова до роботи.");
            }
        } catch (Exception e) {
            System.err.println("Помилка ініціалізації бази даних: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void executeSqlFile(Statement stmt, String resourcePath) throws Exception {
        InputStream is = InitDb.class.getResourceAsStream(resourcePath);
        if (is == null) {
            throw new RuntimeException("Не знайдено файл SQL: " + resourcePath);
        }
        
        Scanner scanner = new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A");
        String sqlContent = scanner.hasNext() ? scanner.next() : "";
        
        // Виконуємо весь файл цілком, щоб не зламати тригери (CREATE TRIGGER містить крапки з комою всередині BEGIN...END)
        if (!sqlContent.trim().isEmpty()) {
            stmt.executeUpdate(sqlContent);
        }
    }
}
