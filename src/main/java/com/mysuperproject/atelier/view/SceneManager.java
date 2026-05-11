package com.mysuperproject.atelier.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import java.util.prefs.Preferences;

public class SceneManager {

    private static Stage primaryStage;
    private static boolean isDarkTheme = false;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        try {
            javafx.scene.image.Image icon = new javafx.scene.image.Image(SceneManager.class.getResourceAsStream("/images/icon.png"));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Не вдалося завантажити іконку: " + e.getMessage());
        }
    }

    public static void applySavedTheme() {
        isDarkTheme = Preferences.userNodeForPackage(SceneManager.class).getBoolean("isDarkTheme", false);
        setTheme(isDarkTheme);
    }

    public static void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        setTheme(isDarkTheme);
        Preferences.userNodeForPackage(SceneManager.class).putBoolean("isDarkTheme", isDarkTheme);
    }

    private static void setTheme(boolean dark) {
        if (dark) {
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        } else {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        }
    }

    public static void switchScene(String fxmlPath, String title, int width, int height) {
        try {
            // Завантажуємо розмітку екрану з FXML файлу
            FXMLLoader fxmlLoader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = fxmlLoader.load();
            
            primaryStage.setTitle(title);
            
            if (primaryStage.getScene() == null) {
                // Якщо це самий перший запуск - створюємо нову сцену і розтягуємо на весь екран
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                primaryStage.setMaximized(true);
            } else {
                // Якщо вікно вже є, просто міняємо "начинку" (root), щоб вікно не моргало і не збивався розмір
                primaryStage.getScene().setRoot(root);
            }
            
            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
