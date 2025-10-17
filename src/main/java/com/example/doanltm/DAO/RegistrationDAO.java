package com.example.doanltm.DAO;

import com.example.doanltm.Database.BDConnection;
import com.example.doanltm.Model.User;

import java.sql.*;

public class RegistrationDAO {

    public User createUser(String hoTen, String email, String matKhau, int maVaitro) {
        String sql = "INSERT INTO nguoidung (email, mat_khau, ho_ten, ma_vaitro) VALUES (?, ?, ?, ?)";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, email);
            ps.setString(2, matKhau); // NOTE: cùng format với hệ thống hiện tại (chưa mã hóa)
            ps.setString(3, hoTen);
            ps.setInt(4, maVaitro);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        User u = new User();
                        u.setMaNguoidung(id);
                        u.setEmail(email);
                        u.setHoTen(hoTen);
                        u.setMaVaitro(maVaitro);
                        u.setTenVaitro(maVaitro == 1 ? "admin" : "nhanvien");
                        return u;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi tạo user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
