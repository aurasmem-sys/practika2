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
        // Set AtlantaFX theme
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mysuperproject/atelier/view/main_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        
        primaryStage.setTitle("Atelier Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
