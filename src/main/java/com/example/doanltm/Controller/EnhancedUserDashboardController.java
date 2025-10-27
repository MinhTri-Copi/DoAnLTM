package com.example.doanltm.Controller;

import com.example.doanltm.Model.*;
import com.example.doanltm.Request.DangKyRequest;
import com.example.doanltm.Request.GetCaLamRequest;
import com.example.doanltm.Request.GetDangKyRequest;
import com.example.doanltm.Request.HuyDangKyRequest;
import com.example.doanltm.Response.DangKyResponse;
import com.example.doanltm.Response.GetCaLamResponse;
import com.example.doanltm.Response.GetDangKyResponse;
import com.example.doanltm.Response.HuyDangKyResponse;
import com.example.doanltm.Service.TCPClientService;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.example.doanltm.Service.NotificationListener;
import com.example.doanltm.Request.NewRegistrationNotification;
import java.util.stream.Collectors;

public class EnhancedUserDashboardController implements NotificationListener {

    // User info
    private User currentUser;
    private TCPClientService tcpClientService;
    
    // Auto-refresh scheduler
    private ScheduledExecutorService scheduler;
    
    // FXML Controls
    @FXML private Label userNameLabel;
    
    // Registration Form Controls
    @FXML private DatePicker ngayLamCaBinhThuongPicker;
    @FXML private ComboBox<CaLam> caLamComboBox;
    @FXML private Label caLamInfoLabel;
    @FXML private Button dangKyCaBinhThuongButton;
    @FXML private Label messageCaBinhThuongLabel;
    
    @FXML private DatePicker ngayLamCaGayPicker;
    @FXML private Spinner<Integer> gioBatDauSpinner;
    @FXML private Spinner<Integer> phutBatDauSpinner;
    @FXML private Spinner<Integer> gioKetThucSpinner;
    @FXML private Spinner<Integer> phutKetThucSpinner;
    @FXML private Button dangKyCaGayButton;
    @FXML private Label messageCaGayLabel;
    
    // Search & Filter Controls
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterCombo;

    
    // Table Controls
    @FXML private TableView<DangKy> dangKyTableView;
    @FXML private TableColumn<DangKy, LocalDate> colNgayLam;

    @FXML private TableColumn<DangKy, String> colCaLam;
    @FXML private TableColumn<DangKy, Time> colGioBatDau;
    @FXML private TableColumn<DangKy, Time> colGioKetThuc;
    @FXML private TableColumn<DangKy, LocalDateTime> colThoiGianDangKy;
    @FXML private TableColumn<DangKy, String> colTrangThai;
    @FXML private TableColumn<DangKy, Void> colAction;

    // Data Lists
    private ObservableList<DangKy> dangKyList = FXCollections.observableArrayList();
    private FilteredList<DangKy> filteredList;
    private SortedList<DangKy> sortedList;

    @FXML
    public void initialize() {
        System.out.println("🎨 Initializing Enhanced Dashboard...");
        
        initializeDatePickers();
        initializeTimeSpinners();
        initializeSearchAndFilters();
        setupTableView();
        setupCaLamComboBox();
        startAutoRefresh();
    }

    /**
     * Initialize date pickers with restrictions
     */
    private void initializeDatePickers() {
        // Restrict date selection: minimum tomorrow, maximum 1 month
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate maxDate = today.plusMonths(1);
        
        if (ngayLamCaBinhThuongPicker != null) {
            ngayLamCaBinhThuongPicker.setValue(tomorrow);
            ngayLamCaBinhThuongPicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (date.isBefore(tomorrow) || date.isAfter(maxDate)) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            });
        }
        
        if (ngayLamCaGayPicker != null) {
            ngayLamCaGayPicker.setValue(tomorrow);
            ngayLamCaGayPicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (date.isBefore(tomorrow) || date.isAfter(maxDate)) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            });
        }
    }

    /**
     * Initialize time spinners
     */
    private void initializeTimeSpinners() {
        if (gioBatDauSpinner != null) {
            SpinnerValueFactory<Integer> hourStartFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 22, 8);
            gioBatDauSpinner.setValueFactory(hourStartFactory);
            gioBatDauSpinner.setEditable(true);
            gioBatDauSpinner.getEditor().setStyle("-fx-text-fill: #2c3e50 !important; -fx-font-weight: bold; -fx-alignment: center; -fx-opacity: 1.0 !important;");
            // Force initial display
            Platform.runLater(() -> {
                gioBatDauSpinner.getEditor().setText("8");
                gioBatDauSpinner.commitValue();
            });
        }
        
        if (gioKetThucSpinner != null) {
            SpinnerValueFactory<Integer> hourEndFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(7, 23, 17);
            gioKetThucSpinner.setValueFactory(hourEndFactory);
            gioKetThucSpinner.setEditable(true);
            gioKetThucSpinner.getEditor().setStyle("-fx-text-fill: #2c3e50 !important; -fx-font-weight: bold; -fx-alignment: center; -fx-opacity: 1.0 !important;");
            // Force initial display
            Platform.runLater(() -> {
                gioKetThucSpinner.getEditor().setText("17");
                gioKetThucSpinner.commitValue();
            });
        }
        
        if (phutBatDauSpinner != null) {
            SpinnerValueFactory<Integer> minuteStartFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 15);
            phutBatDauSpinner.setValueFactory(minuteStartFactory);
            phutBatDauSpinner.setEditable(true);
            phutBatDauSpinner.getEditor().setStyle("-fx-text-fill: #2c3e50 !important; -fx-font-weight: bold; -fx-alignment: center; -fx-opacity: 1.0 !important;");
            // Force initial display
            Platform.runLater(() -> {
                phutBatDauSpinner.getEditor().setText("0");
                phutBatDauSpinner.commitValue();
            });
        }
        
        if (phutKetThucSpinner != null) {
            SpinnerValueFactory<Integer> minuteEndFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 15);
            phutKetThucSpinner.setValueFactory(minuteEndFactory);
            phutKetThucSpinner.setEditable(true);
            phutKetThucSpinner.getEditor().setStyle("-fx-text-fill: #2c3e50 !important; -fx-font-weight: bold; -fx-alignment: center; -fx-opacity: 1.0 !important;");
            // Force initial display
            Platform.runLater(() -> {
                phutKetThucSpinner.getEditor().setText("0");
                phutKetThucSpinner.commitValue();
            });
        }
    }

    /**
     * Initialize search and filter functionality
     */
    private void initializeSearchAndFilters() {
        // Setup filter lists
        filteredList = new FilteredList<>(dangKyList, p -> true);
        sortedList = new SortedList<>(filteredList);
        
        // Status filter setup
        if (statusFilterCombo != null) {
            statusFilterCombo.getItems().addAll("Tất cả", "Chờ duyệt", "Đã duyệt", "Từ chối");
            statusFilterCombo.setValue("Tất cả");
        }
        

    }

    /**
     * Enhanced table setup with sorting and styling
     */
    private void setupTableView() {
        if (dangKyTableView == null) return;
        
        // Bind sorted list to table
        dangKyTableView.setItems(sortedList);
        
        // Setup columns
        if (colNgayLam != null) {
            colNgayLam.setCellValueFactory(cellData -> 
                new SimpleObjectProperty<>(cellData.getValue().getNgayLam()));
            colNgayLam.setCellFactory(column -> new TableCell<DangKy, LocalDate>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                    }
                }
            });
        }
        
        if (colCaLam != null) {
            colCaLam.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getMoTaCaLam()));
        }
        
        if (colGioBatDau != null) {
            colGioBatDau.setCellValueFactory(cellData -> 
                new SimpleObjectProperty<>(cellData.getValue().getGbdCagay()));
        }
        
        if (colGioKetThuc != null) {
            colGioKetThuc.setCellValueFactory(cellData -> 
                new SimpleObjectProperty<>(cellData.getValue().getGktCagay()));
        }
        
        if (colThoiGianDangKy != null) {
            colThoiGianDangKy.setCellValueFactory(cellData -> 
                new SimpleObjectProperty<>(cellData.getValue().getThoigianDangky()));
            colThoiGianDangKy.setCellFactory(column -> new TableCell<DangKy, LocalDateTime>() {
                private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
                
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                    }
                }
            });
        }
        
        // Enhanced status column with CSS styling
        if (colTrangThai != null) {
            colTrangThai.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getTrangthai().getValue()));
            colTrangThai.setCellFactory(column -> new TableCell<DangKy, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        getStyleClass().removeAll("status-pending", "status-approved", "status-rejected");
                    } else {
                        setText(item);
                        getStyleClass().removeAll("status-pending", "status-approved", "status-rejected");
                        switch (item.toLowerCase()) {
                            case "chờ duyệt":
                                getStyleClass().add("status-pending");
                                break;
                            case "đã duyệt":
                                getStyleClass().add("status-approved");
                                break;
                            case "từ chối":
                                getStyleClass().add("status-rejected");
                                break;
                        }
                    }
                }
            });
        }
        
        // Enhanced action column
        if (colAction != null) {
            colAction.setCellFactory(param -> new TableCell<DangKy, Void>() {
                private final Button huyButton = new Button("🗑️ Hủy");
                
                {
                    huyButton.getStyleClass().addAll("btn", "btn-danger");
                    huyButton.setOnAction(event -> {
                        DangKy dangKy = getTableView().getItems().get(getIndex());
                        handleHuyDangKy(dangKy);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        DangKy dangKy = getTableView().getItems().get(getIndex());
                        // Chỉ hiển thị nút hủy cho các ca đang chờ duyệt và chưa qua ngày
                        if (dangKy.getTrangthai() == DangKy.TrangThai.CHO_DUYET && !dangKy.getNgayLam().isBefore(LocalDate.now())) {
                            setGraphic(huyButton);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            });
        }
        
        // Sort by status (pending first) and then by date
        sortedList.comparatorProperty().bind(dangKyTableView.comparatorProperty());
        dangKyTableView.getSortOrder().add(colTrangThai);
        dangKyTableView.getSortOrder().add(colNgayLam);
    }

    /**
     * Setup Ca Lam ComboBox with enhanced features
     */
    private void setupCaLamComboBox() {
        if (caLamComboBox == null) return;
        
        caLamComboBox.setConverter(new StringConverter<CaLam>() {
            @Override
            public String toString(CaLam caLam) {
                if (caLam == null) return "";
                return caLam.getMoTa() + " (" + caLam.getThoiGian() + ")";
            }
            
            @Override
            public CaLam fromString(String string) {
                return null;
            }
        });
        
        caLamComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && caLamInfoLabel != null) {
                caLamInfoLabel.setText("");
                caLamInfoLabel.getStyleClass().removeAll("text-success", "text-warning", "text-danger");
            }
        });
        
        // Auto-reload ca lam when date changes
        if (ngayLamCaBinhThuongPicker != null) {
            ngayLamCaBinhThuongPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    loadCaLamList();
                }
            });
        }
    }

    /**
     * Start auto-refresh scheduler
     */
    private void startAutoRefresh() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            if (currentUser != null && tcpClientService != null) {
                Platform.runLater(() -> loadDangKyList());
            }
        }, 30, 30, TimeUnit.SECONDS); // Refresh every 30 seconds
    }

    // =================== PUBLIC METHODS ===================

    public void setTCPClientService(TCPClientService service) {
        this.tcpClientService = service;
        if (this.tcpClientService != null) {
            this.tcpClientService.setNotificationListener(this);
        }
        System.out.println("✅ TCPClientService set for Enhanced Dashboard");
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (userNameLabel != null) {
            userNameLabel.setText("👋 " + user.getHoTen());
        }
        
        if (tcpClientService != null) {
            loadInitialData();
        }
    }

    // =================== EVENT HANDLERS ===================

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        applyFilters();
    }

    @FXML
    private void handleFilter() {
        applyFilters();
    }

    @FXML 
    private void handleManualRefresh() {
        loadDangKyList();
        loadCaLamList();
        showMessage(null, "🔄 Dữ liệu đã được cập nhật!", false);
    }

    @FXML
    private void handlePersonalStats() {
        showInfoDialog("📊 Thống kê cá nhân", "Tính năng đang được phát triển...");
    }

    @FXML
    private void handleSettings() {
        showInfoDialog("⚙️ Cài đặt", "Tính năng đang được phát triển...");
    }

    @FXML
    private void handleChangePassword() {
        showInfoDialog("🔑 Đổi mật khẩu", "Tính năng đang được phát triển...");
    }

    @FXML
    private void handleHelp() {
        showInfoDialog("📝 Hướng dẫn", 
            "🔹 Chọn ngày làm việc (từ ngày mai đến 1 tháng)\n" +
            "🔹 Đăng ký ca bình thường: chọn ca có sẵn\n" +
            "🔹 Đăng ký ca gãy: tự chọn giờ làm\n" +
            "🔹 Sử dụng tìm kiếm và bộ lọc để quản lý lịch\n" +
            "🔹 Hủy đăng ký khi ở trạng thái 'Chờ duyệt'"
        );
    }

    // =================== REGISTRATION HANDLERS ===================

    @FXML
    private void handleDangKyCaBinhThuong() {
        if (!validateCommonFields(ngayLamCaBinhThuongPicker, messageCaBinhThuongLabel)) return;
        
        CaLam selectedCa = caLamComboBox.getSelectionModel().getSelectedItem();
        if (selectedCa == null) {
            showMessage(messageCaBinhThuongLabel, "⚠️ Vui lòng chọn ca làm việc!", true);
            return;
        }
        
        if (selectedCa.isFullSlot()) {
            showMessage(messageCaBinhThuongLabel, "❌ Ca này đã đủ người đăng ký!", true);
            return;
        }
        
        disableButton(dangKyCaBinhThuongButton, true);
        
        Task<DangKyResponse> task = new Task<DangKyResponse>() {
            @Override
            protected DangKyResponse call() throws Exception {
                DangKy dangKy = new DangKy(currentUser.getMaNguoidung(), 
                    selectedCa.getMaCalam(), ngayLamCaBinhThuongPicker.getValue());
                return tcpClientService.dangKyCaLam(new DangKyRequest(dangKy));
            }
            
            @Override
            protected void succeeded() {
                DangKyResponse response = getValue();
                Platform.runLater(() -> {
                    if (response != null && response.isSuccess()) {
                        showMessage(messageCaBinhThuongLabel, "✅ " + response.getMessage(), false);
                        loadDangKyList();
                        loadCaLamList();
                    } else {
                        showMessage(messageCaBinhThuongLabel, "❌ " + 
                            (response != null ? response.getMessage() : "Đăng ký thất bại!"), true);
                    }
                    disableButton(dangKyCaBinhThuongButton, false);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showMessage(messageCaBinhThuongLabel, "❌ Lỗi kết nối!", true);
                    disableButton(dangKyCaBinhThuongButton, false);
                });
            }
        };
        
        new Thread(task).start();
    }

    @FXML
    private void handleDangKyCaGay() {
        if (!validateCommonFields(ngayLamCaGayPicker, messageCaGayLabel)) return;
        
        int gioBD = gioBatDauSpinner.getValue();
        int phutBD = phutBatDauSpinner.getValue(); 
        int gioKT = gioKetThucSpinner.getValue();
        int phutKT = phutKetThucSpinner.getValue();
        
        Time gioBatDau = Time.valueOf(String.format("%02d:%02d:00", gioBD, phutBD));
        Time gioKetThuc = Time.valueOf(String.format("%02d:%02d:00", gioKT, phutKT));
        
        if (gioKetThuc.before(gioBatDau) || gioKetThuc.equals(gioBatDau)) {
            showMessage(messageCaGayLabel, "⚠️ Giờ kết thúc phải sau giờ bắt đầu ít nhất 1 tiếng!", true);
            return;
        }
        
        // Check minimum duration (1 hour)
        long duration = gioKetThuc.getTime() - gioBatDau.getTime();
        if (duration < 3600000) { // 1 hour in milliseconds
            showMessage(messageCaGayLabel, "⚠️ Ca làm tối thiểu 1 tiếng!", true);
            return;
        }
        
        disableButton(dangKyCaGayButton, true);
        
        Task<DangKyResponse> task = new Task<DangKyResponse>() {
            @Override
            protected DangKyResponse call() throws Exception {
                DangKy dangKy = new DangKy(currentUser.getMaNguoidung(), 
                    ngayLamCaGayPicker.getValue(), gioBatDau, gioKetThuc);
                return tcpClientService.dangKyCaLam(new DangKyRequest(dangKy));
            }
            
            @Override
            protected void succeeded() {
                DangKyResponse response = getValue();
                Platform.runLater(() -> {
                    if (response != null && response.isSuccess()) {
                        showMessage(messageCaGayLabel, "✅ " + response.getMessage(), false);
                        loadDangKyList();
                    } else {
                        showMessage(messageCaGayLabel, "❌ " + 
                            (response != null ? response.getMessage() : "Đăng ký thất bại!"), true);
                    }
                    disableButton(dangKyCaGayButton, false);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showMessage(messageCaGayLabel, "❌ Lỗi kết nối!", true);
                    disableButton(dangKyCaGayButton, false);
                });
            }
        };
        
        new Thread(task).start();
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("🚪 Đăng xuất");
        confirm.setHeaderText("Xác nhận đăng xuất");
        confirm.setContentText("Bạn có chắc muốn đăng xuất khỏi hệ thống?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Stop auto-refresh
            if (scheduler != null) {
                scheduler.shutdown();
            }
            
            if (tcpClientService != null) {
                tcpClientService.disconnect();
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/doanltm/view/login-view.fxml"));
                Scene scene = new Scene(loader.load());

                Stage stage = (Stage) userNameLabel.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("🏢 Hệ Thống Quản Lý Lịch Làm Việc - Đăng Nhập");
                stage.centerOnScreen();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // =================== PRIVATE HELPER METHODS ===================

    private void loadInitialData() {
        loadCaLamList();
        loadDangKyList();
    }

    private void loadCaLamList() {
        if (tcpClientService == null) return;
        
        Task<GetCaLamResponse> task = new Task<GetCaLamResponse>() {
            @Override
            protected GetCaLamResponse call() throws Exception {
                LocalDate date = ngayLamCaBinhThuongPicker != null ? 
                    ngayLamCaBinhThuongPicker.getValue() : LocalDate.now().plusDays(1);
                return tcpClientService.getCaLam(new GetCaLamRequest(date));
            }
            
            @Override
            protected void succeeded() {
                GetCaLamResponse response = getValue();
                Platform.runLater(() -> {
                    if (response != null && response.isSuccess() && caLamComboBox != null) {
                        caLamComboBox.getItems().clear();
                        if (response.getCaLamList() != null) {
                            List<CaLam> availableCaLams = response.getCaLamList().stream()
                                .filter(caLam -> !caLam.isFullSlot())
                                .collect(Collectors.toList());
                            caLamComboBox.getItems().addAll(availableCaLams);
                        }
                    }
                });
            }
        };
        
        new Thread(task).start();
    }

    private void loadDangKyList() {
        if (tcpClientService == null || currentUser == null) return;
        
        Task<GetDangKyResponse> task = new Task<GetDangKyResponse>() {
            @Override
            protected GetDangKyResponse call() throws Exception {
                return tcpClientService.getDangKyByUser(new GetDangKyRequest(currentUser.getMaNguoidung()));
            }
            
            @Override
            protected void succeeded() {
                GetDangKyResponse response = getValue();
                Platform.runLater(() -> {
                    if (response != null && response.isSuccess()) {
                        dangKyList.clear();
                        if (response.getDangKyList() != null) {
                            // Sort: pending first, then by date descending
                            List<DangKy> sortedData = response.getDangKyList().stream()
                                .sorted(Comparator
                                    .comparing((DangKy d) -> d.getTrangthai() != DangKy.TrangThai.CHO_DUYET)
                                    .thenComparing(DangKy::getNgayLam, Comparator.reverseOrder()))
                                .collect(Collectors.toList());
                            dangKyList.addAll(sortedData);
                        }
                    }
                });
            }
        };
        
        new Thread(task).start();
    }

    private void applyFilters() {
        String searchText = searchField != null ? searchField.getText().toLowerCase().trim() : "";
        String statusFilter = statusFilterCombo != null ? statusFilterCombo.getValue() : "Tất cả";
        
        filteredList.setPredicate(dangKy -> {
            // Filter for cancellable registrations
            if (!(dangKy.getTrangthai() == DangKy.TrangThai.CHO_DUYET && !dangKy.getNgayLam().isBefore(LocalDate.now()))) {
                return false;
            }

            // Search filter
            if (!searchText.isEmpty()) {
                String searchableText = (dangKy.getMoTaCaLam() + " " + 
                                      dangKy.getNgayLam()).toLowerCase();
                if (!searchableText.contains(searchText)) {
                    return false;
                }
            }
            
            // Status filter
            if (!"Tất cả".equals(statusFilter)) {
                String currentStatus = dangKy.getTrangthai().getValue();
                if (!statusFilter.toLowerCase().contains(currentStatus.toLowerCase())) {
                    return false;
                }
            }
            

            
            return true;
        });
    }

    private void handleHuyDangKy(DangKy dangKy) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("🗑️ Hủy đăng ký");
        confirm.setHeaderText("Xác nhận hủy đăng ký");
        confirm.setContentText(String.format(
            "Bạn có chắc muốn hủy đăng ký:\n📅 %s - %s\n⏰ %s?",
            dangKy.getNgayLam(), dangKy.getLoaiCa(), dangKy.getMoTaCaLam()
        ));

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Task<HuyDangKyResponse> task = new Task<HuyDangKyResponse>() {
                @Override
                protected HuyDangKyResponse call() throws Exception {
                    return tcpClientService.huyDangKy(new HuyDangKyRequest(dangKy.getMaDangky(), currentUser.getMaNguoidung()));
                }
                
                @Override
                protected void succeeded() {
                    HuyDangKyResponse response = getValue();
                    Platform.runLater(() -> {
                        if (response != null && response.isSuccess()) {
                            showSuccessDialog("✅ Thành công", response.getMessage());
                            loadDangKyList();
                            loadCaLamList();
                        } else {
                            showErrorDialog("❌ Lỗi", response != null ? response.getMessage() : "Không thể hủy đăng ký!");
                        }
                    });
                }
                
                @Override
                protected void failed() {
                    Platform.runLater(() -> showErrorDialog("❌ Lỗi", "Lỗi kết nối server!"));
                }
            };
            
            new Thread(task).start();
        }
    }

    private boolean validateCommonFields(DatePicker datePicker, Label messageLabel) {
        if (datePicker == null || datePicker.getValue() == null) {
            showMessage(messageLabel, "⚠️ Vui lòng chọn ngày làm việc!", true);
            return false;
        }
        
        LocalDate selectedDate = datePicker.getValue();
        LocalDate today = LocalDate.now();
        
        if (selectedDate.isBefore(today.plusDays(1))) {
            showMessage(messageLabel, "⚠️ Chỉ có thể đăng ký từ ngày mai!", true);
            return false;
        }
        
        if (selectedDate.isAfter(today.plusMonths(1))) {
            showMessage(messageLabel, "⚠️ Chỉ có thể đăng ký trong vòng 1 tháng!", true);
            return false;
        }
        
        return true;
    }

    private void disableButton(Button button, boolean disable) {
        if (button != null) {
            button.setDisable(disable);
            if (disable) {
                button.setText("⏳ Đang xử lý...");
            } else {
                button.setText(button == dangKyCaBinhThuongButton ? "📋 Đăng Ký Ca Bình Thường" : "⚡ Đăng Ký Ca Gãy");
            }
        }
    }

    private void showMessage(Label messageLabel, String message, boolean isError) {
        if (messageLabel == null) return;
        
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("alert-success", "alert-danger", "alert-info");
        messageLabel.getStyleClass().add(isError ? "alert-danger" : "alert-success");
        messageLabel.setVisible(true);
        
        // Auto-hide after 5 seconds
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                Platform.runLater(() -> messageLabel.setVisible(false));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

        public void cleanup() {

            if (scheduler != null) {

                scheduler.shutdown();

            }

        }

    

        @Override

        public void onStatusChange(DangKy updatedDangKy) {

            Platform.runLater(() -> {

                for (int i = 0; i < dangKyList.size(); i++) {

                    if (dangKyList.get(i).getMaDangky() == updatedDangKy.getMaDangky()) {

                        dangKyList.set(i, updatedDangKy);

                        System.out.println("UI Updated for registration: " + updatedDangKy.getMaDangky());

                        break;

                    }

                }

            });

        }

    

        @Override

        public void onNewRegistration(NewRegistrationNotification notification) {

            // User dashboard doesn't need to handle this

        }

    }