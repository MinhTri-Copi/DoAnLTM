package com.example.doanltm.Model;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DangKy implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int maDangky;
    private int maNguoidung;
    private Integer maCalam;  // Nullable - NULL nếu là ca gãy
    private LocalDateTime thoigianDangky;
    private LocalDate ngayLam;
    private Time gbdCagay;    // NULL nếu là ca bình thường
    private Time gktCagay;    // NULL nếu là ca bình thường
    private TrangThai trangthai;
    
    // Thông tin bổ sung để hiển thị
    private String tenNguoiDung;
    private String moTaCaLam;
    private String loaiCa;
    
    public enum TrangThai {
        CHO_DUYET("chờ duyệt"),
        DA_DUYET("đã duyệt"),
        TU_CHOI("từ chối");
        
        private String value;
        
        TrangThai(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static TrangThai fromString(String text) {
            for (TrangThai t : TrangThai.values()) {
                if (t.value.equalsIgnoreCase(text)) {
                    return t;
                }
            }
            return CHO_DUYET;
        }
    }
    
    public DangKy() {}
    
    // Constructor cho ca bình thường
    public DangKy(int maNguoidung, int maCalam, LocalDate ngayLam) {
        this.maNguoidung = maNguoidung;
        this.maCalam = maCalam;
        this.ngayLam = ngayLam;
        // thoigian_dangky sẽ được set bởi database (NOW())
        this.trangthai = TrangThai.CHO_DUYET;
        // gbd_cagay và gkt_cagay sẽ là null
        this.gbdCagay = null;
        this.gktCagay = null;
    }
    
    // Constructor cho ca gãy
    public DangKy(int maNguoidung, LocalDate ngayLam, Time gbdCagay, Time gktCagay) {
        this.maNguoidung = maNguoidung;
        this.maCalam = null;  // Ca gãy không có ma_calam
        this.ngayLam = ngayLam;
        this.gbdCagay = gbdCagay;
        this.gktCagay = gktCagay;
        // thoigian_dangky sẽ được set bởi database (NOW())
        this.trangthai = TrangThai.CHO_DUYET;
    }
    
    // Getters and Setters
    public int getMaDangky() {
        return maDangky;
    }
    
    public void setMaDangky(int maDangky) {
        this.maDangky = maDangky;
    }
    
    public int getMaNguoidung() {
        return maNguoidung;
    }
    
    public void setMaNguoidung(int maNguoidung) {
        this.maNguoidung = maNguoidung;
    }
    
    public Integer getMaCalam() {
        return maCalam;
    }
    
    public void setMaCalam(Integer maCalam) {
        this.maCalam = maCalam;
    }
    
    public LocalDateTime getThoigianDangky() {
        return thoigianDangky;
    }
    
    public void setThoigianDangky(LocalDateTime thoigianDangky) {
        this.thoigianDangky = thoigianDangky;
    }
    
    public LocalDate getNgayLam() {
        return ngayLam;
    }
    
    public void setNgayLam(LocalDate ngayLam) {
        this.ngayLam = ngayLam;
    }
    
    public Time getGbdCagay() {
        return gbdCagay;
    }
    
    public void setGbdCagay(Time gbdCagay) {
        this.gbdCagay = gbdCagay;
    }
    
    public Time getGktCagay() {
        return gktCagay;
    }
    
    public void setGktCagay(Time gktCagay) {
        this.gktCagay = gktCagay;
    }
    
    public TrangThai getTrangthai() {
        return trangthai;
    }
    
    public void setTrangthai(TrangThai trangthai) {
        this.trangthai = trangthai;
    }
    
    public String getTenNguoiDung() {
        return tenNguoiDung;
    }
    
    public void setTenNguoiDung(String tenNguoiDung) {
        this.tenNguoiDung = tenNguoiDung;
    }
    
    public String getMoTaCaLam() {
        return moTaCaLam;
    }
    
    public void setMoTaCaLam(String moTaCaLam) {
        this.moTaCaLam = moTaCaLam;
    }
    
    public String getLoaiCa() {
        return loaiCa != null ? loaiCa : (isCaGay() ? "Ca gãy" : "Ca bình thường");
    }
    
    public void setLoaiCa(String loaiCa) {
        this.loaiCa = loaiCa;
    }
    
    /**
     * Kiểm tra có phải ca gãy không
     */
    public boolean isCaGay() {
        return maCalam == null;
    }
    
    @Override
    public String toString() {
        return "DangKy{" +
                "maDangky=" + maDangky +
                ", maNguoidung=" + maNguoidung +
                ", maCalam=" + maCalam +
                ", ngayLam=" + ngayLam +
                ", thoigianDangky=" + thoigianDangky +
                ", gbdCagay=" + gbdCagay +
                ", gktCagay=" + gktCagay +
                ", trangthai=" + trangthai +
                '}';
    }
}
