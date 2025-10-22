package com.example.doanltm.Controller;

import com.example.doanltm.DAO.CaLamDAO;
import com.example.doanltm.DAO.DangKyDAO;
import com.example.doanltm.Model.CaLam;
import com.example.doanltm.Model.ShiftStatusMonth;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

public class ShiftManagementController {
    @FXML private TableView<CaLam> table;
    @FXML private TableColumn<CaLam, Number> colId;
    @FXML private TableColumn<CaLam, String> colMoTa;
    @FXML private TableColumn<CaLam, String> colGioBD;
    @FXML private TableColumn<CaLam, String> colGioKT;
    @FXML private TableColumn<CaLam, Number> colMax;
    @FXML private TableColumn<CaLam, Number> colDaDK;
    @FXML private TextField searchField;
    
    // Bảng trạng thái ca làm theo tháng
    @FXML private TableView<ShiftStatusMonth> monthTable;
    @FXML private Button refreshMonthBtn;

    private final CaLamDAO dao = new CaLamDAO();
    private final DangKyDAO dangKyDAO = new DangKyDAO();
    private final ObservableList<CaLam> data = FXCollections.observableArrayList();
    private FilteredList<CaLam> filtered;
    private final ObservableList<ShiftStatusMonth> monthData = FXCollections.observableArrayList();
    private YearMonth currentMonth;

    @FXML
    public void initialize() {
        System.out.println("[INIT] ShiftManagementController.initialize() called");
        System.out.println("[INIT] table: " + table);
        System.out.println("[INIT] monthTable: " + monthTable);
        System.out.println("[INIT] refreshMonthBtn: " + refreshMonthBtn);
        
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getMaCalam()));
        colMoTa.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMoTa()));
        colGioBD.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getGioBatdau())));
        colGioKT.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getGioKetthuc())));
        colMax.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getSoLuongToiDa()));
        colDaDK.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getSoLuongDaDangKy()));
        filtered = new FilteredList<>(data, c -> true);
        SortedList<CaLam> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        if (searchField != null) {
            searchField.textProperty().addListener((obs, o, q) -> applyFilter(q));
        }
        loadData();
        
        // Khở tạo bảng tháng
        try {
            initializeMonthTable();
            loadMonthData();
        } catch (Exception ex) {
            System.err.println("❌ Lỗi khi khở tạo bảng tháng: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Khở tạo TableView cho bảng trạng thái ca làm theo tháng
     */
    private void initializeMonthTable() {
        System.out.println("[DEBUG] initializeMonthTable called");
        System.out.println("[DEBUG] monthTable: " + monthTable);
        System.out.println("[DEBUG] monthTable is null: " + (monthTable == null));
        
        if (monthTable == null) {
            System.err.println("❌ monthTable là null, không thể khở tạo");
            return;
        }
        
        System.out.println("[DEBUG] Xóa các cột cũ");
        // Xóa các cột cũ (nếu có)
        monthTable.getColumns().clear();
        
        currentMonth = YearMonth.now();
        System.out.println("[DEBUG] Current month: " + currentMonth + ", days: " + currentMonth.lengthOfMonth());
        
        monthTable.setItems(monthData);
        monthTable.setStyle("-fx-font-size: 10; -fx-fixed-cell-size: 35;");
        monthTable.getStyleClass().add("month-table");
        
        // Cột cá làm
        TableColumn<ShiftStatusMonth, String> colShift = new TableColumn<>("Ca làm");
        colShift.setPrefWidth(100);
        colShift.setMinWidth(100);
        colShift.setResizable(false);
        colShift.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTenCa()));
        monthTable.getColumns().add(colShift);
        
        // Tạo 31 cột cho 31 ngày
        int daysInMonth = currentMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            final int dayNum = day;
            TableColumn<ShiftStatusMonth, String> dayCol = new TableColumn<>(String.valueOf(day));
            dayCol.setPrefWidth(40);
            dayCol.setMinWidth(40);
            dayCol.setMaxWidth(40);
            dayCol.setResizable(false);
            
            dayCol.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getRegistrationCountForDay(dayNum) + "/" + 
                    cellData.getValue().getSoLuongToiDa()
                )
            );
            
            // Tựng d᫠ng cell factory để định dạng màu
            dayCol.setCellFactory(col -> new DayStatusCell(dayNum));
            
            monthTable.getColumns().add(dayCol);
        }
        
        if (refreshMonthBtn != null) {
            refreshMonthBtn.setOnAction(e -> loadMonthData());
        }
    }
    
    /**
     * Custom TableCell để hiển thị trạng thái ca (full/chưa full) với màu
     */
    private class DayStatusCell extends TableCell<ShiftStatusMonth, String> {
        private final int dayNum;
        
        public DayStatusCell(int dayNum) {
            this.dayNum = dayNum;
        }
        
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || getTableRow() == null) {
                setText(null);
                setStyle("");
            } else {
                ShiftStatusMonth data = getTableRow().getItem();
                if (data == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (data.isFullForDay(dayNum)) {
                        // Màu đỏ - full ca
                        setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; -fx-alignment: center; -fx-font-weight: bold;");
                    } else {
                        // Màu xanh - chưa full
                        setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-alignment: center; -fx-font-weight: bold;");
                    }
                }
            }
        }
    }
    
    /**
     * Tải dữ liệu bảng tháng
     */
    private void loadMonthData() {
        currentMonth = YearMonth.now();
        List<ShiftStatusMonth> data = dangKyDAO.getShiftStatusForMonth(currentMonth);
        System.out.println("[DEBUG] Loaded " + data.size() + " shifts for month " + currentMonth);
        monthData.setAll(data);
        System.out.println("[DEBUG] monthData size: " + monthData.size());
    }

    @FXML private void handleRefresh() { 
        loadData();
        loadMonthData(); // Also refresh month data
    }
    
    @FXML private void handleMonthRefresh() {
        System.out.println("[TEST] Handle month refresh clicked");
        System.out.println("[TEST] monthTable: " + monthTable);
        loadMonthData();
    }

    private void loadData() {
        data.setAll(dao.getCaLamWithRegistrationCount(LocalDate.now()));
        applyFilter(searchField == null ? null : searchField.getText());
    }

    private void applyFilter(String query) {
        final String q = query == null ? "" : query.trim().toLowerCase();
        if (filtered == null) return;
        filtered.setPredicate(c -> {
            if (q.isEmpty()) return true;
            String moTa = c.getMoTa() == null ? "" : c.getMoTa().toLowerCase();
            String bd = String.valueOf(c.getGioBatdau()).toLowerCase();
            String kt = String.valueOf(c.getGioKetthuc()).toLowerCase();
            String id = String.valueOf(c.getMaCalam());
            return moTa.contains(q) || bd.contains(q) || kt.contains(q) || id.contains(q);
        });
    }

    @FXML private void handleAdd() {
        Dialog<CaLam> dlg = buildShiftDialog(null);
        dlg.showAndWait().ifPresent(c -> {
            try {
                if (dao.insertCaLam(c)) loadData();
                else showError("Thêm ca làm thất bại (kiểm tra dữ liệu/DB)");
            } catch (Exception ex) {
                showError("Lỗi khi thêm ca: " + ex.getMessage());
            }
        });
    }

    @FXML private void handleEdit() {
        CaLam sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showWarn("Chọn một ca để sửa"); return; }
        Dialog<CaLam> dlg = buildShiftDialog(sel);
        dlg.showAndWait().ifPresent(c -> {
            c.setMaCalam(sel.getMaCalam());
            if (dao.updateCaLam(c)) loadData();
            else showError("Cập nhật ca làm thất bại");
        });
    }

    @FXML private void handleDelete() {
        CaLam sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showWarn("Chọn một ca để xóa"); return; }
        Alert cf = new Alert(Alert.AlertType.CONFIRMATION, "Xóa ca '"+sel.getMoTa()+"'?", ButtonType.OK, ButtonType.CANCEL);
        cf.setHeaderText(null);
        cf.showAndWait().ifPresent(bt -> { if (bt==ButtonType.OK) {
            if (dao.deleteCaLam(sel.getMaCalam())) loadData();
            else showError("Xóa ca làm thất bại");
        }});
    }

    private Dialog<CaLam> buildShiftDialog(CaLam origin) {
        Dialog<CaLam> d = new Dialog<>();
        d.setTitle(origin==null?"Thêm ca làm":"Sửa ca làm");
        ButtonType ok = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);
        GridPane gp = new GridPane(); gp.setHgap(10); gp.setVgap(10); gp.setPadding(new javafx.geometry.Insets(10));
        TextField tfMoTa = new TextField(origin==null?"":origin.getMoTa());
        TextField tfBD = new TextField(origin==null?"07:00:00":String.valueOf(origin.getGioBatdau()));
        TextField tfKT = new TextField(origin==null?"11:00:00":String.valueOf(origin.getGioKetthuc()));
        Spinner<Integer> spMax = new Spinner<>(1, 1000, origin==null?10:origin.getSoLuongToiDa());
        gp.addRow(0, new Label("Mô tả:"), tfMoTa);
        gp.addRow(1, new Label("Giờ bắt đầu (HH:mm:ss):"), tfBD);
        gp.addRow(2, new Label("Giờ kết thúc (HH:mm:ss):"), tfKT);
        gp.addRow(3, new Label("Số lượng tối đa:"), spMax);
        d.getDialogPane().setContent(gp);

        // Chặn đóng dialog nếu dữ liệu không hợp lệ
        Node okBtn = d.getDialogPane().lookupButton(ok);
        okBtn.addEventFilter(ActionEvent.ACTION, ev -> {
            try {
                String moTa = tfMoTa.getText().trim();
                if (moTa.isEmpty()) throw new IllegalArgumentException("Mô tả không được để trống");
                LocalTime bd = parseTimeFlexible(tfBD.getText());
                LocalTime kt = parseTimeFlexible(tfKT.getText());
                if (!kt.isAfter(bd)) throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu");
                if (spMax.getValue() == null || spMax.getValue() <= 0) throw new IllegalArgumentException("Số lượng tối đa phải > 0");
            } catch (Exception ex) {
                ev.consume();
                showError("Dữ liệu không hợp lệ: " + ex.getMessage());
            }
        });

        d.setResultConverter(bt -> {
            if (bt==ok) {
                CaLam c = new CaLam();
                c.setMoTa(tfMoTa.getText().trim());
                LocalTime bd = parseTimeFlexible(tfBD.getText());
                LocalTime kt = parseTimeFlexible(tfKT.getText());
                c.setGioBatdau(Time.valueOf(bd));
                c.setGioKetthuc(Time.valueOf(kt));
                c.setSoLuongToiDa(spMax.getValue());
                return c;
            }
            return null;
        });
        return d;
    }

    private LocalTime parseTimeFlexible(String input) {
        String s = input == null ? "" : input.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("Giờ không được trống (HH:mm hoặc HH:mm:ss)");
        java.util.List<java.time.format.DateTimeFormatter> fmts = java.util.Arrays.asList(
                java.time.format.DateTimeFormatter.ofPattern("H:mm"),
                java.time.format.DateTimeFormatter.ofPattern("HH:mm"),
                java.time.format.DateTimeFormatter.ofPattern("H:mm:ss"),
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
        );
        for (java.time.format.DateTimeFormatter f : fmts) {
            try {
                java.time.LocalTime t = java.time.LocalTime.parse(s, f);
                // Nếu thiếu giây, thêm 00
                return t.getSecond() == 0 && (s.length() == 4 || s.length() == 5) ? t.withSecond(0) : t;
            } catch (Exception ignore) { }
        }
        throw new IllegalArgumentException("Định dạng giờ không hợp lệ. VD: 6:00, 06:00, 6:00:00 hoặc 06:00:00");
    }

    private void showError(String s){ Alert a=new Alert(Alert.AlertType.ERROR,s,ButtonType.OK); a.setHeaderText(null); a.showAndWait(); }
    private void showWarn(String s){ Alert a=new Alert(Alert.AlertType.WARNING,s,ButtonType.OK); a.setHeaderText(null); a.showAndWait(); }
}
