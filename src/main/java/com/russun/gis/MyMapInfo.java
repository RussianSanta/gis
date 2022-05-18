package com.russun.gis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MyMapInfo extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main_page.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.setTitle("MapInfoV2");
        stage.setResizable(false);
        stage.centerOnScreen();

        MainPageController.setPrimaryStage(stage);

        stage.show();
    }
}