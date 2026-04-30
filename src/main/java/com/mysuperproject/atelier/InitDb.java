package com.mysuperproject.atelier;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class InitDb {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:atelier.db";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Starting database initialization...");

            // Читаємо і виконуємо DDL
            String ddl = new String(Files.readAllBytes(Paths.get("db/DDL.sql")));
            String[] ddlStatements = ddl.split(";");
            for (String sql : ddlStatements) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql);
                }
            }
            System.out.println("DDL executed successfully (tables created).");
            
            // Читаємо і виконуємо DML
            String dml = new String(Files.readAllBytes(Paths.get("db/DML.sql")));
            String[] dmlStatements = dml.split(";");
            for (String sql : dmlStatements) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql);
                }
            }
            System.out.println("DML executed successfully (data inserted).");
            System.out.println("SQLite database is fully ready!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
