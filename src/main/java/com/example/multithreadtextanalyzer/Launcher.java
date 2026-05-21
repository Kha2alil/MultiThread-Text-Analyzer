package com.example.multithreadtextanalyzer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Team : KHALFI KHALIL , BOUDERSA ZIN EDDINE , G01


public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws Exception {


        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/example/multithreadtextanalyzer/main.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setTitle("Multithreaded Text Analyzer");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
