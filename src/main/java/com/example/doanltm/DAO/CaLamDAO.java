
package com.example.doanltm.DAO;

import com.example.doanltm.Database.BDConnection;
import com.example.doanltm.Model.CaLam;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CaLamDAO {

    /**
     * Lấy tất cả ca làm
     */
    public List<CaLam> getAllCaLam() {
        List<CaLam> list = new ArrayList<>();
        String sql = "SELECT * FROM calam ORDER BY gio_batdau";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                CaLam caLam = new CaLam();
                caLam.setMaCalam(rs.getInt("ma_calam"));
                caLam.setGioBatdau(rs.getTime("gio_batdau"));
                caLam.setGioKetthuc(rs.getTime("gio_ketthuc"));
                caLam.setMoTa(rs.getString("mo_ta"));
                caLam.setSoLuongToiDa(rs.getInt("so_luong_toi_da"));
                list.add(caLam);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy danh sách ca làm: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy ca làm theo ID
     */
    public CaLam getCaLamById(int maCalam) {
        String sql = "SELECT * FROM calam WHERE ma_calam = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maCalam);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                CaLam caLam = new CaLam();
                caLam.setMaCalam(rs.getInt("ma_calam"));
                caLam.setGioBatdau(rs.getTime("gio_batdau"));
                caLam.setGioKetthuc(rs.getTime("gio_ketthuc"));
                caLam.setMoTa(rs.getString("mo_ta"));
                caLam.setSoLuongToiDa(rs.getInt("so_luong_toi_da"));
                return caLam;
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy ca làm: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy số lượng đã đăng ký cho ca làm trong ngày cụ thể
     */
    public int getSoLuongDaDangKy(int maCalam, LocalDate ngayLam) {
        String sql = "SELECT COUNT(*) FROM dangkycalam " +
                "WHERE ma_calam = ? AND ngay_lam = ? AND trangthai != 'ừ chối'";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maCalam);
            pstmt.setDate(2, Date.valueOf(ngayLam));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi đếm số lượng đăng ký: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Lấy ca làm với thông tin số lượng đã đăng ký
     */
    public List<CaLam> getCaLamWithRegistrationCount(LocalDate ngayLam) {
        List<CaLam> list = getAllCaLam();

        for (CaLam caLam : list) {
            int soLuongDaDangKy = getSoLuongDaDangKy(caLam.getMaCalam(), ngayLam);
            caLam.setSoLuongDaDangKy(soLuongDaDangKy);
        }

        return list;
    }
}