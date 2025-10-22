package com.example.doanltm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/doanltm/view/admin-dashboard.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 800);
        
        stage.setTitle("Hệ thống quản lí làm việc - Admin Panel");
        stage.setScene(scene);
        
        // Cho phép resize, zoom
        stage.setResizable(true);
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        
        // Mở ở chế độ maximize
        stage.setMaximized(true);
        
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
