
package com.example.doanltm.Controller;

import com.example.doanltm.Model.CaLam;
import com.example.doanltm.Model.DangKy;
import com.example.doanltm.Model.User;
import com.example.doanltm.Request.*;
import com.example.doanltm.Response.*;
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

public class UserDashboard1Controller {

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

    private ObservableList<DangKy> dangKyList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // KHÔNG khởi tạo TCPClientService ở đây
        // Sẽ được set từ LoginController

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

    /**
     * ✅ THÊM METHOD NÀY - Nhận TCPClientService từ LoginController
     */
    public void setTCPClientService(TCPClientService service) {
        this.tcpClientService = service;
        System.out.println("✅ TCPClientService đã được set cho UserDashboardController");
    }

    /**
     * Set user hiện tại
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (userNameLabel != null) {
            userNameLabel.setText(user.getHoTen());
        }

        // Chỉ load data khi đã có tcpClientService
        if (tcpClientService != null) {
            loadCaLamList();
            loadDangKyList();
            handleThongKeCaNhan();

        } else {
            System.err.println("⚠️ TCPClientService chưa được khởi tạo!");
        }
    }

    // ... rest of your code (giữ nguyên tất cả các method khác) ...

    private void initializeTimeSpinners() {
        if (gioBatDauSpinner != null) {
            SpinnerValueFactory<Integer> gioFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 8);
            gioBatDauSpinner.setValueFactory(gioFactory1);
            gioBatDauSpinner.setEditable(true);
            gioBatDauSpinner.getEditor().setStyle("-fx-text-fill: #2c3e50 !important; -fx-background-color: white !important; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center; -fx-opacity: 1.0 !important;");
            // Force display and commit value
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
            // Force display and commit value
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
            // Force display and commit value
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
            // Force display and commit value
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

        // Set column resize policy to distribute columns evenly
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
            colThoiGianDangKy.setCellFactory(column -> new TableCell<DangKy, LocalDateTime>() {
                private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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
            colTrangThai.setCellFactory(column -> new TableCell<DangKy, String>() {
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
            colAction.setCellFactory(param -> new TableCell<DangKy, Void>() {
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
                        if (dangKy.getTrangthai() == DangKy.TrangThai.CHO_DUYET) {
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



    private void showMessage(Label label, String message, boolean isError) {
        if (label == null) return;

        label.setText(message);
        label.setStyle(isError ? "-fx-text-fill: #fc1919;" : "-fx-text-fill: green;");
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


    @FXML private Label lblTongSoCa;
    @FXML private Label lblDaDuyet;
    @FXML private Label lblChoDuyet;
    @FXML private Label lblTuChoi;
    @FXML private Label lblTongGioLam;
    @FXML
    public void handleThongKeCaNhan() {
        try {
            //ThongKeCaNhanRequest req = new ThongKeCaNhanRequest(2);
            //ThongKeCaNhanResponse res = tcpClientService.getThongKeCaNhan(req);
            lblTongSoCa.setText("0");
            lblDaDuyet.setText("0");
            lblChoDuyet.setText("0");
            lblTuChoi.setText("0");
            lblTongGioLam.setText("0");
//            if (res != null && res.isSuccess()) {
//                lblTongSoCa.setText(String.valueOf(res.getTongSoCa()));
//                lblDaDuyet.setText(String.valueOf(res.getSoCaDaDuyet()));
//                lblChoDuyet.setText(String.valueOf(res.getSoCaChoDuyet()));
//                lblTuChoi.setText(String.valueOf(res.getSoCaTuChoi()));
//                lblTongGioLam.setText(String.valueOf(res.getTongGioLam()));
//            } else {
//                lblTongSoCa.setText("0");
//                lblDaDuyet.setText("0");
//                lblChoDuyet.setText("0");
//                lblTuChoi.setText("0");
//                lblTongGioLam.setText("0");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}