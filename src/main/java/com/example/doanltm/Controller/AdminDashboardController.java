package com.example.doanltm.Controller;

import com.example.doanltm.DAO.CaLamDAO;
import com.example.doanltm.Model.CaLam;
import com.example.doanltm.Model.DangKy;
import com.example.doanltm.Model.User;
import com.example.doanltm.Service.TCPClientService;
import com.example.doanltm.Request.*;
import com.example.doanltm.Response.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminDashboardController {

    // Header
    @FXML private Label titleLabel; // "Hệ thống quản lí làm việc"

    // Sidebar (buttons handled in FXML)

    // Filters
    @FXML private ComboBox<CaLam> caFilterCombo;
    @FXML private DatePicker ngayFilterPicker;
    @FXML private DatePicker thangThongKePicker; // pick any date in the month

    // Stats labels
    @FXML private Label monthlyTotalLabel;
    @FXML private Label normalShiftLabel;
    @FXML private Label brokenShiftLabel;

    // Registrations table
    @FXML private TableView<DangKy> regTable;
    @FXML private TableColumn<DangKy, String> colNguoiDung;
    @FXML private TableColumn<DangKy, LocalDate> colNgay;
    @FXML private TableColumn<DangKy, String> colMoTa;
    @FXML private TableColumn<DangKy, String> colLoaiCa;
    @FXML private TableColumn<DangKy, String> colTrangThai;
    @FXML private TableColumn<DangKy, Void> colActions;

    // Dynamic content container and content panes
    @FXML private StackPane contentContainer;
    @FXML private VBox overviewContent;
    @FXML private VBox shiftManagementContent;
    @FXML private VBox userManagementContent;
    @FXML private VBox scheduleManagementContent;
    
    // Sidebar buttons
    @FXML private Button overviewBtn;
    @FXML private Button shiftMgmtBtn;
    @FXML private Button userMgmtBtn;
    @FXML private Button scheduleMgmtBtn;

    private final CaLamDAO caLamDAO = new CaLamDAO();
    private final TCPClientService tcpClient = new TCPClientService();
    private final ObservableList<DangKy> registrations = FXCollections.observableArrayList();

    private User currentUser;

    @FXML
    public void initialize() {
        if (titleLabel != null) titleLabel.setText("Hệ thống quản lý làm việc");
        
        // Kết nối TCP
        if (!tcpClient.connect()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Không thể kết nối đến server!", ButtonType.OK);
            alert.setHeaderText(null);
            alert.setTitle("Lỗi kết nối");
            alert.showAndWait();
        }
        
        // Defer access check to allow LoginController to set currentUser
        Platform.runLater(this::verifyAccessOrRedirect);

        // Init filters
        if (thangThongKePicker != null) thangThongKePicker.setValue(LocalDate.now());
        if (ngayFilterPicker != null) ngayFilterPicker.setValue(null);

        if (caFilterCombo != null) {
            caFilterCombo.getItems().setAll(caLamDAO.getAllCaLam());
            caFilterCombo.setConverter(new javafx.util.StringConverter<CaLam>() {
                @Override public String toString(CaLam c) { return c == null ? "" : c.getMoTa() + " (" + c.getThoiGian() + ")"; }
                @Override public CaLam fromString(String s) { return null; }
            });
        }

        setupRegTable();
        refreshStats();
        refreshRegistrations();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        verifyAccessOrRedirect();
    }

    private void verifyAccessOrRedirect() {
        if (currentUser == null || !currentUser.isAdmin()) {
            // Not allowed: redirect to login
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Bạn không có quyền truy cập trang admin!", ButtonType.OK);
                alert.setHeaderText(null);
                alert.setTitle("Cảnh báo");
                alert.showAndWait();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/doanltm/view/login-view.fxml"));
                    Scene scene = new Scene(loader.load());
                    Stage stage = (Stage) (titleLabel != null ? titleLabel.getScene().getWindow() : contentContainer.getScene().getWindow());
                    stage.setScene(scene);
                    stage.setTitle("Đăng Nhập");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void setupRegTable() {
        if (regTable == null) return;
        regTable.setItems(registrations);
        if (colNguoiDung != null) colNguoiDung.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTenNguoiDung()));
        if (colNgay != null) colNgay.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getNgayLam()));
        if (colMoTa != null) colMoTa.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMoTaCaLam()));
        if (colLoaiCa != null) colLoaiCa.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLoaiCa()));
        if (colTrangThai != null) {
            colTrangThai.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTrangthai().getValue()));
            colTrangThai.setCellFactory(col -> new TableCell<DangKy, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                        return;
                    }
                    setText(item);
                    setStyle("-fx-alignment: CENTER; -fx-font-weight: 700;");
                    String lower = item.toLowerCase();
                    if (lower.contains("chờ")) {
                        setTextFill(javafx.scene.paint.Color.web("#b45309")); // amber-700
                    } else if (lower.contains("đã")) {
                        setTextFill(javafx.scene.paint.Color.web("#047857")); // emerald-700
                    } else if (lower.contains("từ chối")) {
                        setTextFill(javafx.scene.paint.Color.web("#b91c1c")); // red-700
                    } else {
                        setTextFill(javafx.scene.paint.Color.web("#111827"));
                    }
                }
            });
        }
        if (colActions != null) {
            colActions.setCellFactory(col -> new TableCell<DangKy, Void>() {
                private final Button btnApprove = new Button("Đã duyệt");
                private final Button btnPending = new Button("Chờ duyệt");
                private final Button btnReject = new Button("Từ chối");
                private final HBox box = new HBox(8, btnApprove, btnPending, btnReject);
                {
                    btnApprove.getStyleClass().add("primary-btn");
                    btnPending.getStyleClass().add("sidebar-btn");
                    btnReject.getStyleClass().add("danger-btn");
                    btnApprove.setOnAction(e -> updateStatus(DangKy.TrangThai.DA_DUYET));
                    btnPending.setOnAction(e -> updateStatus(DangKy.TrangThai.CHO_DUYET));
                    btnReject.setOnAction(e -> updateStatus(DangKy.TrangThai.TU_CHOI));
                }
                private void updateStatus(DangKy.TrangThai st) {
                    DangKy item = getTableView().getItems().get(getIndex());
                    
                    CapNhatTrangThaiRequest request = new CapNhatTrangThaiRequest(item.getMaDangky(), st);
                    CapNhatTrangThaiResponse response = tcpClient.capNhatTrangThai(request);
                    
                    if (response.isSuccess()) {
                        item.setTrangthai(st);
                        // Ẩn bộ nút ngay khi đã xử lý
                        box.setVisible(false);
                        box.setManaged(false);
                        getTableView().refresh();
                        refreshRegistrations();
                        refreshStats();
                        Alert a = new Alert(Alert.AlertType.INFORMATION, response.getMessage(), ButtonType.OK);
                        a.setHeaderText(null);
                        a.setTitle("Thành công");
                        a.showAndWait();
                    } else {
                        Alert a = new Alert(Alert.AlertType.ERROR, response.getMessage(), ButtonType.OK);
                        a.setHeaderText(null);
                        a.setTitle("Lỗi");
                        a.showAndWait();
                    }
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        return;
                    }
                    DangKy row = getTableView().getItems().get(getIndex());
                    boolean show = row != null && row.getTrangthai() == DangKy.TrangThai.CHO_DUYET;
                    box.setVisible(show);
                    box.setManaged(show);
                    setGraphic(box);
                }
            });
        }
    }

    @FXML
    private void handleApplyFilters() {
        refreshRegistrations();
    }

    @FXML
    private void handleClearFilters() {
        if (caFilterCombo != null) caFilterCombo.getSelectionModel().clearSelection();
        if (ngayFilterPicker != null) ngayFilterPicker.setValue(null);
        refreshRegistrations();
    }

    @FXML
    private void handleChangeMonth() {
        refreshStats();
    }

    private void refreshStats() {
        LocalDate m = thangThongKePicker != null && thangThongKePicker.getValue() != null ? thangThongKePicker.getValue() : LocalDate.now();
        
        ThongKeAdminRequest request = new ThongKeAdminRequest(m);
        ThongKeAdminResponse response = tcpClient.getThongKeAdmin(request);
        
        if (response.isSuccess()) {
            if (monthlyTotalLabel != null) monthlyTotalLabel.setText(String.valueOf(response.getMonthlyTotal()));
            if (normalShiftLabel != null) normalShiftLabel.setText(String.valueOf(response.getNormalShiftsCount()));
            if (brokenShiftLabel != null) brokenShiftLabel.setText(String.valueOf(response.getBrokenShiftsCount()));
        } else {
            System.err.println("Lỗi lấy thống kê: " + response.getMessage());
        }
    }

    private void refreshRegistrations() {
        Integer maCalam = null;
        if (caFilterCombo != null && caFilterCombo.getValue() != null) maCalam = caFilterCombo.getValue().getMaCalam();
        LocalDate ngay = ngayFilterPicker != null ? ngayFilterPicker.getValue() : null;
        
        DanhSachDangKyAdminRequest request = new DanhSachDangKyAdminRequest(maCalam, ngay);
        DanhSachDangKyAdminResponse response = tcpClient.getDanhSachDangKyAdmin(request);
        
        if (response.isSuccess() && response.getRegistrations() != null) {
            Platform.runLater(() -> {
                registrations.setAll(response.getRegistrations());
                if (regTable != null) regTable.refresh();
            });
        } else {
            System.err.println("Lỗi lấy danh sách đăng ký: " + response.getMessage());
            Platform.runLater(() -> registrations.clear());
        }
    }

    private void reloadCaFilterList() {
        if (caFilterCombo == null) return;
        CaLam selected = caFilterCombo.getValue();
        List<CaLam> list = caLamDAO.getAllCaLam();
        caFilterCombo.getItems().setAll(list);
        if (selected != null) {
            list.stream().filter(c -> c.getMaCalam() == selected.getMaCalam()).findFirst()
                .ifPresent(caFilterCombo::setValue);
        }
    }

    @FXML
    private void handleRefreshData() {
        // Reset tháng về hôm nay và xóa các bộ lọc, sau đó tải lại dữ liệu từ DB
        if (thangThongKePicker != null) thangThongKePicker.setValue(LocalDate.now());
        if (caFilterCombo != null) caFilterCombo.getSelectionModel().clearSelection();
        if (ngayFilterPicker != null) ngayFilterPicker.setValue(null);
        reloadCaFilterList();
        refreshStats();
        refreshRegistrations();
    }

    // Dynamic content switching methods
    @FXML private void showOverview() {
        switchToContent(overviewContent);
        updateSidebarActiveState(overviewBtn);
    }
    
    @FXML private void showShiftManagement() {
        switchToContent(shiftManagementContent);
        updateSidebarActiveState(shiftMgmtBtn);
    }
    
    @FXML private void showUserManagement() {
        switchToContent(userManagementContent);
        updateSidebarActiveState(userMgmtBtn);
    }
    
    @FXML private void showScheduleManagement() {
        switchToContent(scheduleManagementContent);
        updateSidebarActiveState(scheduleMgmtBtn);
    }
    
    private void switchToContent(VBox targetContent) {
        if (contentContainer == null || targetContent == null) return;
        
        // Hide all content panes
        hideAllContent();
        
        // Show the target content
        targetContent.setVisible(true);
        targetContent.setManaged(true);
    }
    
    private void hideAllContent() {
        if (overviewContent != null) {
            overviewContent.setVisible(false);
            overviewContent.setManaged(false);
        }
        if (shiftManagementContent != null) {
            shiftManagementContent.setVisible(false);
            shiftManagementContent.setManaged(false);
        }
        if (userManagementContent != null) {
            userManagementContent.setVisible(false);
            userManagementContent.setManaged(false);
        }
        if (scheduleManagementContent != null) {
            scheduleManagementContent.setVisible(false);
            scheduleManagementContent.setManaged(false);
        }
    }
    
    private void updateSidebarActiveState(Button activeButton) {
        // Remove active state from all buttons
        if (overviewBtn != null) overviewBtn.getStyleClass().remove("sidebar-active");
        if (shiftMgmtBtn != null) shiftMgmtBtn.getStyleClass().remove("sidebar-active");
        if (userMgmtBtn != null) userMgmtBtn.getStyleClass().remove("sidebar-active");
        if (scheduleMgmtBtn != null) scheduleMgmtBtn.getStyleClass().remove("sidebar-active");
        
        // Add active state to the clicked button
        if (activeButton != null && !activeButton.getStyleClass().contains("sidebar-active")) {
            activeButton.getStyleClass().add("sidebar-active");
        }
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle("Thông báo");
        a.showAndWait();
    }

    @FXML private void handleLogout() {
        try {
            // Ngắt kết nối TCP
            tcpClient.disconnect();
            
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/doanltm/view/login-view.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            javafx.stage.Stage stage = (javafx.stage.Stage) titleLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Đăng Nhập");
        } catch (Exception e) {
            e.printStackTrace();
            showInfo("Không thể đăng xuất");
        }
    }
}
