package com.example.doanltm.DAO;

import com.example.doanltm.Database.BDConnection;
import com.example.doanltm.Model.DangKy;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdminReportDAO {

    public int getMonthlyTotal(LocalDate anyDateInMonth) {
        String sql = "SELECT COUNT(*) FROM dangkycalam WHERE YEAR(ngay_lam)=? AND MONTH(ngay_lam)=? AND trangthai != 'từ chối'";
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, anyDateInMonth.getYear());
            ps.setInt(2, anyDateInMonth.getMonthValue());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public DayCount getHighestDay(LocalDate anyDateInMonth) {
        String sql = "SELECT ngay_lam, COUNT(*) cnt FROM dangkycalam " +
                "WHERE YEAR(ngay_lam)=? AND MONTH(ngay_lam)=? AND trangthai != 'từ chối' " +
                "GROUP BY ngay_lam ORDER BY cnt DESC LIMIT 1";
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, anyDateInMonth.getYear());
            ps.setInt(2, anyDateInMonth.getMonthValue());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DayCount(rs.getDate("ngay_lam").toLocalDate(), rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DayCount getLowestDay(LocalDate anyDateInMonth) {
        String sql = "SELECT ngay_lam, COUNT(*) cnt FROM dangkycalam " +
                "WHERE YEAR(ngay_lam)=? AND MONTH(ngay_lam)=? AND trangthai != 'từ chối' " +
                "GROUP BY ngay_lam ORDER BY cnt ASC LIMIT 1";
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, anyDateInMonth.getYear());
            ps.setInt(2, anyDateInMonth.getMonthValue());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DayCount(rs.getDate("ngay_lam").toLocalDate(), rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DangKy> getRegistrations(Integer maCalamFilter, LocalDate ngayFilter) {
        List<DangKy> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT d.*, c.mo_ta, c.gio_batdau, c.gio_ketthuc, n.ho_ten ")
           .append("FROM dangkycalam d ")
           .append("LEFT JOIN calam c ON d.ma_calam = c.ma_calam ")
           .append("INNER JOIN nguoidung n ON d.ma_nguoidung = n.ma_nguoidung ")
           .append("WHERE 1=1 ");
        if (maCalamFilter != null) sql.append("AND d.ma_calam = ? ");
        if (ngayFilter != null) sql.append("AND d.ngay_lam = ? ");
        sql.append("ORDER BY d.ngay_lam DESC, COALESCE(d.gbd_cagay, c.gio_batdau) ASC");

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (maCalamFilter != null) ps.setInt(idx++, maCalamFilter);
            if (ngayFilter != null) ps.setDate(idx++, Date.valueOf(ngayFilter));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DangKy d = new DangKy();
                    d.setMaDangky(rs.getInt("ma_dangky"));
                    d.setMaNguoidung(rs.getInt("ma_nguoidung"));
                    d.setTenNguoiDung(rs.getString("ho_ten"));
                    int maCalam = rs.getInt("ma_calam");
                    if (!rs.wasNull()) {
                        d.setMaCalam(maCalam);
                        d.setMoTaCaLam(rs.getString("mo_ta"));
                        d.setGbdCagay(rs.getTime("gio_batdau"));
                        d.setGktCagay(rs.getTime("gio_ketthuc"));
                    } else {
                        d.setMaCalam(null);
                        d.setMoTaCaLam("Ca gãy");
                        d.setGbdCagay(rs.getTime("gbd_cagay"));
                        d.setGktCagay(rs.getTime("gkt_cagay"));
                    }
                    d.setThoigianDangky(rs.getTimestamp("thoigian_dangky").toLocalDateTime());
                    d.setNgayLam(rs.getDate("ngay_lam").toLocalDate());
                    d.setTrangthai(DangKy.TrangThai.fromString(rs.getString("trangthai")));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static class DayCount {
        public final LocalDate date;
        public final int count;
        public DayCount(LocalDate date, int count) { this.date = date; this.count = count; }
    }
}
