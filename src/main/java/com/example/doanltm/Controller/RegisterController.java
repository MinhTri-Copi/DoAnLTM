package com.example.doanltm.Controller;

import com.example.doanltm.DAO.RegistrationDAO;
import com.example.doanltm.DAO.UserDAO;
import com.example.doanltm.Model.User;
import com.example.doanltm.Util.PasswordHashUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
        @FXML
        private PasswordField passwordField;
        @FXML
        private TextField passwordVisibleField;
        @FXML
        private Button togglePasswordButton;
    
        @FXML
        private PasswordField confirmPasswordField;
        @FXML
        private TextField confirmPasswordVisibleField;
        @FXML
        private Button toggleConfirmPasswordButton;
    // Removed roleComboBox - default role is Employee (2)
    @FXML private Label messageLabel;
    @FXML private Button registerButton;
    @FXML private Button backToLoginButton;

    @FXML private Label req6CharLabel;
    @FXML private Label reqUppercaseLabel;
    @FXML private Label reqNumberLabel;
    @FXML private Label reqSpecialLabel;

    private final UserDAO userDAO = new UserDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    @FXML
    public void initialize() {
        setupPasswordValidationListeners();
        syncPasswordFields();
        syncConfirmPasswordFields();
    }

    private void setupPasswordValidationListeners() {
        if (passwordField != null) {
            passwordField.textProperty().addListener((obs, oldVal, newVal) -> handlePasswordValidation());
        }
        if (passwordVisibleField != null) {
            passwordVisibleField.textProperty().addListener((obs, oldVal, newVal) -> handlePasswordValidation());
        }
        if (confirmPasswordField != null) {
            confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> handlePasswordValidation());
        }
        if (confirmPasswordVisibleField != null) {
            confirmPasswordVisibleField.textProperty().addListener((obs, oldVal, newVal) -> handlePasswordValidation());
        }
    }

    @FXML
    public void handleTogglePassword() {
        if (passwordField.isVisible()) {
            passwordVisibleField.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            togglePasswordButton.setText("🔒");
        } else {
            passwordField.setText(passwordVisibleField.getText());
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            togglePasswordButton.setText("👁️");
        }
    }

    @FXML
    public void handleToggleConfirmPassword() {
        if (confirmPasswordField.isVisible()) {
            confirmPasswordVisibleField.setText(confirmPasswordField.getText());
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
            confirmPasswordVisibleField.setVisible(true);
            confirmPasswordVisibleField.setManaged(true);
            toggleConfirmPasswordButton.setText("🔒");
        } else {
            confirmPasswordField.setText(confirmPasswordVisibleField.getText());
            confirmPasswordVisibleField.setVisible(false);
            confirmPasswordVisibleField.setManaged(false);
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            toggleConfirmPasswordButton.setText("👁️");
        }
    }

    private void syncPasswordFields() {
        if (passwordField != null && passwordVisibleField != null) {
            passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (passwordVisibleField.isVisible()) {
                    passwordVisibleField.setText(newVal);
                }
            });
            passwordVisibleField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (passwordField.isVisible()) {
                    passwordField.setText(newVal);
                }
            });
        }
    }

    private void syncConfirmPasswordFields() {
        if (confirmPasswordField != null && confirmPasswordVisibleField != null) {
            confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (confirmPasswordVisibleField.isVisible()) {
                    confirmPasswordVisibleField.setText(newVal);
                }
            });
            confirmPasswordVisibleField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (confirmPasswordField.isVisible()) {
                    confirmPasswordField.setText(newVal);
                }
            });
        }
    }

    @FXML
    private void handleRegister() {
        String fullName = getText(fullNameField);
        String email = getText(emailField);
        String password = passwordField.isVisible() ? passwordField.getText() : passwordVisibleField.getText();
        String confirm = confirmPasswordField.isVisible() ? confirmPasswordField.getText() : confirmPasswordVisibleField.getText();
        // Default role is Employee (2)

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showMessage("Vui lòng nhập đầy đủ thông tin!", true);
            return;
        }
        if (!isValidEmail(email)) {
            showMessage("Email không hợp lệ!", true);
            return;
        }
        if (!PasswordHashUtil.isPasswordStrong(password)) {
            showMessage("Mật khẩu không đủ mạnh! Cần ít nhất 6 ký tự, 1 chữ hoa, 1 chữ số và 1 ký tự đặc biệt.", true);
            return;
        }
        if (!password.equals(confirm)) {
            showMessage("Mật khẩu xác nhận không khớp!", true);
            return;
        }
        if (userDAO.isEmailExists(email)) {
            showMessage("Email đã tồn tại trong hệ thống!", true);
            return;
        }

        int maVaitro = 2; // Always Employee role
        String hashedPassword = PasswordHashUtil.hashPassword(password);
        System.out.println("📝 [REGISTER] Email: " + email + " | FullName: " + fullName);
        System.out.println("🔐 [REGISTER] Password hashed: " + hashedPassword.substring(0, Math.min(20, hashedPassword.length())) + "...");
        User created = registrationDAO.createUser(fullName, email, hashedPassword, maVaitro);
        if (created != null) {
            System.out.println("✅ [REGISTER] Đăng ký thành công! User ID: " + created.getMaNguoidung());
            // Luôn quay về trang đăng nhập sau khi đăng ký (kể cả Admin)
            try {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đăng ký thành công! Vui lòng đăng nhập.", ButtonType.OK);
                alert.setHeaderText(null);
                alert.setTitle("Thành công");
                alert.showAndWait();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/doanltm/view/login-view.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) registerButton.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Đăng Nhập");
            } catch (IOException e) {
                showMessage("Không thể quay lại trang đăng nhập!", true);
            }
            clearForm();
        } else {
            showMessage("Đăng ký thất bại!", true);
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/doanltm/view/login-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Đăng Nhập");
        } catch (IOException e) {
            showMessage("Không thể quay lại trang đăng nhập!", true);
        }
    }

    private void clearForm() {
        if (fullNameField != null) fullNameField.clear();
        if (emailField != null) emailField.clear();
        if (passwordField != null) passwordField.clear();
        if (confirmPasswordField != null) confirmPasswordField.clear();
        // No role selection to clear
    }

    private void showMessage(String msg, boolean isError) {
        if (messageLabel != null) {
            messageLabel.setText(msg);
            messageLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
            messageLabel.setVisible(true);
        }
    }

    private String getText(TextField tf) {
        return tf != null && tf.getText() != null ? tf.getText().trim() : "";
    }

    private boolean isValidEmail(String email) {
        return Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches();
    }

    @FXML
    private void handlePasswordValidation() {
        String password = passwordField.isVisible() ? passwordField.getText() : passwordVisibleField.getText();
        
        boolean hasMinLength = password.length() >= 6;
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
//                   Cần hai dấu gạch chéo ngược
        // Check for special characters without complex regex escaping
        boolean hasSpecialChar = false;
        String specialChars = "!@#$%";
        for (char c : password.toCharArray()) {
            if (specialChars.indexOf(c) >= 0) {
                hasSpecialChar = true;
                break;
            }
        }
        
        updateRequirementLabel(req6CharLabel, hasMinLength, "✓ Tối thiểu 6 kỷ tự", "✗ Tối thiểu 6 kỷ tự");
        updateRequirementLabel(reqUppercaseLabel, hasUppercase, "✓ 1 chữ hoa (A-Z)", "✗ 1 chữ hoa (A-Z)");
        updateRequirementLabel(reqNumberLabel, hasDigit, "✓ 1 số (0-9)", "✗ 1 số (0-9)");
        updateRequirementLabel(reqSpecialLabel, hasSpecialChar, "✓ 1 kỷ đặc biệt (!@#$%^&*...)", "✗ 1 kỷ đặc biệt (!@#$%^&*...)");
    }

    private void updateRequirementLabel(Label label, boolean isMet, String successText, String failText) {
        if (label != null) {
            if (isMet) {
                label.setStyle("-fx-text-fill: #27ae60;");
                label.setText(successText);
            } else {
                label.setStyle("-fx-text-fill: #e74c3c;");
                label.setText(failText);
            }
        }
    }
}