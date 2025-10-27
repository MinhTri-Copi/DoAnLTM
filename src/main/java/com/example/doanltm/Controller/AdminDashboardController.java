package com.example.doanltm.Controller;

import com.example.doanltm.DAO.CaLamDAO;
import com.example.doanltm.DAO.UserDAO;
import com.example.doanltm.DAO.DangKyDAO;
import com.example.doanltm.Model.CaLam;
import com.example.doanltm.Model.DangKy;
import com.example.doanltm.Model.User;
import com.example.doanltm.Model.ShiftStatusMonth;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.control.ScrollPane;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import com.example.doanltm.Service.NotificationListener;
import com.example.doanltm.Request.NewRegistrationNotification;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class AdminDashboardController implements NotificationListener {

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
    
    // Pagination controls
    @FXML private Button btnPrevPage;
    @FXML private Button btnNextPage;
    @FXML private Label lblPageInfo;
    @FXML private Label lblTotalRecords;

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
    
    // Pagination state
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalRecords = 0;
    
    // Shift Management
    @FXML private TableView<CaLam> shiftTable;
    @FXML private TableColumn<CaLam, Number> colShiftId;
    @FXML private TableColumn<CaLam, String> colShiftDesc;
    @FXML private TableColumn<CaLam, String> colShiftStart;
    @FXML private TableColumn<CaLam, String> colShiftEnd;
    @FXML private TableColumn<CaLam, Number> colShiftMax;
    @FXML private TableColumn<CaLam, Void> colShiftActions;
    @FXML private TextField tfSearchShift;
    @FXML private Button btnAddShift;
    @FXML private Button btnShiftPrev;
    @FXML private Button btnShiftNext;
    @FXML private Label lblShiftPageInfo;
    @FXML private Label lblTotalShifts;
    
    private final ObservableList<CaLam> shifts = FXCollections.observableArrayList();
    private int shiftCurrentPage = 1;
    private int shiftPageSize = 10;
    private int shiftTotalRecords = 0;
    private String shiftSearchKeyword = "";
    
    // User Management
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Number> colUserId;
    @FXML private TableColumn<User, String> colUserEmail;
    @FXML private TableColumn<User, String> colUserName;
    @FXML private TableColumn<User, String> colUserRole;
    @FXML private TableColumn<User, Void> colUserActions;
    @FXML private TextField tfSearchUser;
    @FXML private Button btnAddUser;
    @FXML private Button btnUserPrev;
    @FXML private Button btnUserNext;
    @FXML private Label lblUserPageInfo;
    @FXML private Label lblTotalUsers;
    
    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private int userCurrentPage = 1;
    private int userPageSize = 10;
    private int userTotalRecords = 0;
    private String userSearchKeyword = "";
    
    // Schedule Management
    @FXML private TableView<DangKy> scheduleTable;
    @FXML private TableColumn<DangKy, String> colScheduleName;
    @FXML private TableColumn<DangKy, String> colScheduleDate;
    @FXML private TableColumn<DangKy, String> colScheduleShift;
    @FXML private TableColumn<DangKy, String> colScheduleTime;
    @FXML private TableColumn<DangKy, String> colScheduleStatus;
    @FXML private TableColumn<DangKy, Void> colScheduleActions;
    @FXML private TextField tfSearchSchedule;
    @FXML private DatePicker dpFromDate;
    @FXML private DatePicker dpToDate;
    @FXML private Button btnSchedulePrev;
    @FXML private Button btnScheduleNext;
    @FXML private Label lblSchedulePageInfo;
    @FXML private Label lblTotalSchedules;
    
    private final DangKyDAO dangKyDAO = new DangKyDAO();
    private final ObservableList<DangKy> schedules = FXCollections.observableArrayList();
    private int scheduleCurrentPage = 1;
    private int schedulePageSize = 10;
    private int scheduleTotalRecords = 0;
    private String scheduleSearchKeyword = "";

    // Monthly Shift Status Management
    @FXML private ScrollPane monthlyShiftStatusContent;

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
        
        // Đăng ký listener để nhận thông báo real-time
        tcpClient.setNotificationListener(this);
        System.out.println("📄 Admin listener đã được đăng ký");
        
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
        setupShiftTable();
        setupUserTable();
        setupScheduleTable();
        refreshStats();
        refreshRegistrations();
        refreshShifts();
        refreshUsers();
        refreshSchedules();
        
        // Force table refresh sau khi load
        Platform.runLater(() -> {
            if (regTable != null) {
                regTable.refresh();
                regTable.layout();
            }
            if (shiftTable != null) {
                shiftTable.refresh();
                shiftTable.layout();
            }
            if (userTable != null) {
                userTable.refresh();
                userTable.layout();
            }
            if (scheduleTable != null) {
                scheduleTable.refresh();
                scheduleTable.layout();
            }
        });
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
                private final Button btnApprove = new Button("Duyệt");
                private final Button btnReject = new Button("Từ chối");
                private final HBox box = new HBox(8, btnApprove, btnReject);
                {
                    btnApprove.getStyleClass().add("danger-btn");
                    btnReject.getStyleClass().add("reject-btn");
                    btnApprove.setOnAction(e -> updateStatus(DangKy.TrangThai.DA_DUYET));
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
        currentPage = 1;  // Reset về trang 1 khi áp dụng filter
        refreshRegistrations();
    }

    @FXML
    private void handleClearFilters() {
        if (caFilterCombo != null) caFilterCombo.getSelectionModel().clearSelection();
        if (ngayFilterPicker != null) ngayFilterPicker.setValue(null);
        currentPage = 1;  // Reset về trang 1
        refreshRegistrations();
    }

    @FXML
    private void handleChangeMonth() {
        refreshStats();
    }

    private void refreshStats() {
        LocalDate m = thangThongKePicker != null && thangThongKePicker.getValue() != null ? thangThongKePicker.getValue() : LocalDate.now();
        System.out.println("\n📊 === refreshStats ===");
        System.out.println("📅 Current Date: " + LocalDate.now());
        System.out.println("📅 Picker Value: " + (thangThongKePicker != null ? thangThongKePicker.getValue() : "null"));
        System.out.println("📅 Using Month: " + m + " (Year: " + m.getYear() + ", Month: " + m.getMonthValue() + ")");
        
        ThongKeAdminRequest request = new ThongKeAdminRequest(m);
        System.out.println("🔍 Sending request with: " + m);
        ThongKeAdminResponse response = tcpClient.getThongKeAdmin(request);
        
        if (response.isSuccess()) {
            System.out.println("📈 Stats Response - Total: " + response.getMonthlyTotal() + ", Normal: " + response.getNormalShiftsCount() + ", Broken: " + response.getBrokenShiftsCount());
            if (monthlyTotalLabel != null) monthlyTotalLabel.setText(String.valueOf(response.getMonthlyTotal()));
            if (normalShiftLabel != null) normalShiftLabel.setText(String.valueOf(response.getNormalShiftsCount()));
            if (brokenShiftLabel != null) brokenShiftLabel.setText(String.valueOf(response.getBrokenShiftsCount()));
            System.out.println("=== End refreshStats ===\n");
        } else {
            System.err.println("❌ Lỗi lấy thống kê: " + response.getMessage());
        }
    }

    private void refreshRegistrations() {
        Integer maCalam = null;
        if (caFilterCombo != null && caFilterCombo.getValue() != null) maCalam = caFilterCombo.getValue().getMaCalam();
        LocalDate ngay = ngayFilterPicker != null ? ngayFilterPicker.getValue() : null;
        
        DanhSachDangKyAdminRequest request = new DanhSachDangKyAdminRequest(maCalam, ngay, "chờ duyệt", currentPage, pageSize);
        DanhSachDangKyAdminResponse response = tcpClient.getDanhSachDangKyAdmin(request);
        
        if (response.isSuccess() && response.getRegistrations() != null) {
            Platform.runLater(() -> {
                registrations.setAll(response.getRegistrations());
                totalRecords = response.getTotalRecords();
                updatePaginationControls();
                if (regTable != null) regTable.refresh();
            });
        } else {
            System.err.println("Lỗi lấy danh sách đăng ký: " + response.getMessage());
            Platform.runLater(() -> {
                registrations.clear();
                totalRecords = 0;
                updatePaginationControls();
            });
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
    
    private void switchToContent(ScrollPane targetContent) {
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
        if (monthlyShiftStatusContent != null) {
            monthlyShiftStatusContent.setVisible(false);
            monthlyShiftStatusContent.setManaged(false);
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

    // Pagination methods
    @FXML
    private void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--;
            refreshRegistrations();
        }
    }
    
    @FXML
    private void handleNextPage() {
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        if (currentPage < totalPages) {
            currentPage++;
            refreshRegistrations();
        }
    }
    
    private void updatePaginationControls() {
        int totalPages = totalRecords > 0 ? (int) Math.ceil((double) totalRecords / pageSize) : 1;
        
        // Cập nhật label tổng số bản ghi
        if (lblTotalRecords != null) {
            lblTotalRecords.setText(String.valueOf(totalRecords));
        }
        
        // Cập nhật thông tin trang
        if (lblPageInfo != null) {
            lblPageInfo.setText("Trang " + currentPage + "/" + totalPages);
        }
        
        // Enable/disable buttons
        if (btnPrevPage != null) {
            btnPrevPage.setDisable(currentPage <= 1);
        }
        
        if (btnNextPage != null) {
            btnNextPage.setDisable(currentPage >= totalPages || totalRecords == 0);
        }
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
    
    // ============ SHIFT MANAGEMENT METHODS ============
    
    private void setupShiftTable() {
        if (shiftTable == null) return;
        shiftTable.setItems(shifts);
        if (colShiftId != null) colShiftId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getMaCalam()));
        if (colShiftDesc != null) colShiftDesc.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMoTa()));
        if (colShiftStart != null) colShiftStart.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getGioBatdau())));
        if (colShiftEnd != null) colShiftEnd.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getGioKetthuc())));
        if (colShiftMax != null) colShiftMax.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getSoLuongToiDa()));
        
        if (colShiftActions != null) {
            colShiftActions.setCellFactory(col -> new TableCell<CaLam, Void>() {
                private final Button btnEdit = new Button("✏️");
                private final Button btnDelete = new Button("🗑️");
                private final HBox box = new HBox(8, btnEdit, btnDelete);
                {
                    btnEdit.getStyleClass().add("primary-btn");
                    btnDelete.getStyleClass().add("danger-btn");
                    btnEdit.setStyle("-fx-padding: 5px 15px;");
                    btnDelete.setStyle("-fx-padding: 5px 15px;");
                    btnEdit.setOnAction(e -> handleEditShift());
                    btnDelete.setOnAction(e -> handleDeleteShift());
                }
                
                private void handleEditShift() {
                    CaLam item = getTableView().getItems().get(getIndex());
                    showEditShiftDialog(item);
                }
                
                private void handleDeleteShift() {
                    CaLam item = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                        "Xác nhận xoá ca làm '" + item.getMoTa() + "'?", ButtonType.OK, ButtonType.CANCEL);
                    confirm.setHeaderText(null);
                    confirm.showAndWait().ifPresent(bt -> {
                        if (bt == ButtonType.OK) {
                            if (caLamDAO.deleteCaLam(item.getMaCalam())) {
                                shiftCurrentPage = 1;
                                refreshShifts();
                                showInfo("Xoá ca làm thành công!");
                            } else {
                                Alert err = new Alert(Alert.AlertType.ERROR, "Xoá ca làm thất bại!", ButtonType.OK);
                                err.setHeaderText(null);
                                err.showAndWait();
                            }
                        }
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : box);
                }
            });
        }
    }
    
    @FXML private void handleAddShift() {
        showAddShiftDialog();
    }
    
    @FXML private void handleSearchShift() {
        shiftSearchKeyword = tfSearchShift != null ? tfSearchShift.getText().trim() : "";
        shiftCurrentPage = 1;
        refreshShifts();
    }
    
    @FXML private void handleShiftPrevPage() {
        if (shiftCurrentPage > 1) {
            shiftCurrentPage--;
            refreshShifts();
        }
    }
    
    @FXML private void handleShiftNextPage() {
        int totalPages = (int) Math.ceil((double) shiftTotalRecords / shiftPageSize);
        if (shiftCurrentPage < totalPages) {
            shiftCurrentPage++;
            refreshShifts();
        }
    }
    
    private void refreshShifts() {
        int offset = (shiftCurrentPage - 1) * shiftPageSize;
        List<CaLam> shiftList = caLamDAO.getCaLamWithFilter(shiftSearchKeyword, shiftPageSize, offset);
        shiftTotalRecords = caLamDAO.countCaLamWithFilter(shiftSearchKeyword);
        
        System.out.println("⏰ [SHIFT MANAGEMENT] Lấy danh sách ca làm - Trang: " + shiftCurrentPage + " | Tìm kiếm: " + (shiftSearchKeyword.isEmpty() ? "(không)" : shiftSearchKeyword) + " | Tổng: " + shiftTotalRecords);
        
        Platform.runLater(() -> {
            shifts.setAll(shiftList);
            updateShiftPaginationControls();
            if (shiftTable != null) shiftTable.refresh();
        });
    }
    
    private void updateShiftPaginationControls() {
        int totalPages = shiftTotalRecords > 0 ? (int) Math.ceil((double) shiftTotalRecords / shiftPageSize) : 1;
        
        if (lblTotalShifts != null) {
            lblTotalShifts.setText(String.valueOf(shiftTotalRecords));
        }
        
        if (lblShiftPageInfo != null) {
            lblShiftPageInfo.setText("Trang " + shiftCurrentPage + "/" + totalPages);
        }
        
        if (btnShiftPrev != null) {
            btnShiftPrev.setDisable(shiftCurrentPage <= 1);
        }
        
        if (btnShiftNext != null) {
            btnShiftNext.setDisable(shiftCurrentPage >= totalPages || shiftTotalRecords == 0);
        }
    }
    
    private void showAddShiftDialog() {
        Dialog<CaLam> dlg = new Dialog<>();
        dlg.setTitle("Thêm Ca Làm Mới");
        dlg.setHeaderText("Nhập thông tin ca làm");
        
        ButtonType btnOK = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(btnOK, ButtonType.CANCEL);
        
        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new javafx.geometry.Insets(15));
        
        TextField tfDesc = new TextField();
        tfDesc.setPromptText("Ví dụ: Khung sáng");
        TextField tfStart = new TextField();
        tfStart.setPromptText("Ví dụ: 07:00:00");
        TextField tfEnd = new TextField();
        tfEnd.setPromptText("Ví dụ: 11:00:00");
        Spinner<Integer> spMax = new Spinner<>(1, 1000, 10);
        
        gp.addRow(0, new Label("Mô tả:"), tfDesc);
        gp.addRow(1, new Label("Giờ bắt đầu:"), tfStart);
        gp.addRow(2, new Label("Giờ kết thúc:"), tfEnd);
        gp.addRow(3, new Label("Số lượng tối đa:"), spMax);
        
        dlg.getDialogPane().setContent(gp);
        
        Node okBtn = dlg.getDialogPane().lookupButton(btnOK);
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            try {
                String desc = tfDesc.getText().trim();
                if (desc.isEmpty()) throw new IllegalArgumentException("Mô tả không được để trống");
                
                java.time.LocalTime start = java.time.LocalTime.parse(tfStart.getText(), java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                java.time.LocalTime end = java.time.LocalTime.parse(tfEnd.getText(), java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                
                if (!end.isAfter(start)) throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu");
                if (spMax.getValue() <= 0) throw new IllegalArgumentException("Số lượng phải > 0");
            } catch (Exception ex) {
                ev.consume();
                Alert err = new Alert(Alert.AlertType.ERROR, "Dữ liệu không hợp lệ: " + ex.getMessage(), ButtonType.OK);
                err.setHeaderText(null);
                err.showAndWait();
            }
        });
        
        dlg.setResultConverter(bt -> {
            if (bt == btnOK) {
                CaLam c = new CaLam();
                c.setMoTa(tfDesc.getText().trim());
                c.setGioBatdau(java.sql.Time.valueOf(java.time.LocalTime.parse(tfStart.getText(), java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))));
                c.setGioKetthuc(java.sql.Time.valueOf(java.time.LocalTime.parse(tfEnd.getText(), java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))));
                c.setSoLuongToiDa(spMax.getValue());
                return c;
            }
            return null;
        });
        
        dlg.showAndWait().ifPresent(caLam -> {
            System.out.println("➕ [SHIFT ADD] Mô tả: " + caLam.getMoTa() + " | Giờ: " + caLam.getGioBatdau() + " - " + caLam.getGioKetthuc() + " | Max: " + caLam.getSoLuongToiDa());
            if (caLamDAO.insertCaLam(caLam)) {
                System.out.println("✅ [SHIFT ADD] Thêm thành công!");
                shiftCurrentPage = 1;
                refreshShifts();
                showInfo("Thêm ca làm thành công!");
            } else {
                System.out.println("❌ [SHIFT ADD] Thêm thất bại!");
                Alert err = new Alert(Alert.AlertType.ERROR, "Thêm ca làm thất bại!", ButtonType.OK);
                err.setHeaderText(null);
                err.showAndWait();
            }
        });
    }
    
    private void showEditShiftDialog(CaLam caLam) {
        Dialog<CaLam> dlg = new Dialog<>();
        dlg.setTitle("Sửa Ca Làm");
        dlg.setHeaderText("Cập nhật thông tin ca làm");
        
        ButtonType btnOK = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(btnOK, ButtonType.CANCEL);
        
        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new javafx.geometry.Insets(15));
        
        TextField tfDesc = new TextField(caLam.getMoTa());
        TextField tfStart = new TextField(String.valueOf(caLam.getGioBatdau()));
        TextField tfEnd = new TextField(String.valueOf(caLam.getGioKetthuc()));
        Spinner<Integer> spMax = new Spinner<>(1, 1000, caLam.getSoLuongToiDa());
        
        gp.addRow(0, new Label("Mô tả:"), tfDesc);
        gp.addRow(1, new Label("Giờ bắt đầu:"), tfStart);
        gp.addRow(2, new Label("Giờ kết thúc:"), tfEnd);
        gp.addRow(3, new Label("Số lượng tối đa:"), spMax);
        
        dlg.getDialogPane().setContent(gp);
        
        Node okBtn = dlg.getDialogPane().lookupButton(btnOK);
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            try {
                String desc = tfDesc.getText().trim();
                if (desc.isEmpty()) throw new IllegalArgumentException("Mô tả không được để trống");
                
                java.time.LocalTime start = java.time.LocalTime.parse(tfStart.getText(), java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                java.time.LocalTime end = java.time.LocalTime.parse(tfEnd.getText(), java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                
                if (!end.isAfter(start)) throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu");
                if (spMax.getValue() <= 0) throw new IllegalArgumentException("Số lượng phải > 0");
            } catch (Exception ex) {
                ev.consume();
                Alert err = new Alert(Alert.AlertType.ERROR, "Dữ liệu không hợp lệ: " + ex.getMessage(), ButtonType.OK);
                err.setHeaderText(null);
                err.showAndWait();
            }
        });
        
        dlg.setResultConverter(bt -> {
            if (bt == btnOK) {
                caLam.setMoTa(tfDesc.getText().trim());
                caLam.setGioBatdau(java.sql.Time.valueOf(java.time.LocalTime.parse(tfStart.getText(), java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))));
                caLam.setGioKetthuc(java.sql.Time.valueOf(java.time.LocalTime.parse(tfEnd.getText(), java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))));
                caLam.setSoLuongToiDa(spMax.getValue());
                return caLam;
            }
            return null;
        });
        
        dlg.showAndWait().ifPresent(updated -> {
            if (caLamDAO.updateCaLam(updated)) {
                refreshShifts();
                showInfo("Đập nhật ca làm thành công!");
            } else {
                Alert err = new Alert(Alert.AlertType.ERROR, "Đập nhật ca làm thất bại!", ButtonType.OK);
                err.setHeaderText(null);
                err.showAndWait();
            }
        });
    }
    
    // ============ USER MANAGEMENT METHODS ============
    
    private void setupUserTable() {
        if (userTable == null) return;
        userTable.setItems(users);
        if (colUserId != null) colUserId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getMaNguoidung()));
        if (colUserEmail != null) colUserEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));
        if (colUserName != null) colUserName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getHoTen()));
        if (colUserRole != null) colUserRole.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTenVaitro()));
        
        if (colUserActions != null) {
            colUserActions.setCellFactory(col -> new TableCell<User, Void>() {
                private final Button btnEdit = new Button("✏️");
                private final Button btnDelete = new Button("🗑️");
                private final HBox box = new HBox(8, btnEdit, btnDelete);
                {
                    btnEdit.getStyleClass().add("primary-btn");
                    btnDelete.getStyleClass().add("danger-btn");
                    btnEdit.setStyle("-fx-padding: 5px 15px;");
                    btnDelete.setStyle("-fx-padding: 5px 15px;");
                    btnEdit.setOnAction(e -> handleEditUser());
                    btnDelete.setOnAction(e -> handleDeleteUser());
                }
                
                private void handleEditUser() {
                    User item = getTableView().getItems().get(getIndex());
                    showEditUserDialog(item);
                }
                
                private void handleDeleteUser() {
                    User item = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                        "Xác nhận xoá người dùng '" + item.getHoTen() + "'?", ButtonType.OK, ButtonType.CANCEL);
                    confirm.setHeaderText(null);
                    confirm.showAndWait().ifPresent(bt -> {
                        if (bt == ButtonType.OK) {
                            if (userDAO.deleteUser(item.getMaNguoidung())) {
                                userCurrentPage = 1;
                                refreshUsers();
                                showInfo("Xoá người dùng thành công!");
                            } else {
                                Alert err = new Alert(Alert.AlertType.ERROR, "Xoá người dùng thất bại!", ButtonType.OK);
                                err.setHeaderText(null);
                                err.showAndWait();
                            }
                        }
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : box);
                }
            });
        }
    }
    
    @FXML private void handleAddUser() {
        showAddUserDialog();
    }
    
    @FXML private void handleSearchUser() {
        userSearchKeyword = tfSearchUser != null ? tfSearchUser.getText().trim() : "";
        userCurrentPage = 1;
        refreshUsers();
    }
    
    @FXML private void handleUserPrevPage() {
        if (userCurrentPage > 1) {
            userCurrentPage--;
            refreshUsers();
        }
    }
    
    @FXML private void handleUserNextPage() {
        int totalPages = (int) Math.ceil((double) userTotalRecords / userPageSize);
        if (userCurrentPage < totalPages) {
            userCurrentPage++;
            refreshUsers();
        }
    }
    
    private void refreshUsers() {
        int offset = (userCurrentPage - 1) * userPageSize;
        List<User> userList = userDAO.getUsersWithFilter(userSearchKeyword, userPageSize, offset);
        userTotalRecords = userDAO.countUsersWithFilter(userSearchKeyword);
        
        System.out.println("👥 [USER MANAGEMENT] Lấy danh sách người dùng - Trang: " + userCurrentPage + " | Tìm kiếm: " + (userSearchKeyword.isEmpty() ? "(không)" : userSearchKeyword) + " | Tổng: " + userTotalRecords);
        
        Platform.runLater(() -> {
            users.setAll(userList);
            updateUserPaginationControls();
            if (userTable != null) userTable.refresh();
        });
    }
    
    private void updateUserPaginationControls() {
        int totalPages = userTotalRecords > 0 ? (int) Math.ceil((double) userTotalRecords / userPageSize) : 1;
        
        if (lblTotalUsers != null) {
            lblTotalUsers.setText(String.valueOf(userTotalRecords));
        }
        
        if (lblUserPageInfo != null) {
            lblUserPageInfo.setText("Trang " + userCurrentPage + "/" + totalPages);
        }
        
        if (btnUserPrev != null) {
            btnUserPrev.setDisable(userCurrentPage <= 1);
        }
        
        if (btnUserNext != null) {
            btnUserNext.setDisable(userCurrentPage >= totalPages || userTotalRecords == 0);
        }
    }
    
    private void showAddUserDialog() {
        Dialog<User> dlg = new Dialog<>();
        dlg.setTitle("Thêm Người Dùng Mới");
        dlg.setHeaderText("Nhập thông tin người dùng");
        
        ButtonType btnOK = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(btnOK, ButtonType.CANCEL);
        
        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new javafx.geometry.Insets(15));
        
        TextField tfEmail = new TextField();
        tfEmail.setPromptText("Ví dụ: user@example.com");
        TextField tfPassword = new TextField();
        tfPassword.setPromptText("Ví dụ: 123456");
        TextField tfName = new TextField();
        tfName.setPromptText("Ví dụ: Nguyễn Văn A");
        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll("Nhân viên", "Quản lý");
        cbRole.setValue("Nhân viên");
        
        gp.addRow(0, new Label("Email:"), tfEmail);
        gp.addRow(1, new Label("Mật khẩu:"), tfPassword);
        gp.addRow(2, new Label("Họ tên:"), tfName);
        gp.addRow(3, new Label("Văn tỏ:"), cbRole);
        
        dlg.getDialogPane().setContent(gp);
        
        Node okBtn = dlg.getDialogPane().lookupButton(btnOK);
        okBtn.addEventFilter(ActionEvent.ACTION, ev -> {
            try {
                String email = tfEmail.getText().trim();
                if (email.isEmpty()) throw new IllegalArgumentException("Email không được để trống");
                if (!email.contains("@")) throw new IllegalArgumentException("Email không hợp lệ");
                
                String password = tfPassword.getText();
                if (password.isEmpty()) throw new IllegalArgumentException("Mật khẩu không được để trống");
                
                String name = tfName.getText().trim();
                if (name.isEmpty()) throw new IllegalArgumentException("Họ tên không được để trống");
            } catch (Exception ex) {
                ev.consume();
                Alert err = new Alert(Alert.AlertType.ERROR, "Dữ liệu không hợp lệ: " + ex.getMessage(), ButtonType.OK);
                err.setHeaderText(null);
                err.showAndWait();
            }
        });
        
        dlg.setResultConverter(bt -> {
            if (bt == btnOK) {
                User u = new User();
                u.setEmail(tfEmail.getText().trim());
                u.setMatKhau(tfPassword.getText());
                u.setHoTen(tfName.getText().trim());
                u.setMaVaitro(cbRole.getValue().equals("Quản lý") ? 1 : 2);
                return u;
            }
            return null;
        });
        
        dlg.showAndWait().ifPresent(user -> {
            System.out.println("➕ [USER ADD] Email: " + user.getEmail() + " | Tên: " + user.getHoTen() + " | Role: " + user.getTenVaitro());
            if (userDAO.insertUser(user)) {
                System.out.println("✅ [USER ADD] Thêm thành công!");
                userCurrentPage = 1;
                refreshUsers();
                showInfo("Thêm người dùng thành công!");
            } else {
                System.out.println("❌ [USER ADD] Thêm thất bại!");
                Alert err = new Alert(Alert.AlertType.ERROR, "Thêm người dùng thất bại!", ButtonType.OK);
                err.setHeaderText(null);
                err.showAndWait();
            }
        });
    }
    
    private void showEditUserDialog(User user) {
        Dialog<User> dlg = new Dialog<>();
        dlg.setTitle("Sửa Thông Tin Người Dùng");
        dlg.setHeaderText("Cập nhật thông tin");
        
        ButtonType btnOK = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(btnOK, ButtonType.CANCEL);
        
        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new javafx.geometry.Insets(15));
        
        TextField tfEmail = new TextField(user.getEmail());
        TextField tfName = new TextField(user.getHoTen());
        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll("Nhân viên", "Quản lý");
        cbRole.setValue(user.getTenVaitro());
        
        gp.addRow(0, new Label("Email:"), tfEmail);
        gp.addRow(1, new Label("Họ tên:"), tfName);
        gp.addRow(2, new Label("Văn tỏ:"), cbRole);
        
        dlg.getDialogPane().setContent(gp);
        
        Node okBtn = dlg.getDialogPane().lookupButton(btnOK);
        okBtn.addEventFilter(ActionEvent.ACTION, ev -> {
            try {
                String email = tfEmail.getText().trim();
                if (email.isEmpty()) throw new IllegalArgumentException("Email không được để trống");
                if (!email.contains("@")) throw new IllegalArgumentException("Email không hợp lệ");
                
                String name = tfName.getText().trim();
                if (name.isEmpty()) throw new IllegalArgumentException("Họ tên không được để trống");
            } catch (Exception ex) {
                ev.consume();
                Alert err = new Alert(Alert.AlertType.ERROR, "Dữ liệu không hợp lệ: " + ex.getMessage(), ButtonType.OK);
                err.setHeaderText(null);
                err.showAndWait();
            }
        });
        
        dlg.setResultConverter(bt -> {
            if (bt == btnOK) {
                user.setEmail(tfEmail.getText().trim());
                user.setHoTen(tfName.getText().trim());
                user.setMaVaitro(cbRole.getValue().equals("Quản lý") ? 1 : 2);
                user.setTenVaitro(cbRole.getValue());
                return user;
            }
            return null;
        });
        
        dlg.showAndWait().ifPresent(updated -> {
            System.out.println("✏️  [USER EDIT] ID: " + updated.getMaNguoidung() + " | Email: " + updated.getEmail() + " | Tên: " + updated.getHoTen());
            if (userDAO.updateUser(updated)) {
                System.out.println("✅ [USER EDIT] Cập nhật thành công!");
                refreshUsers();
                showInfo("Đập nhật người dùng thành công!");
            } else {
                System.out.println("❌ [USER EDIT] Cập nhật thất bại!");
                Alert err = new Alert(Alert.AlertType.ERROR, "Đập nhật người dùng thất bại!", ButtonType.OK);
                err.setHeaderText(null);
                err.showAndWait();
            }
        });
    }
    
    // ============ SCHEDULE MANAGEMENT METHODS ============
    
    private void setupScheduleTable() {
        if (scheduleTable == null) return;
        scheduleTable.setItems(schedules);
        if (colScheduleName != null) colScheduleName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTenNguoiDung()));
        if (colScheduleDate != null) colScheduleDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getNgayLam())));
        if (colScheduleShift != null) colScheduleShift.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMoTaCaLam()));
        if (colScheduleTime != null) colScheduleTime.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getGbdCagay() + " - " + c.getValue().getGktCagay()));
        if (colScheduleStatus != null) {
            colScheduleStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTrangthai().getValue()));
            colScheduleStatus.setCellFactory(col -> new TableCell<DangKy, String>() {
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
                        setTextFill(javafx.scene.paint.Color.web("#b45309"));
                    } else if (lower.contains("đã")) {
                        setTextFill(javafx.scene.paint.Color.web("#047857"));
                    } else if (lower.contains("từ chối")) {
                        setTextFill(javafx.scene.paint.Color.web("#b91c1c"));
                    } else {
                        setTextFill(javafx.scene.paint.Color.web("#111827"));
                    }
                }
            });
        }
        if (colScheduleActions != null) {
            colScheduleActions.setCellFactory(col -> new TableCell<DangKy, Void>() {
                private final Button btnEdit = new Button("✏️");
                private final Button btnDelete = new Button("🗑️");
                private final HBox box = new HBox(8, btnEdit, btnDelete);
                {
                    btnEdit.getStyleClass().add("primary-btn");
                    btnDelete.getStyleClass().add("danger-btn");
                    btnEdit.setStyle("-fx-padding: 5px 15px;");
                    btnDelete.setStyle("-fx-padding: 5px 15px;");
                    btnEdit.setOnAction(e -> handleEditSchedule());
                    btnDelete.setOnAction(e -> handleDeleteSchedule());
                }
                
                private void handleEditSchedule() {
                    DangKy item = getTableView().getItems().get(getIndex());
                    showEditScheduleDialog(item);
                }
                
                private void handleDeleteSchedule() {
                    DangKy item = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                        "Xác nhận xoá lịch trình của '" + item.getTenNguoiDung() + "'?", ButtonType.OK, ButtonType.CANCEL);
                    confirm.setHeaderText(null);
                    confirm.showAndWait().ifPresent(bt -> {
                        if (bt == ButtonType.OK) {
                            HuyDangKyRequest request = new HuyDangKyRequest(item.getMaDangky(), item.getMaNguoidung());
                            HuyDangKyResponse response = tcpClient.huyDangKy(request);
                            
                            if (response.isSuccess()) {
                                scheduleCurrentPage = 1;
                                refreshSchedules();
                                showInfo("Xoá lịch trình thành công!");
                            } else {
                                Alert err = new Alert(Alert.AlertType.ERROR, response.getMessage(), ButtonType.OK);
                                err.setHeaderText(null);
                                err.showAndWait();
                            }
                        }
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : box);
                }
            });
        }
    }
    
    @FXML private void handleSearchSchedule() {
        scheduleSearchKeyword = tfSearchSchedule != null ? tfSearchSchedule.getText().trim() : "";
        scheduleCurrentPage = 1;
        refreshSchedules();
    }
    
    @FXML private void handleSchedulePrevPage() {
        if (scheduleCurrentPage > 1) {
            scheduleCurrentPage--;
            refreshSchedules();
        }
    }
    
    @FXML private void handleScheduleNextPage() {
        int totalPages = (int) Math.ceil((double) scheduleTotalRecords / schedulePageSize);
        if (scheduleCurrentPage < totalPages) {
            scheduleCurrentPage++;
            refreshSchedules();
        }
    }
    
    private void refreshSchedules() {
        int offset = (scheduleCurrentPage - 1) * schedulePageSize;
        LocalDate fromDate = dpFromDate != null ? dpFromDate.getValue() : null;
        LocalDate toDate = dpToDate != null ? dpToDate.getValue() : null;
        
        List<DangKy> scheduleList = dangKyDAO.getScheduleWithFilter(scheduleSearchKeyword, fromDate, toDate, schedulePageSize, offset);
        scheduleTotalRecords = dangKyDAO.countScheduleWithFilter(scheduleSearchKeyword, fromDate, toDate);
        
        System.out.println("📅 [SCHEDULE MANAGEMENT] Lấy danh sách lịch - Trang: " + scheduleCurrentPage + " | Từ: " + fromDate + " Đến: " + toDate + " | Tìm kiếm: " + (scheduleSearchKeyword.isEmpty() ? "(không)" : scheduleSearchKeyword) + " | Tổng: " + scheduleTotalRecords);
        
        Platform.runLater(() -> {
            schedules.setAll(scheduleList);
            updateSchedulePaginationControls();
            if (scheduleTable != null) scheduleTable.refresh();
        });
    }
    
    private void updateSchedulePaginationControls() {
        int totalPages = scheduleTotalRecords > 0 ? (int) Math.ceil((double) scheduleTotalRecords / schedulePageSize) : 1;
        
        if (lblTotalSchedules != null) {
            lblTotalSchedules.setText(String.valueOf(scheduleTotalRecords));
        }
        
        if (lblSchedulePageInfo != null) {
            lblSchedulePageInfo.setText("Trang " + scheduleCurrentPage + "/" + totalPages);
        }
        
        if (btnSchedulePrev != null) {
            btnSchedulePrev.setDisable(scheduleCurrentPage <= 1);
        }
        
        if (btnScheduleNext != null) {
            btnScheduleNext.setDisable(scheduleCurrentPage >= totalPages || scheduleTotalRecords == 0);
        }
    }
    
    private void showEditScheduleDialog(DangKy schedule) {
        Dialog<DangKy> dlg = new Dialog<>();
        dlg.setTitle("Chỉnh Sửa Lịch Trình");
        dlg.setHeaderText("Cập nhật trạng thái đăng ký ca làm");
        
        ButtonType btnOK = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(btnOK, ButtonType.CANCEL);
        
        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new javafx.geometry.Insets(15));
        
        Label lblPerson = new Label(schedule.getTenNguoiDung());
        Label lblDate = new Label(String.valueOf(schedule.getNgayLam()));
        Label lblShift = new Label(schedule.getMoTaCaLam());
        Label lblTime = new Label(schedule.getGbdCagay() + " - " + schedule.getGktCagay());
        
        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("Chờ duyệt", "Đã duyệt", "Từ chối");
        cbStatus.setValue(schedule.getTrangthai().getValue());
        
        gp.addRow(0, new Label("Nhân Viên:"), lblPerson);
        gp.addRow(1, new Label("Ngày Làm:"), lblDate);
        gp.addRow(2, new Label("Ca Làm:"), lblShift);
        gp.addRow(3, new Label("Thời Gian:"), lblTime);
        gp.addRow(4, new Label("Trạng Thái:"), cbStatus);
        
        dlg.getDialogPane().setContent(gp);
        
        dlg.setResultConverter(bt -> {
            if (bt == btnOK) {
                String selectedStatus = cbStatus.getValue();
                if (selectedStatus.contains("Chờ")) {
                    schedule.setTrangthai(DangKy.TrangThai.CHO_DUYET);
                } else if (selectedStatus.contains("Đã")) {
                    schedule.setTrangthai(DangKy.TrangThai.DA_DUYET);
                } else if (selectedStatus.contains("Từ")) {
                    schedule.setTrangthai(DangKy.TrangThai.TU_CHOI);
                }
                return schedule;
            }
            return null;
        });
        
        dlg.showAndWait().ifPresent(updated -> {
            CapNhatTrangThaiRequest request = new CapNhatTrangThaiRequest(updated.getMaDangky(), updated.getTrangthai());
            CapNhatTrangThaiResponse response = tcpClient.capNhatTrangThai(request);
            
            if (response.isSuccess()) {
                System.out.println("✏️  [SCHEDULE EDIT] ID: " + updated.getMaDangky() + " | Trạng Thái: " + updated.getTrangthai().getValue());
                refreshSchedules();
                showInfo("Cập nhật trạng thái thành công!");
            } else {
                Alert err = new Alert(Alert.AlertType.ERROR, response.getMessage(), ButtonType.OK);
                err.setHeaderText(null);
                err.showAndWait();
            }
        });
    }
    
    // ============ MONTHLY SHIFT STATUS METHODS ============
    
    @FXML private DatePicker monthPickerAdmin;
    @FXML private Label monthYearLabelAdmin;
    @FXML private HBox headerRowAdmin;
    @FXML private HBox row1Admin;
    @FXML private HBox row2Admin;
    @FXML private HBox row3Admin;
    @FXML private HBox row4Admin;
    @FXML private VBox calendarContainerAdmin;
    
    private LocalDate selectedMonthAdmin = LocalDate.now();
    private final Map<String, HBox> shiftRowMapAdmin = new java.util.HashMap<>();
    private final CaLamDAO caLamDAOAdmin = new CaLamDAO();
    private final DangKyDAO dangKyDAOAdmin = new DangKyDAO();
    
    @FXML private void showMonthlyShiftStatus() {
        switchToContent(monthlyShiftStatusContent);
        updateSidebarActiveState(null);
        
        // Initialize calendar on first load
        if (monthPickerAdmin != null && monthPickerAdmin.getValue() == null) {
            monthPickerAdmin.setValue(LocalDate.now());
            monthPickerAdmin.setOnAction(e -> loadCalendarAdmin(monthPickerAdmin.getValue()));
        }
        
        shiftRowMapAdmin.put("Ca sáng", row1Admin);
        shiftRowMapAdmin.put("Ca trưa", row2Admin);
        shiftRowMapAdmin.put("Ca chiều", row3Admin);
        shiftRowMapAdmin.put("Ca tối", row4Admin);
        
        loadCalendarAdmin(LocalDate.now());
    }
    
    @FXML private void handleRefreshCalendar() {
        LocalDate selectedDate = (monthPickerAdmin != null && monthPickerAdmin.getValue() != null) 
            ? monthPickerAdmin.getValue() : LocalDate.now();
        loadCalendarAdmin(selectedDate);
    }
    
    private void loadCalendarAdmin(LocalDate monthDate) {
        selectedMonthAdmin = monthDate;
        java.time.YearMonth yearMonth = java.time.YearMonth.from(monthDate);
        int daysInMonth = yearMonth.lengthOfMonth();
        int year = yearMonth.getYear();
        int month = yearMonth.getMonthValue();
        
        System.out.println("📅 [CALENDAR] Loading calendar for month: " + month + "/" + year);
        
        if (monthYearLabelAdmin != null) {
            monthYearLabelAdmin.setText("Tháng " + month + ", " + year);
        }
        
        // Clear previous cells
        clearCalendarAdmin();
        
        // Get all shifts
        List<CaLam> shifts = caLamDAOAdmin.getAllCaLam();
        System.out.println("📅 [CALENDAR] Total shifts found: " + shifts.size());
        for (CaLam shift : shifts) {
            System.out.println("   - " + shift.getMoTa() + " (Max: " + shift.getSoLuongToiDa() + ")");
        }
        if (shifts.isEmpty()) {
            System.out.println("❌ [CALENDAR] No shifts found!");
            return;
        }
        
        // Get all registrations for this month
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        System.out.println("📅 [CALENDAR] Fetching registrations from " + firstDay + " to " + lastDay);
        List<DangKy> registrations = dangKyDAOAdmin.getScheduleWithFilter("", firstDay, lastDay, 1000, 0);
        System.out.println("📅 [CALENDAR] Total registrations found: " + registrations.size());
        for (DangKy reg : registrations) {
            System.out.println("   - Date: " + reg.getNgayLam() + ", Shift: " + reg.getLoaiCa() + ", Status: " + reg.getTrangthai());
        }
        
        // Build registration map: Map<day, Map<shift name, count>>
        java.util.Map<Integer, java.util.Map<String, Integer>> dayRegistrations = new java.util.HashMap<>();
        
        // Initialize all days with all shifts
        for (int day = 1; day <= daysInMonth; day++) {
            dayRegistrations.put(day, new java.util.HashMap<>());
            for (CaLam shift : shifts) {
                dayRegistrations.get(day).put(shift.getMoTa(), 0);
            }
        }
        
        // Count registrations per day per shift
        // IMPORTANT: Only count registrations with status "đã duyệt" (accepted)
        for (DangKy reg : registrations) {
            // Only count accepted registrations
            if (reg.getTrangthai() == DangKy.TrangThai.DA_DUYET) {
                int day = reg.getNgayLam().getDayOfMonth();
                String shiftName = reg.getLoaiCa(); // This is "Ca sáng", "Ca trưa", or "Ca gãy"
                
                // Debugging: Print shiftName and check if it exists in dayRegistrations
                System.out.println("   [DEBUG COUNT] Processing registration for Day: " + day + ", ShiftName: " + shiftName + ", Status: " + reg.getTrangthai());
                
                if (dayRegistrations.containsKey(day) && dayRegistrations.get(day).containsKey(shiftName)) {
                    dayRegistrations.get(day).put(shiftName, 
                        dayRegistrations.get(day).get(shiftName) + 1);
                    System.out.println("      [DEBUG COUNT] Incremented count for Day: " + day + ", ShiftName: " + shiftName);
                } else {
                    System.out.println("      [DEBUG COUNT] No matching entry in dayRegistrations for Day: " + day + ", ShiftName: " + shiftName + ". Available keys: " + dayRegistrations.get(day).keySet());
                    // This is where the problem likely lies: shiftName from registration doesn't match keys in dayRegistrations
                }
            }
        }
        
        // Log final registration counts
        System.out.println("📅 [CALENDAR] Final registration count per day:");
        for (int day = 1; day <= Math.min(5, daysInMonth); day++) {
            for (CaLam shift : shifts) {
                int count = dayRegistrations.get(day).getOrDefault(shift.getMoTa(), 0);
                System.out.println("   Day " + day + ", " + shift.getMoTa() + ": " + count + "/" + shift.getSoLuongToiDa());
            }
        }
        
        // Generate header
        generateHeaderAdmin(daysInMonth);
        
        // Generate rows
        for (CaLam shift : shifts) {
            HBox shiftRow = shiftRowMapAdmin.get(shift.getMoTa());
            if (shiftRow != null) {
                generateShiftRowAdmin(shiftRow, shift, daysInMonth, dayRegistrations, year, month);
            }
        }
        System.out.println("✅ [CALENDAR] Calendar loaded successfully!");
    }
    
    private void generateHeaderAdmin(int daysInMonth) {
        while (headerRowAdmin.getChildren().size() > 1) {
            headerRowAdmin.getChildren().remove(1);
        }
        for (int day = 1; day <= daysInMonth; day++) {
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setMinWidth(60);
            dayLabel.setMinHeight(40);
            dayLabel.setStyle("-fx-border-color: #333333; -fx-border-width: 1; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #e5e7eb;");
            dayLabel.setAlignment(javafx.geometry.Pos.CENTER);
            headerRowAdmin.getChildren().add(dayLabel);
        }
    }
    
    private void generateShiftRowAdmin(HBox row, CaLam shift, int daysInMonth, 
                                      java.util.Map<Integer, java.util.Map<String, Integer>> dayRegistrations,
                                      int year, int month) {
        while (row.getChildren().size() > 1) {
            row.getChildren().remove(1);
        }
        
        for (int day = 1; day <= daysInMonth; day++) {
            int registered = dayRegistrations.get(day).getOrDefault(shift.getMoTa(), 0);
            int capacity = shift.getSoLuongToiDa();
            
            // Determine color: RED if full, GREEN if has space (even if 0 registered)
            String bgColor;
            String status;
            if (registered >= capacity) {
                bgColor = "#ef4444"; // RED - Full
                status = "Đầy";
            } else {
                bgColor = "#10b981"; // GREEN - Has space
                status = "Còn chỗ";
            }
            
            Region cell = new Region();
            cell.setMinWidth(60);
            cell.setMinHeight(40);
            cell.setStyle("-fx-border-color: #333333; -fx-border-width: 1; -fx-background-color: " + bgColor + ";");
            
            javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(
                day + "/" + month + "\n" + shift.getMoTa() + "\n" + status + 
                "\nĐăng ký: " + registered + "/" + capacity);
            javafx.scene.control.Tooltip.install(cell, tooltip);
            
            row.getChildren().add(cell);
        }
    }
    
    private void clearCalendarAdmin() {
        while (headerRowAdmin.getChildren().size() > 1) {
            headerRowAdmin.getChildren().remove(1);
        }
        for (HBox row : shiftRowMapAdmin.values()) {
            while (row.getChildren().size() > 1) {
                row.getChildren().remove(1);
            }
        }
    }

    @Override
    public void onNewRegistration(NewRegistrationNotification notification) {
        System.out.println("📑 Admin nhận được notification: " + notification.getMessage());
        Platform.runLater(() -> {
            // Only add if the new registration is pending and not in the past
            DangKy newReg = notification.getNewRegistration();
            if (newReg.getTrangthai() == DangKy.TrangThai.CHO_DUYET && !newReg.getNgayLam().isBefore(LocalDate.now())) {
                showInfo("Có đăng ký mới: " + notification.getMessage());
                System.out.println("🔄 Đang refresh danh sách đăng ký do có thông báo mới...");
                currentPage = 1; // Reset to first page to show new registration
                refreshRegistrations(); // Re-fetch all pending registrations
            }
            refreshStats(); // Always refresh stats
        });
    }

    @Override
    public void onStatusChange(DangKy updatedDangKy) {
        // Admin dashboard không cần xử lý notification này trực tiếp trên UI
        // vì chính admin là người thực hiện thay đổi.
        // Tuy nhiên, nếu nhiều admin cùng làm việc, có thể refresh tại đây để đồng bộ.
        Platform.runLater(() -> {
            System.out.println("🔄 Nhận được thay đổi trạng thái, đang làm mới...");
            refreshRegistrations();
            refreshSchedules();
        });
    }
}
