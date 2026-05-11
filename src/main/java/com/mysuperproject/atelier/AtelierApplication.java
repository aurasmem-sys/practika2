package com.mysuperproject.atelier;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AtelierApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Підтягуємо збережену тему (світлу або темну), щоб не скидалась при перезапуску
        com.mysuperproject.atelier.view.SceneManager.applySavedTheme();
        
        // Зберігаємо головне вікно, щоб потім в ньому міняти сцени (екрани)
        com.mysuperproject.atelier.view.SceneManager.setPrimaryStage(primaryStage);
        
        // ПЕРЕВІРКА БАЗИ ДАНИХ (створить, якщо користувач її видалив)
        com.mysuperproject.atelier.InitDb.initializeGracefully();
        
        // Першим ділом запускаємо вікно логіну
        com.mysuperproject.atelier.view.SceneManager.switchScene("/com/mysuperproject/atelier/view/login_view.fxml", "Вхід в систему", 400, 600);
    }

    public static void main(String[] args) {
        // Стандартний запуск JavaFX програми
        launch(args);
    }
}
