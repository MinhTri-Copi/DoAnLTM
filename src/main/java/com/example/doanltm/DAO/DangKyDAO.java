package com.example.doanltm.DAO;

import com.example.doanltm.Database.BDConnection;
import com.example.doanltm.Model.DangKy;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DangKyDAO {
    
    /**
     * Đăng ký ca làm mới (cả ca bình thường và ca gãy)
     */
    public boolean dangKyCaLam(DangKy dangKy) {
        String sql;
        
        // ✅ Phân biệt SQL cho 2 loại ca
        if (dangKy.isCaGay()) {
            // Ca gãy: ma_calam NULL, gbd_cagay và gkt_cagay có giá trị
            sql = "INSERT INTO dangkycalam (ma_nguoidung, ma_calam, thoigian_dangky, ngay_lam, gbd_cagay, gkt_cagay, trangthai) " +
                  "VALUES (?, NULL, NOW(), ?, ?, ?, ?)";
        } else {
            // Ca bình thường: ma_calam có giá trị, gbd_cagay và gkt_cagay NULL
            sql = "INSERT INTO dangkycalam (ma_nguoidung, ma_calam, thoigian_dangky, ngay_lam, gbd_cagay, gkt_cagay, trangthai) " +
                  "VALUES (?, ?, NOW(), ?, NULL, NULL, ?)";
        }
        
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, dangKy.getMaNguoidung());
            
            if (dangKy.isCaGay()) {
                // ✅ CA GÃY
                pstmt.setDate(2, Date.valueOf(dangKy.getNgayLam()));
                pstmt.setTime(3, dangKy.getGbdCagay());
                pstmt.setTime(4, dangKy.getGktCagay());
                pstmt.setString(5, dangKy.getTrangthai().getValue());
            } else {
                // ✅ CA BÌNH THƯỜNG
                pstmt.setInt(2, dangKy.getMaCalam());
                pstmt.setDate(3, Date.valueOf(dangKy.getNgayLam()));
                pstmt.setString(4, dangKy.getTrangthai().getValue());
            }
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    dangKy.setMaDangky(rs.getInt(1));
                }
                System.out.println("✅ Đăng ký ca làm thành công! Loại: " + dangKy.getLoaiCa());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi đăng ký ca làm: " + e.getMessage());
            System.err.println("❌ SQL State: " + e.getSQLState());
            System.err.println("❌ Error Code: " + e.getErrorCode());
            System.err.println("❌ SQL Query: " + sql);
            System.err.println("❌ DangKy object: " + dangKy);
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Kiểm tra user đã đăng ký ca này trong ngày chưa (cho ca bình thường)
     */
    public boolean isDaDangKyCaBinhThuong(int maNguoidung, int maCalam, LocalDate ngayLam) {
        String sql = "SELECT COUNT(*) FROM dangkycalam " +
                     "WHERE ma_nguoidung = ? AND ma_calam = ? AND ngay_lam = ? AND trangthai != 'ừ chối'";
        
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maNguoidung);
            pstmt.setInt(2, maCalam);
            pstmt.setDate(3, Date.valueOf(ngayLam));
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi kiểm tra đăng ký: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Kiểm tra user có ca trùng giờ trong ngày không (cho ca gãy)
     */
    public boolean isTrungGio(int maNguoidung, LocalDate ngayLam, Time gioBatDau, Time gioKetThuc) {
        String sql = "SELECT COUNT(*) FROM dangkycalam " +
                     "WHERE ma_nguoidung = ? AND ngay_lam = ? AND trangthai != 'ừ chối' " +
                     "AND (" +
                     "  (ma_calam IS NULL AND gbd_cagay < ? AND gkt_cagay > ?) " +  // Ca gãy trùng giờ
                     "  OR " +
                     "  (ma_calam IS NOT NULL AND EXISTS (" +  // Ca bình thường trùng giờ
                     "    SELECT 1 FROM calam c " +
                     "    WHERE c.ma_calam = dangkycalam.ma_calam " +
                     "    AND c.gio_batdau < ? AND c.gio_ketthuc > ?" +
                     "  ))" +
                     ")";
        
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maNguoidung);
            pstmt.setDate(2, Date.valueOf(ngayLam));
            pstmt.setTime(3, gioKetThuc);
            pstmt.setTime(4, gioBatDau);
            pstmt.setTime(5, gioKetThuc);
            pstmt.setTime(6, gioBatDau);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi kiểm tra trùng giờ: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Lấy danh sách đăng ký của user
     */
    public List<DangKy> getDangKyByUser(int maNguoidung) {
        List<DangKy> list = new ArrayList<>();
        String sql = "SELECT d.*, c.mo_ta, c.gio_batdau, c.gio_ketthuc " +
                     "FROM dangkycalam d " +
                     "LEFT JOIN calam c ON d.ma_calam = c.ma_calam " +
                     "WHERE d.ma_nguoidung = ? " +
                     "ORDER BY d.ngay_lam DESC, COALESCE(d.gbd_cagay, c.gio_batdau) ASC";
        
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maNguoidung);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                DangKy dangKy = new DangKy();
                dangKy.setMaDangky(rs.getInt("ma_dangky"));
                dangKy.setMaNguoidung(rs.getInt("ma_nguoidung"));
                
                // Xử lý ma_calam (có thể null)
                int maCalam = rs.getInt("ma_calam");
                if (!rs.wasNull()) {
                    // ✅ CA BÌNH THƯỜNG
                    dangKy.setMaCalam(maCalam);
                    dangKy.setMoTaCaLam(rs.getString("mo_ta"));
                    dangKy.setGbdCagay(rs.getTime("gio_batdau"));
                    dangKy.setGktCagay(rs.getTime("gio_ketthuc"));
                } else {
                    // ✅ CA GÃY
                    dangKy.setMaCalam(null);
                    dangKy.setMoTaCaLam("Ca gãy");
                    dangKy.setGbdCagay(rs.getTime("gbd_cagay"));
                    dangKy.setGktCagay(rs.getTime("gkt_cagay"));
                }
                
                dangKy.setThoigianDangky(rs.getTimestamp("thoigian_dangky").toLocalDateTime());
                dangKy.setNgayLam(rs.getDate("ngay_lam").toLocalDate());
                dangKy.setTrangthai(DangKy.TrangThai.fromString(rs.getString("trangthai")));
                
                list.add(dangKy);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy danh sách đăng ký: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Lấy tất cả đăng ký (cho admin)
     */
    public List<DangKy> getAllDangKy() {
        List<DangKy> list = new ArrayList<>();
        String sql = "SELECT d.*, c.mo_ta, c.gio_batdau, c.gio_ketthuc, n.ho_ten " +
                     "FROM dangkycalam d " +
                     "LEFT JOIN calam c ON d.ma_calam = c.ma_calam " +
                     "INNER JOIN nguoidung n ON d.ma_nguoidung = n.ma_nguoidung " +
                     "ORDER BY d.ngay_lam DESC, COALESCE(d.gbd_cagay, c.gio_batdau) ASC";
        
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                DangKy dangKy = new DangKy();
                dangKy.setMaDangky(rs.getInt("ma_dangky"));
                dangKy.setMaNguoidung(rs.getInt("ma_nguoidung"));
                dangKy.setTenNguoiDung(rs.getString("ho_ten"));
                
                // Xử lý ma_calam (có thể null)
                int maCalam = rs.getInt("ma_calam");
                if (!rs.wasNull()) {
                    dangKy.setMaCalam(maCalam);
                    dangKy.setMoTaCaLam(rs.getString("mo_ta"));
                    dangKy.setGbdCagay(rs.getTime("gio_batdau"));
                    dangKy.setGktCagay(rs.getTime("gio_ketthuc"));
                } else {
                    dangKy.setMaCalam(null);
                    dangKy.setMoTaCaLam("Ca gãy");
                    dangKy.setGbdCagay(rs.getTime("gbd_cagay"));
                    dangKy.setGktCagay(rs.getTime("gkt_cagay"));
                }
                
                dangKy.setThoigianDangky(rs.getTimestamp("thoigian_dangky").toLocalDateTime());
                dangKy.setNgayLam(rs.getDate("ngay_lam").toLocalDate());
                dangKy.setTrangthai(DangKy.TrangThai.fromString(rs.getString("trangthai")));
                
                list.add(dangKy);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy tất cả đăng ký: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Hủy đăng ký
     */
    public boolean huyDangKy(int maDangky, int maNguoidung) {
        String checkSql = "SELECT trangthai, ngay_lam FROM dangkycalam WHERE ma_dangky = ? AND ma_nguoidung = ?";
        
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, maDangky);
            checkStmt.setInt(2, maNguoidung);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                String trangThai = rs.getString("trangthai");
                LocalDate ngayLam = rs.getDate("ngay_lam").toLocalDate();
                
                if ("đã duyệt".equals(trangThai) && ngayLam.isBefore(LocalDate.now())) {
                    System.out.println("❌ Không thể hủy ca đã duyệt và đã qua!");
                    return false;
                }
                
                String deleteSql = "DELETE FROM dangkycalam WHERE ma_dangky = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, maDangky);
                    int affected = deleteStmt.executeUpdate();
                    
                    if (affected > 0) {
                        System.out.println("✅ Hủy đăng ký thành công!");
                        return true;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi hủy đăng ký: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Lấy danh sách đăng ký cho admin với filter và phân trang
     * - Chỉ hiển thị các đăng ký CHỜ DUYỆT
     * - Chỉ hiển thị các đăng ký chưa quá ngày (ngày_lam >= hôm nay)
     */
    public List<DangKy> getDanhSachDangKyAdminWithFilter(Integer maCalam, LocalDate ngayFilter, int limit, int offset) {
        List<DangKy> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT d.*, c.mo_ta, c.gio_batdau, c.gio_ketthuc, n.ho_ten ");
        sql.append("FROM dangkycalam d ");
        sql.append("LEFT JOIN calam c ON d.ma_calam = c.ma_calam ");
        sql.append("INNER JOIN nguoidung n ON d.ma_nguoidung = n.ma_nguoidung ");
        sql.append("WHERE d.trangthai = 'chờ duyệt' ");
        sql.append("AND d.ngay_lam >= CURDATE() ");
        
        if (maCalam != null) {
            sql.append("AND d.ma_calam = ? ");
        }
        
        if (ngayFilter != null) {
            sql.append("AND d.ngay_lam = ? ");
        }
        
        sql.append("ORDER BY d.ngay_lam ASC, COALESCE(d.gbd_cagay, c.gio_batdau) ASC ");
        sql.append("LIMIT ? OFFSET ?");
        
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (maCalam != null) {
                pstmt.setInt(paramIndex++, maCalam);
            }
            
            if (ngayFilter != null) {
                pstmt.setDate(paramIndex++, Date.valueOf(ngayFilter));
            }
            
            pstmt.setInt(paramIndex++, limit);
            pstmt.setInt(paramIndex, offset);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                DangKy dangKy = new DangKy();
                dangKy.setMaDangky(rs.getInt("ma_dangky"));
                dangKy.setMaNguoidung(rs.getInt("ma_nguoidung"));
                dangKy.setTenNguoiDung(rs.getString("ho_ten"));
                
                // Xử lý ma_calam (có thể null)
                int maCaLamResult = rs.getInt("ma_calam");
                if (!rs.wasNull()) {
                    dangKy.setMaCalam(maCaLamResult);
                    dangKy.setMoTaCaLam(rs.getString("mo_ta"));
                    dangKy.setGbdCagay(rs.getTime("gio_batdau"));
                    dangKy.setGktCagay(rs.getTime("gio_ketthuc"));
                } else {
                    dangKy.setMaCalam(null);
                    dangKy.setMoTaCaLam("Ca gãy: " + rs.getTime("gbd_cagay") + " - " + rs.getTime("gkt_cagay"));
                    dangKy.setGbdCagay(rs.getTime("gbd_cagay"));
                    dangKy.setGktCagay(rs.getTime("gkt_cagay"));
                }
                
                dangKy.setThoigianDangky(rs.getTimestamp("thoigian_dangky").toLocalDateTime());
                dangKy.setNgayLam(rs.getDate("ngay_lam").toLocalDate());
                dangKy.setTrangthai(DangKy.TrangThai.fromString(rs.getString("trangthai")));
                
                list.add(dangKy);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy danh sách đăng ký admin: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Đếm tổng số đăng ký cho admin (với filter)
     */
    public int countDanhSachDangKyAdmin(Integer maCalam, LocalDate ngayFilter) {
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT COUNT(*) ");
        sql.append("FROM dangkycalam d ");
        sql.append("WHERE d.trangthai = 'chờ duyệt' ");
        sql.append("AND d.ngay_lam >= CURDATE() ");
        
        if (maCalam != null) {
            sql.append("AND d.ma_calam = ? ");
        }
        
        if (ngayFilter != null) {
            sql.append("AND d.ngay_lam = ? ");
        }
        
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (maCalam != null) {
                pstmt.setInt(paramIndex++, maCalam);
            }
            
            if (ngayFilter != null) {
                pstmt.setDate(paramIndex, Date.valueOf(ngayFilter));
            }
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi đếm đăng ký admin: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Cập nhật trạng thái đăng ký (dành cho admin)
     */
    public boolean updateTrangThai(int maDangky, DangKy.TrangThai trangThai) {
        String sql = "UPDATE dangkycalam SET trangthai = ? WHERE ma_dangky = ?";
        
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, trangThai.getValue());
            pstmt.setInt(2, maDangky);
            
            int affected = pstmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println("✅ Cập nhật trạng thái thành công!");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi cập nhật trạng thái: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Lấy danh sách lịch trình (đăng ký) cho admin với bộ lọc
     */
    public List<DangKy> getScheduleWithFilter(String searchKeyword, LocalDate fromDate, LocalDate toDate, int limit, int offset) {
        List<DangKy> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT d.*, c.mo_ta, c.gio_batdau, c.gio_ketthuc, n.ho_ten ");
        sql.append("FROM dangkycalam d ");
        sql.append("LEFT JOIN calam c ON d.ma_calam = c.ma_calam ");
        sql.append("INNER JOIN nguoidung n ON d.ma_nguoidung = n.ma_nguoidung ");
        sql.append("WHERE 1=1 ");
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append("AND (LOWER(n.ho_ten) LIKE ? OR LOWER(c.mo_ta) LIKE ?) ");
        }
        
        if (fromDate != null) {
            sql.append("AND d.ngay_lam >= ? ");
        }
        
        if (toDate != null) {
            sql.append("AND d.ngay_lam <= ? ");
        }
        
        sql.append("ORDER BY d.ngay_lam DESC, COALESCE(d.gbd_cagay, c.gio_batdau) ASC ");
        sql.append("LIMIT ? OFFSET ?");
        
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                String keyword = "%" + searchKeyword.toLowerCase() + "%";
                pstmt.setString(paramIndex++, keyword);
                pstmt.setString(paramIndex++, keyword);
            }
            
            if (fromDate != null) {
                pstmt.setDate(paramIndex++, Date.valueOf(fromDate));
            }
            
            if (toDate != null) {
                pstmt.setDate(paramIndex++, Date.valueOf(toDate));
            }
            
            pstmt.setInt(paramIndex++, limit);
            pstmt.setInt(paramIndex, offset);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                DangKy dangKy = new DangKy();
                dangKy.setMaDangky(rs.getInt("ma_dangky"));
                dangKy.setMaNguoidung(rs.getInt("ma_nguoidung"));
                dangKy.setTenNguoiDung(rs.getString("ho_ten"));
                
                int maCaLamResult = rs.getInt("ma_calam");
                if (!rs.wasNull()) {
                    dangKy.setMaCalam(maCaLamResult);
                    dangKy.setMoTaCaLam(rs.getString("mo_ta"));
                    dangKy.setGbdCagay(rs.getTime("gio_batdau"));
                    dangKy.setGktCagay(rs.getTime("gio_ketthuc"));
                } else {
                    dangKy.setMaCalam(null);
                    dangKy.setMoTaCaLam("Ca gãy: " + rs.getTime("gbd_cagay") + " - " + rs.getTime("gkt_cagay"));
                    dangKy.setGbdCagay(rs.getTime("gbd_cagay"));
                    dangKy.setGktCagay(rs.getTime("gkt_cagay"));
                }
                
                dangKy.setThoigianDangky(rs.getTimestamp("thoigian_dangky").toLocalDateTime());
                dangKy.setNgayLam(rs.getDate("ngay_lam").toLocalDate());
                dangKy.setTrangthai(DangKy.TrangThai.fromString(rs.getString("trangthai")));
                
                list.add(dangKy);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy lịch trình: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Đếm tổng số lịch trình với bộ lọc
     */
    public int countScheduleWithFilter(String searchKeyword, LocalDate fromDate, LocalDate toDate) {
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT COUNT(*) FROM dangkycalam d ");
        sql.append("LEFT JOIN calam c ON d.ma_calam = c.ma_calam ");
        sql.append("INNER JOIN nguoidung n ON d.ma_nguoidung = n.ma_nguoidung ");
        sql.append("WHERE 1=1 ");
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append("AND (LOWER(n.ho_ten) LIKE ? OR LOWER(c.mo_ta) LIKE ?) ");
        }
        
        if (fromDate != null) {
            sql.append("AND d.ngay_lam >= ? ");
        }
        
        if (toDate != null) {
            sql.append("AND d.ngay_lam <= ? ");
        }
        
        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                String keyword = "%" + searchKeyword.toLowerCase() + "%";
                pstmt.setString(paramIndex++, keyword);
                pstmt.setString(paramIndex++, keyword);
            }
            
            if (fromDate != null) {
                pstmt.setDate(paramIndex++, Date.valueOf(fromDate));
            }
            
            if (toDate != null) {
                pstmt.setDate(paramIndex, Date.valueOf(toDate));
            }
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi đếm lịch trình: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
}
