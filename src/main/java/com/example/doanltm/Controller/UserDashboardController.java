package com.example.doanltm.Controller;

import com.example.doanltm.Model.*;
import com.example.doanltm.Request.*;
import com.example.doanltm.Response.DangKyResponse;
import com.example.doanltm.Response.GetCaLamResponse;
import com.example.doanltm.Response.GetDangKyResponse;
import com.example.doanltm.Response.HuyDangKyResponse;
import com.example.doanltm.Service.NotificationListener;
import com.example.doanltm.Service.TCPClientService;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.Optional;

public class UserDashboardController implements NotificationListener {

    // User info
    private User currentUser;

    // TCP Client Service
    private TCPClientService tcpClientService;

    // ... rest of FXML fields ...
    @FXML private Label userNameLabel;
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
    @FXML private TableView<DangKy> dangKyTableView;
    @FXML private TableColumn<DangKy, LocalDate> colNgayLam;
    @FXML private TableColumn<DangKy, String> colLoaiCa;
    @FXML private TableColumn<DangKy, String> colCaLam;
    @FXML private TableColumn<DangKy, Time> colGioBatDau;
    @FXML private TableColumn<DangKy, Time> colGioKetThuc;
    @FXML private TableColumn<DangKy, LocalDateTime> colThoiGianDangKy;
    @FXML private TableColumn<DangKy, String> colTrangThai;
    @FXML private TableColumn<DangKy, Void> colAction;

    private final ObservableList<DangKy> dangKyList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (ngayLamCaBinhThuongPicker != null) {
            ngayLamCaBinhThuongPicker.setValue(LocalDate.now());
        }
        if (ngayLamCaGayPicker != null) {
            ngayLamCaGayPicker.setValue(LocalDate.now());
        }

        initializeTimeSpinners();
        setupCaLamComboBox();
        setupTableView();
    }

    public void setTCPClientService(TCPClientService service) {
        this.tcpClientService = service;
        // Đăng ký listener để nhận thông báo
        if (this.tcpClientService != null) {
            this.tcpClientService.setNotificationListener(this);
        }
        System.out.println("✅ TCPClientService đã được set cho UserDashboardController");
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (userNameLabel != null) {
            userNameLabel.setText(user.getHoTen());
        }

        if (tcpClientService != null) {
            loadCaLamList();
            loadDangKyList();
        } else {
            System.err.println("⚠️ TCPClientService chưa được khởi tạo!");
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
        // User dashboard không cần xử lý thông báo này
    }

    private void initializeTimeSpinners() {
        if (gioBatDauSpinner != null) {
            SpinnerValueFactory<Integer> gioFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 8);
            gioBatDauSpinner.setValueFactory(gioFactory1);
            gioBatDauSpinner.setEditable(true);
            gioBatDauSpinner.getEditor().setStyle("-fx-text-fill: #2c3e50 !important; -fx-background-color: white !important; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center; -fx-opacity: 1.0 !important;");
            Platform.runLater(() -> {
                gioBatDauSpinner.getEditor().setText("8");
                gioBatDauSpinner.commitValue();
            });
        }

        if (gioKetThucSpinner != null) {
            SpinnerValueFactory<Integer> gioFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 17);
            gioKetThucSpinner.setValueFactory(gioFactory2);
            gioKetThucSpinner.setEditable(true);
            gioKetThucSpinner.getEditor().setStyle("-fx-text-fill: #2c3e50 !important; -fx-background-color: white !important; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center; -fx-opacity: 1.0 !important;");
            Platform.runLater(() -> {
                gioKetThucSpinner.getEditor().setText("17");
                gioKetThucSpinner.commitValue();
            });
        }

        if (phutBatDauSpinner != null) {
            SpinnerValueFactory<Integer> phutFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
            phutBatDauSpinner.setValueFactory(phutFactory1);
            phutBatDauSpinner.setEditable(true);
            phutBatDauSpinner.getEditor().setStyle("-fx-text-fill: #2c3e50 !important; -fx-background-color: white !important; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center; -fx-opacity: 1.0 !important;");
            Platform.runLater(() -> {
                phutBatDauSpinner.getEditor().setText("0");
                phutBatDauSpinner.commitValue();
            });
        }

        if (phutKetThucSpinner != null) {
            SpinnerValueFactory<Integer> phutFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
            phutKetThucSpinner.setValueFactory(phutFactory2);
            phutKetThucSpinner.setEditable(true);
            phutKetThucSpinner.getEditor().setStyle("-fx-text-fill: #2c3e50 !important; -fx-background-color: white !important; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center; -fx-opacity: 1.0 !important;");
            Platform.runLater(() -> {
                phutKetThucSpinner.getEditor().setText("0");
                phutKetThucSpinner.commitValue();
            });
        }
    }

    private void loadCaLamList() {
        if (tcpClientService == null) {
            System.err.println("❌ TCPClientService is null!");
            return;
        }

        new Thread(() -> {
            LocalDate ngayLam = LocalDate.now();
            if (ngayLamCaBinhThuongPicker != null && ngayLamCaBinhThuongPicker.getValue() != null) {
                ngayLam = ngayLamCaBinhThuongPicker.getValue();
            }

            GetCaLamRequest request = new GetCaLamRequest(ngayLam);
            GetCaLamResponse response = tcpClientService.getCaLam(request);

            Platform.runLater(() -> {
                if (response != null && response.isSuccess() && response.getCaLamList() != null) {
                    if (caLamComboBox != null) {
                        caLamComboBox.getItems().clear();
                        caLamComboBox.getItems().addAll(response.getCaLamList());
                    }
                } else {
                    System.err.println("❌ Không thể tải danh sách ca làm");
                }
            });
        }).start();
    }

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
                int conLai = newVal.getSoLuongToiDa() - newVal.getSoLuongDaDangKy();
                String info = String.format("Còn %d/%d slot", conLai, newVal.getSoLuongToiDa());
                caLamInfoLabel.setText(info);

                if (newVal.isFullSlot()) {
                    caLamInfoLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                } else if (conLai <= 2) {
                    caLamInfoLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                } else {
                    caLamInfoLabel.setStyle("-fx-text-fill: green;");
                }
            }
        });

        if (ngayLamCaBinhThuongPicker != null) {
            ngayLamCaBinhThuongPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    loadCaLamList();
                }
            });
        }
    }

    private void setupTableView() {
        if (dangKyTableView == null) return;

        dangKyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        if (colNgayLam != null) {
            colNgayLam.setCellValueFactory(cellData ->
                    new SimpleObjectProperty<>(cellData.getValue().getNgayLam())
            );
        }

        if (colLoaiCa != null) {
            colLoaiCa.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getLoaiCa())
            );
        }

        if (colCaLam != null) {
            colCaLam.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getMoTaCaLam())
            );
        }

        if (colGioBatDau != null) {
            colGioBatDau.setCellValueFactory(cellData ->
                    new SimpleObjectProperty<>(cellData.getValue().getGbdCagay())
            );
        }

        if (colGioKetThuc != null) {
            colGioKetThuc.setCellValueFactory(cellData ->
                    new SimpleObjectProperty<>(cellData.getValue().getGktCagay())
            );
        }

        if (colThoiGianDangKy != null) {
            colThoiGianDangKy.setCellValueFactory(cellData ->
                    new SimpleObjectProperty<>(cellData.getValue().getThoigianDangky())
            );
            colThoiGianDangKy.setCellFactory(column -> new TableCell<>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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

        if (colTrangThai != null) {
            colTrangThai.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getTrangthai().getValue())
            );
            colTrangThai.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        switch (item) {
                            case "chờ duyệt":
                                setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                                break;
                            case "đã duyệt":
                                setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                                break;
                            case "từ chối":
                                setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                                break;
                        }
                    }
                }
            });
        }

        if (colAction != null) {
            colAction.setCellFactory(param -> new TableCell<>() {
                private final Button huyButton = new Button("Hủy");

                {
                    huyButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
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
                        if (dangKy.getTrangthai() == DangKy.TrangThai.CHO_DUYET && !dangKy.getNgayLam().isBefore(LocalDate.now())) {
                            setGraphic(huyButton);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            });
        }

        dangKyTableView.setItems(dangKyList);
    }

    private void loadDangKyList() {
        if (currentUser == null || tcpClientService == null) return;

        new Thread(() -> {
            GetDangKyRequest request = new GetDangKyRequest(currentUser.getMaNguoidung());
            GetDangKyResponse response = tcpClientService.getDangKyByUser(request);

            Platform.runLater(() -> {
                dangKyList.clear();
                if (response != null && response.isSuccess() && response.getDangKyList() != null) {
                    dangKyList.addAll(response.getDangKyList());
                }
            });
        }).start();
    }

    @FXML
    private void handleDangKyCaBinhThuong() {
        if (ngayLamCaBinhThuongPicker == null || caLamComboBox == null || tcpClientService == null) return;

        LocalDate ngayLam = ngayLamCaBinhThuongPicker.getValue();
        CaLam caLam = caLamComboBox.getSelectionModel().getSelectedItem();

        if (ngayLam == null) {
            showMessage(messageCaBinhThuongLabel, "Vui lòng chọn ngày làm việc!", true);
            return;
        }

        if (ngayLam.isBefore(LocalDate.now())) {
            showMessage(messageCaBinhThuongLabel, "Không thể đăng ký ca trong quá khứ!", true);
            return;
        }

        if (caLam == null) {
            showMessage(messageCaBinhThuongLabel, "Vui lòng chọn ca làm việc!", true);
            return;
        }

        if (caLam.isFullSlot()) {
            showMessage(messageCaBinhThuongLabel, "Ca này đã đủ người đăng ký!", true);
            return;
        }

        if (dangKyCaBinhThuongButton != null) {
            dangKyCaBinhThuongButton.setDisable(true);
        }

        new Thread(() -> {
            DangKy dangKy = new DangKy(currentUser.getMaNguoidung(), caLam.getMaCalam(), ngayLam);
            dangKy.setThoigianDangky(LocalDateTime.now());
            DangKyRequest request = new DangKyRequest(dangKy);
            DangKyResponse response = tcpClientService.dangKyCaLam(request);

            Platform.runLater(() -> {
                if (response != null && response.isSuccess()) {
                    showMessage(messageCaBinhThuongLabel, "✅ " + response.getMessage(), false);
                    loadDangKyList();
                    loadCaLamList();
                } else {
                    String msg = response != null ? response.getMessage() : "Đăng ký thất bại!";
                    showMessage(messageCaBinhThuongLabel, "❌ " + msg, true);
                }
                if (dangKyCaBinhThuongButton != null) {
                    dangKyCaBinhThuongButton.setDisable(false);
                }
            });
        }).start();
    }

    @FXML
    private void handleDangKyCaGay() {
        if (ngayLamCaGayPicker == null || tcpClientService == null) return;

        LocalDate ngayLam = ngayLamCaGayPicker.getValue();

        if (ngayLam == null) {
            showMessage(messageCaGayLabel, "Vui lòng chọn ngày làm việc!", true);
            return;
        }

        if (ngayLam.isBefore(LocalDate.now())) {
            showMessage(messageCaGayLabel, "Không thể đăng ký ca trong quá khứ!", true);
            return;
        }

        int gioBD = gioBatDauSpinner.getValue();
        int phutBD = phutBatDauSpinner.getValue();
        int gioKT = gioKetThucSpinner.getValue();
        int phutKT = phutKetThucSpinner.getValue();

        Time gioBatDau = Time.valueOf(String.format("%02d:%02d:00", gioBD, phutBD));
        Time gioKetThuc = Time.valueOf(String.format("%02d:%02d:00", gioKT, phutKT));

        if (gioKetThuc.before(gioBatDau) || gioKetThuc.equals(gioBatDau)) {
            showMessage(messageCaGayLabel, "Giờ kết thúc phải sau giờ bắt đầu!", true);
            return;
        }

        if (dangKyCaGayButton != null) {
            dangKyCaGayButton.setDisable(true);
        }

        new Thread(() -> {
            DangKy dangKy = new DangKy(currentUser.getMaNguoidung(), ngayLam, gioBatDau, gioKetThuc);
            dangKy.setThoigianDangky(LocalDateTime.now());
            DangKyRequest request = new DangKyRequest(dangKy);
            DangKyResponse response = tcpClientService.dangKyCaLam(request);

            Platform.runLater(() -> {
                if (response != null && response.isSuccess()) {
                    showMessage(messageCaGayLabel, "✅ " + response.getMessage(), false);
                    loadDangKyList();
                } else {
                    String msg = response != null ? response.getMessage() : "Đăng ký thất bại!";
                    showMessage(messageCaGayLabel, "❌ " + msg, true);
                }
                if (dangKyCaGayButton != null) {
                    dangKyCaGayButton.setDisable(false);
                }
            });
        }).start();
    }

    private void handleHuyDangKy(DangKy dangKy) {
        if (tcpClientService == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Hủy đăng ký ca làm");
        confirm.setContentText("Bạn có chắc muốn hủy ca làm này không?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new Thread(() -> {
                HuyDangKyRequest request = new HuyDangKyRequest(dangKy.getMaDangky(), currentUser.getMaNguoidung());
                HuyDangKyResponse response = tcpClientService.huyDangKy(request);

                Platform.runLater(() -> {
                    if (response != null && response.isSuccess()) {
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setTitle("Thành công");
                        info.setContentText(response.getMessage());
                        info.show();
                        loadDangKyList();
                        loadCaLamList();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Lỗi");
                        error.setContentText(response != null ? response.getMessage() : "Không thể hủy đăng ký!");
                        error.show();
                    }
                });
            }).start();
        }
    }

    @FXML
    private void handleRefresh() {
        loadDangKyList();
        loadCaLamList();
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Đăng xuất");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn đăng xuất?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (tcpClientService != null) {
                tcpClientService.disconnect();
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/doanltm/view/login-view.fxml"));
                Scene scene = new Scene(loader.load());

                Stage stage = (Stage) userNameLabel.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Đăng Nhập");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showMessage(Label label, String message, boolean isError) {
        if (label == null) return;

        label.setText(message);
        label.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        label.setVisible(true);

        new Thread(() -> {
            try {
                Thread.sleep(5000);
                Platform.runLater(() -> label.setVisible(false));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}