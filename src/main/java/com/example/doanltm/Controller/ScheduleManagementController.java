package com.example.doanltm.Controller;

import com.example.doanltm.DAO.AdminReportDAO;
import com.example.doanltm.Model.DangKy;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ScheduleManagementController {

    @FXML private TextField searchField;
    @FXML private DatePicker dateFilterPicker;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private ComboBox<String> typeFilterCombo;

    @FXML private TableView<DangKy> table;
    @FXML private TableColumn<DangKy, String> colNguoiDung;
    @FXML private TableColumn<DangKy, LocalDate> colNgay;
    @FXML private TableColumn<DangKy, String> colLoaiCa;
    @FXML private TableColumn<DangKy, String> colMoTa;
    @FXML private TableColumn<DangKy, Time> colGioBD;
    @FXML private TableColumn<DangKy, Time> colGioKT;
    @FXML private TableColumn<DangKy, LocalDateTime> colThoiGianDangKy;
    @FXML private TableColumn<DangKy, String> colTrangThai;

    private final AdminReportDAO reportDAO = new AdminReportDAO();
    private final ObservableList<DangKy> data = FXCollections.observableArrayList();
    private FilteredList<DangKy> filtered;
    private SortedList<DangKy> sorted;

    @FXML
    public void initialize() {
        // Setup table columns
        if (colNguoiDung != null) colNguoiDung.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenNguoiDung()));
        if (colNgay != null) colNgay.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getNgayLam()));
        if (colLoaiCa != null) colLoaiCa.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLoaiCa()));
        if (colMoTa != null) colMoTa.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMoTaCaLam()));
        if (colGioBD != null) colGioBD.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getGbdCagay()));
        if (colGioKT != null) colGioKT.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getGktCagay()));
        if (colThoiGianDangKy != null) {
            colThoiGianDangKy.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getThoigianDangky()));
            colThoiGianDangKy.setCellFactory(column -> new TableCell<DangKy, LocalDateTime>() {
                private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                @Override protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : fmt.format(item));
                }
            });
        }
        if (colTrangThai != null) colTrangThai.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTrangthai().getValue()));

        filtered = new FilteredList<>(data, d -> true);
        sorted = new SortedList<>(filtered);
        if (table != null) {
            table.setItems(sorted);
            sorted.comparatorProperty().bind(table.comparatorProperty());
        }

        // Setup filter combos
        if (statusFilterCombo != null) {
            statusFilterCombo.getItems().addAll("Tất cả", "chờ duyệt", "đã duyệt", "từ chối");
            statusFilterCombo.setValue("Tất cả");
        }
        if (typeFilterCombo != null) {
            typeFilterCombo.getItems().addAll("Tất cả", "Ca bình thường", "Ca gãy");
            typeFilterCombo.setValue("Tất cả");
        }

        loadData();
    }

    @FXML private void handleRefresh() { loadData(); }
    @FXML private void handleApplyFilters() { applyFilters(); }
    @FXML private void handleClearFilters() {
        if (searchField != null) searchField.clear();
        if (dateFilterPicker != null) dateFilterPicker.setValue(null);
        if (statusFilterCombo != null) statusFilterCombo.setValue("Tất cả");
        if (typeFilterCombo != null) typeFilterCombo.setValue("Tất cả");
        applyFilters();
    }

    private void loadData() {
        // Lấy toàn bộ đăng ký (không lọc) từ DAO báo cáo quản trị
        List<DangKy> list = reportDAO.getRegistrations(null, null);
        // Sắp xếp: ngày gần nhất trước, sau đó giờ bắt đầu tăng dần
        list.sort(java.util.Comparator
                .comparing(DangKy::getNgayLam)
                .thenComparing(dk -> dk.getGbdCagay(), java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())));
        Platform.runLater(() -> {
            data.setAll(list);
            applyFilters();
            if (table != null) table.refresh();
        });
    }

    private void applyFilters() {
        final String q = searchField != null ? searchField.getText().toLowerCase().trim() : "";
        final LocalDate date = dateFilterPicker != null ? dateFilterPicker.getValue() : null;
        final String st = statusFilterCombo != null && statusFilterCombo.getValue() != null ? statusFilterCombo.getValue() : "Tất cả";
        final String type = typeFilterCombo != null && typeFilterCombo.getValue() != null ? typeFilterCombo.getValue() : "Tất cả";

        if (filtered == null) return;
        filtered.setPredicate(d -> {
            if (d == null) return false;
            // Search by user name, ca description
            if (!q.isEmpty()) {
                String text = ((d.getTenNguoiDung()==null?"":d.getTenNguoiDung()) + " " +
                               (d.getMoTaCaLam()==null?"":d.getMoTaCaLam()) + " " +
                               d.getLoaiCa()).toLowerCase();
                if (!text.contains(q)) return false;
            }
            // Date filter
            if (date != null && (d.getNgayLam() == null || !d.getNgayLam().equals(date))) return false;
            // Status filter
            if (!"Tất cả".equalsIgnoreCase(st)) {
                if (d.getTrangthai() == null || !d.getTrangthai().getValue().equalsIgnoreCase(st)) return false;
            }
            // Type filter
            if (!"Tất cả".equalsIgnoreCase(type)) {
                if (!type.equalsIgnoreCase(d.getLoaiCa())) return false;
            }
            return true;
        });
    }

    @FXML
    private void handleReloadPage() {
        try {
            Stage stage = null;
            if (table != null && table.getScene() != null) {
                stage = (Stage) table.getScene().getWindow();
            } else if (searchField != null && searchField.getScene() != null) {
                stage = (Stage) searchField.getScene().getWindow();
            }
            if (stage == null) return;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/doanltm/view/schedule-management.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Quản lí lịch làm việc");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCloseWindow() {
        try {
            Stage stage = null;
            if (table != null && table.getScene() != null) {
                stage = (Stage) table.getScene().getWindow();
            } else if (searchField != null && searchField.getScene() != null) {
                stage = (Stage) searchField.getScene().getWindow();
            }
            if (stage != null) stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
