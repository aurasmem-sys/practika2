package com.mysuperproject.atelier;

public class Launcher {
    public static void main(String[] args) {
        // Запускаємо наш JavaFX додаток через цей клас-обгортку.
        // Це вирішує проблему "JavaFX runtime components are missing" 
        // при запуску без налаштування Java Modules (module-info.java).
        AtelierApplication.main(args);
    }
}
