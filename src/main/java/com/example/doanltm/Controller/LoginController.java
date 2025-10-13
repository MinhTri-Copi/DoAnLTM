package com.example.doanltm.Controller;

import com.example.doanltm.Model.LoginRequest;
import com.example.doanltm.Model.LoginResponse;
import com.example.doanltm.Model.User;
import com.example.doanltm.Service.TCPClientService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private CheckBox rememberMeCheckbox;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Label serverStatusLabel;
    
    private TCPClientService tcpClientService;
    
    @FXML
    public void initialize() {
        tcpClientService = new TCPClientService();
        checkServerConnection();
        passwordField.setOnAction(event -> handleLogin());
    }
    
    private void checkServerConnection() {
        new Thread(() -> {
            boolean connected = tcpClientService.connect();
            Platform.runLater(() -> {
                if (connected) {
                    serverStatusLabel.setText("Đã kết nối");
                    serverStatusLabel.setStyle("-fx-text-fill: #27ae60;");
                } else {
                    serverStatusLabel.setText("Mất kết nối");
                    serverStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
                }
            });
        }).start();
    }
    
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        
        if (!isValidEmail(email)) {
            showError("Email không hợp lệ!");
            return;
        }
        
        loginButton.setDisable(true);
        errorLabel.setVisible(false);
        
        new Thread(() -> {
            try {
                LoginRequest request = new LoginRequest(email, password);
                LoginResponse response = tcpClientService.login(request);
                
                Platform.runLater(() -> {
                    if (response != null && response.isSuccess()) {
                        handleLoginSuccess(response.getUser());
                    } else {
                        String message = response != null ? response.getMessage() : "Không thể kết nối đến server!";
                        showError(message);
                        loginButton.setDisable(false);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Lỗi: " + e.getMessage());
                    loginButton.setDisable(false);
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private void handleLoginSuccess(User user) {
        System.out.println("✅ Đăng nhập thành công: " + user);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText("Chào mừng " + user.getHoTen() + "!\nVai trò: " + user.getTenVaitro());
        alert.showAndWait();
        
        // TODO: Chuyển sang màn hình chính
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        
        emailField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
        passwordField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
        
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> {
                    emailField.setStyle("");
                    passwordField.setStyle("");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    public void cleanup() {
        if (tcpClientService != null) {
            tcpClientService.disconnect();
        }
    }
}
