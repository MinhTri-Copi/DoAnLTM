package com.example.doanltm.Controller;

import com.example.doanltm.Request.LoginRequest;
import com.example.doanltm.Response.LoginResponse;
import com.example.doanltm.Model.User;
import com.example.doanltm.Service.TCPClientService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordVisibleField;

    @FXML
    private CheckBox rememberMeCheckbox;

    @FXML
    private Button togglePasswordButton;

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
        if (passwordField != null) {
            passwordField.setOnAction(event -> handleLogin());
        }
        // Setup password toggle
        syncPasswordFields();
    }

    private void checkServerConnection() {
        new Thread(() -> {
            boolean connected = tcpClientService.connect();
            Platform.runLater(() -> {
                if (serverStatusLabel != null) {
                    if (connected) {
                        serverStatusLabel.setText("Đã kết nối");
                        serverStatusLabel.setStyle("-fx-text-fill: #27ae60;");
                    } else {
                        serverStatusLabel.setText("Mất kết nối");
                        serverStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
                    }
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

        if (loginButton != null) {
            loginButton.setDisable(true);
        }
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }

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
                        if (loginButton != null) {
                            loginButton.setDisable(false);
                        }
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Lỗi: " + e.getMessage());
                    if (loginButton != null) {
                        loginButton.setDisable(false);
                    }
                });
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * XỬ LÝ ĐĂNG NHẬP THÀNH CÔNG - CHỈ GIỮ 1 METHOD
     */
    private void handleLoginSuccess(User user) {
        System.out.println("✅ Đăng nhập thành công: " + user);

        try {
            // Chuyển hướng theo role
            if (user.getMaVaitro() == 1) {
                // Admin - chuyển sang trang admin
                showAdminDashboard(user);
            } else if (user.getMaVaitro() == 2) {
                // Nhân viên - chuyển sang trang user
                showUserDashboard(user);
            } else {
                // Role không xác định
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo");
                alert.setHeaderText(null);
                alert.setContentText("Vai trò người dùng không xác định!");
                alert.showAndWait();
            }
        } catch (IOException e) {
            showError("Lỗi khi chuyển trang: " + e.getMessage());
            if (loginButton != null) {
                loginButton.setDisable(false);
            }
            e.printStackTrace();
        }
    }

    /**
     * Hiển thị Dashboard cho User (Nhân viên)
     */
    private void showUserDashboard(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/doanltm/view/dashboard-view.fxml"));
        Scene scene = new Scene(loader.load());
        
        EnhancedUserDashboardController controller = loader.getController();
        // TRUYỀN CÙNG TCPClientService ĐỂ GIỮ KẾT NỐI
        controller.setTCPClientService(tcpClientService);
        controller.setCurrentUser(user);
        
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Dashboard - Nhân viên");
        stage.setMaximized(true);
    }
    
    private void showAdminDashboard(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/doanltm/view/admin-dashboard.fxml"));
        Scene scene = new Scene(loader.load());

        AdminDashboardController controller = loader.getController();
        controller.setCurrentUser(user);

        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Admin Panel - Hệ thống quản lí làm việc");
        stage.setMaximized(true);
    }

    @FXML
    private void handleOpenRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/doanltm/view/register-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Đăng ký tài khoản");
        } catch (IOException e) {
            showError("Không thể mở trang đăng ký: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
        
        if (emailField != null) {
            emailField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
        }
        if (passwordField != null) {
            passwordField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
        }
        
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> {
                    if (emailField != null) {
                        emailField.setStyle("");
                    }
                    if (passwordField != null) {
                        passwordField.setStyle("");
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    @FXML
    private void handleTogglePassword() {
        if (passwordField.isVisible()) {
            // Switch to visible text field
            String currentPassword = passwordField.getText();
            passwordVisibleField.setText(currentPassword);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            if (togglePasswordButton != null) {
                togglePasswordButton.setStyle("-fx-text: '👁️'; -fx-font-size: 12;");
            }
        } else {
            // Switch to password field
            String currentPassword = passwordVisibleField.getText();
            passwordField.setText(currentPassword);
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            if (togglePasswordButton != null) {
                togglePasswordButton.setStyle("-fx-text: '🔒'; -fx-font-size: 12;");
            }
        }
    }

    private void syncPasswordFields() {
        if (passwordField != null && passwordVisibleField != null) {
            // Sync from hidden to visible
            passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (passwordVisibleField.isVisible()) {
                    passwordVisibleField.setText(newVal);
                }
            });
            // Sync from visible to hidden
            passwordVisibleField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (passwordField.isVisible()) {
                    passwordField.setText(newVal);
                }
            });
        }
    }
    
    public void cleanup() {
        // KHÔNG disconnect ở đây vì UserDashboardController đang dùng
    }
}
