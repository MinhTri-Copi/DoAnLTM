package com.example.doanltm.Controller;

import com.example.doanltm.DAO.UserDAO;
import com.example.doanltm.Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class UserManagementController {
    @FXML private TableView<User> table;
    @FXML private TableColumn<User, Number> colId;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colHoTen;
    @FXML private TableColumn<User, String> colVaiTro;
    @FXML private TextField searchField;

    private final UserDAO dao = new UserDAO();
    private final ObservableList<User> data = FXCollections.observableArrayList();
    private FilteredList<User> filtered;

    @FXML public void initialize(){
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getMaNguoidung()));
        colEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));
        colHoTen.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getHoTen()));
        colVaiTro.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTenVaitro()));
        filtered = new FilteredList<>(data, u -> true);
        SortedList<User> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);
        if (searchField != null) searchField.textProperty().addListener((o, ov, nv) -> applyFilter(nv));
        loadData();
    }

    private void loadData(){ data.setAll(dao.getAllUsers()); applyFilter(searchField==null?null:searchField.getText()); }

    @FXML private void handleRefresh(){ loadData(); }

    private void applyFilter(String q){
        final String s = q==null?"":q.trim().toLowerCase();
        if (filtered==null) return;
        filtered.setPredicate(u -> {
            if (s.isEmpty()) return true;
            String email = u.getEmail()==null?"":u.getEmail().toLowerCase();
            String ten = u.getHoTen()==null?"":u.getHoTen().toLowerCase();
            String role = u.getTenVaitro()==null?"":u.getTenVaitro().toLowerCase();
            String id = String.valueOf(u.getMaNguoidung());
            return email.contains(s) || ten.contains(s) || role.contains(s) || id.contains(s);
        });
    }

    @FXML private void handleAdd(){
        Dialog<User> d = buildUserDialog(null);
        d.showAndWait().ifPresent(u -> {
            try {
                if (dao.insertUser(u)) loadData(); else showError("Thêm người dùng thất bại (kiểm tra dữ liệu/DB)");
            } catch (Exception ex) { showError("Lỗi khi thêm người dùng: " + ex.getMessage()); }
        });
    }

    @FXML private void handleEdit(){
        User sel = table.getSelectionModel().getSelectedItem();
        if (sel==null){ showWarn("Chọn một người dùng"); return; }
        Dialog<User> d = buildUserDialog(sel);
        d.showAndWait().ifPresent(u -> { u.setMaNguoidung(sel.getMaNguoidung()); if (dao.updateUser(u)) loadData(); else showError("Cập nhật người dùng thất bại");});
    }

    @FXML private void handleDelete(){
        User sel = table.getSelectionModel().getSelectedItem();
        if (sel==null){ showWarn("Chọn một người dùng để xóa"); return; }
        Alert cf = new Alert(Alert.AlertType.CONFIRMATION, "Xóa người dùng '"+sel.getEmail()+"'?", ButtonType.OK, ButtonType.CANCEL); cf.setHeaderText(null);
        cf.showAndWait().ifPresent(bt -> { if (bt==ButtonType.OK) { if (dao.deleteUser(sel.getMaNguoidung())) loadData(); else showError("Xóa người dùng thất bại"); }});
    }

    private Dialog<User> buildUserDialog(User origin){
        Dialog<User> d = new Dialog<>(); d.setTitle(origin==null?"Thêm người dùng":"Sửa người dùng");
        ButtonType ok = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);
        GridPane gp = new GridPane(); gp.setHgap(10); gp.setVgap(10); gp.setPadding(new javafx.geometry.Insets(10));
        TextField tfEmail = new TextField(origin==null?"":origin.getEmail());
        PasswordField pf = new PasswordField(); pf.setPromptText(origin==null?"Mật khẩu":"(để trống nếu không đổi)");
        TextField tfHoTen = new TextField(origin==null?"":origin.getHoTen());
        ComboBox<String> cbVaiTro = new ComboBox<>(); cbVaiTro.getItems().addAll("admin","user");
        if (origin!=null) cbVaiTro.setValue(origin.getTenVaitro()); else cbVaiTro.setValue("user");
        gp.addRow(0, new Label("Email:"), tfEmail);
        gp.addRow(1, new Label("Mật khẩu:"), pf);
        gp.addRow(2, new Label("Họ tên:"), tfHoTen);
        gp.addRow(3, new Label("Vai trò:"), cbVaiTro);
        d.getDialogPane().setContent(gp);

        Node okBtn = d.getDialogPane().lookupButton(ok);
        okBtn.addEventFilter(ActionEvent.ACTION, ev -> {
            try {
                String email = tfEmail.getText().trim();
                String hoten = tfHoTen.getText().trim();
                if (email.isEmpty() || !email.contains("@")) throw new IllegalArgumentException("Email không hợp lệ");
                if (hoten.isEmpty()) throw new IllegalArgumentException("Họ tên không được để trống");
                if (origin == null && pf.getText().trim().isEmpty()) throw new IllegalArgumentException("Nhập mật khẩu cho tài khoản mới");
            } catch (Exception ex) {
                ev.consume();
                Alert a = new Alert(Alert.AlertType.ERROR, "Dữ liệu không hợp lệ: " + ex.getMessage(), ButtonType.OK);
                a.setHeaderText(null);
                a.showAndWait();
            }
        });

        d.setResultConverter(bt -> {
            if (bt==ok){
                User u = new User();
                u.setEmail(tfEmail.getText().trim());
                if (!pf.getText().isEmpty()) u.setMatKhau(pf.getText()); else u.setMatKhau("123456");
                u.setHoTen(tfHoTen.getText().trim());
                u.setMaVaitro("admin".equalsIgnoreCase(cbVaiTro.getValue())?1:2);
                u.setTenVaitro(cbVaiTro.getValue());
                return u;
            }
            return null;
        });
        return d;
    }

    private void showError(String s){ Alert a=new Alert(Alert.AlertType.ERROR,s,ButtonType.OK); a.setHeaderText(null); a.showAndWait(); }
    private void showWarn(String s){ Alert a=new Alert(Alert.AlertType.WARNING,s,ButtonType.OK); a.setHeaderText(null); a.showAndWait(); }
}
