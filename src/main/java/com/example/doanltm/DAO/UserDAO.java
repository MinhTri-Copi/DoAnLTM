package com.example.doanltm.DAO;

import com.example.doanltm.Database.BDConnection;
import com.example.doanltm.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    
    /**
     * Xác thực người dùng với email và mật khẩu
     */
    public User authenticate(String email, String matKhau) {
        String sql = "SELECT n.ma_nguoidung, n.email, n.ho_ten, n.ma_vaitro, v.ten_vaitro " +
                     "FROM nguoidung n " +
                     "INNER JOIN vaitro v ON n.ma_vaitro = v.ma_vaitro " +
                     "WHERE n.email = ? AND n.mat_khau = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = BDConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, matKhau); // TODO: Nên mã hóa mật khẩu (MD5, SHA-256, BCrypt)
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setMaNguoidung(rs.getInt("ma_nguoidung"));
                user.setEmail(rs.getString("email"));
                user.setHoTen(rs.getString("ho_ten"));
                user.setMaVaitro(rs.getInt("ma_vaitro"));
                user.setTenVaitro(rs.getString("ten_vaitro"));
                
                System.out.println("✅ Tìm thấy user: " + user);
                return user;
            } else {
                System.out.println("❌ Không tìm thấy user với email: " + email);
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi xác thực user: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                // Không đóng connection ở đây để tái sử dụng
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Kiểm tra email đã tồn tại chưa
     */
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM nguoidung WHERE email = ?";
        
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi kiểm tra email: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Lấy thông tin user theo ID
     */
    public User getUserById(int maNguoidung) {
        String sql = "SELECT n.ma_nguoidung, n.email, n.ho_ten, n.ma_vaitro, v.ten_vaitro " +
                     "FROM nguoidung n " +
                     "INNER JOIN vaitro v ON n.ma_vaitro = v.ma_vaitro " +
                     "WHERE n.ma_nguoidung = ?";
        
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maNguoidung);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setMaNguoidung(rs.getInt("ma_nguoidung"));
                user.setEmail(rs.getString("email"));
                user.setHoTen(rs.getString("ho_ten"));
                user.setMaVaitro(rs.getInt("ma_vaitro"));
                user.setTenVaitro(rs.getString("ten_vaitro"));
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
