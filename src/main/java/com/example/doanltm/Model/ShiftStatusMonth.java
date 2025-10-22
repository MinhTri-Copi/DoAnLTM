package com.example.doanltm.Model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Model để lưu trạng thái ca làm trong 1 tháng
 * Mỗi hàng đại diện cho 1 ca vào 1 ngày cụ thể
 */
public class ShiftStatusMonth implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int maCalam;
    private String tenCa;                    // Mô tả ca (sáng, trưa, chiều, tối)
    private String moTa;                     // Mô tả ca (full name)
    private LocalTime gioBatdau;
    private LocalTime gioKetthuc;
    private String gbdCagay;                 // Giờ bắt đầu dạng string
    private String gktCagay;                 // Giờ kết thúc dạng string
    private int soLuongToiDa;
    private int soDangky;                    // Số đã đăng ký
    private String trangThai;                // Trạng thái (Đầy, Còn chỗ, Chưa có đăng ký)
    private LocalDate ngay;                  // Ngày cụ thể
    private String loaiCa;                   // Loại ca
    
    // Map<ngayTrongThang, soLuongDaDangKy>
    private Map<Integer, Integer> registrationCountByDay = new HashMap<>();
    
    public ShiftStatusMonth() {
    }
    
    public ShiftStatusMonth(int maCalam, String tenCa, LocalTime gioBatdau, LocalTime gioKetthuc, int soLuongToiDa) {
        this.maCalam = maCalam;
        this.tenCa = tenCa;
        this.gioBatdau = gioBatdau;
        this.gioKetthuc = gioKetthuc;
        this.soLuongToiDa = soLuongToiDa;
    }
    
    // Getters and Setters
    public int getMaCalam() {
        return maCalam;
    }
    
    public void setMaCalam(int maCalam) {
        this.maCalam = maCalam;
    }
    
    public String getTenCa() {
        return tenCa;
    }
    
    public void setTenCa(String tenCa) {
        this.tenCa = tenCa;
    }
    
    public LocalTime getGioBatdau() {
        return gioBatdau;
    }
    
    public void setGioBatdau(LocalTime gioBatdau) {
        this.gioBatdau = gioBatdau;
    }
    
    public LocalTime getGioKetthuc() {
        return gioKetthuc;
    }
    
    public void setGioKetthuc(LocalTime gioKetthuc) {
        this.gioKetthuc = gioKetthuc;
    }
    
    public int getSoLuongToiDa() {
        return soLuongToiDa;
    }
    
    public void setSoLuongToiDa(int soLuongToiDa) {
        this.soLuongToiDa = soLuongToiDa;
    }
    
    public String getMoTa() {
        return moTa;
    }
    
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    
    public String getGbdCagay() {
        return gbdCagay;
    }
    
    public void setGbdCagay(String gbdCagay) {
        this.gbdCagay = gbdCagay;
    }
    
    public String getGktCagay() {
        return gktCagay;
    }
    
    public void setGktCagay(String gktCagay) {
        this.gktCagay = gktCagay;
    }
    
    public int getSoDangky() {
        return soDangky;
    }
    
    public void setSoDangky(int soDangky) {
        this.soDangky = soDangky;
    }
    
    public String getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    
    public LocalDate getNgay() {
        return ngay;
    }
    
    public void setNgay(LocalDate ngay) {
        this.ngay = ngay;
    }
    
    public String getLoaiCa() {
        return loaiCa;
    }
    
    public void setLoaiCa(String loaiCa) {
        this.loaiCa = loaiCa;
    }
    
    public Map<Integer, Integer> getRegistrationCountByDay() {
        return registrationCountByDay;
    }
    
    public void setRegistrationCountByDay(Map<Integer, Integer> registrationCountByDay) {
        this.registrationCountByDay = registrationCountByDay;
    }
    
    /**
     * Lấy số lượng đã đăng ký cho ngày cụ thể
     */
    public int getRegistrationCountForDay(int day) {
        return registrationCountByDay.getOrDefault(day, 0);
    }
    
    /**
     * Kiểm tra ca có full cho ngày cụ thể không
     */
    public boolean isFullForDay(int day) {
        return getRegistrationCountForDay(day) >= soLuongToiDa;
    }
    
    /**
     * Đặt số lượng đã đăng ký cho ngày cụ thể
     */
    public void setRegistrationCountForDay(int day, int count) {
        registrationCountByDay.put(day, count);
    }
    
    @Override
    public String toString() {
        return "ShiftStatusMonth{" +
                "maCalam=" + maCalam +
                ", tenCa='" + tenCa + '\'' +
                ", gioBatdau=" + gioBatdau +
                ", gioKetthuc=" + gioKetthuc +
                ", soLuongToiDa=" + soLuongToiDa +
                '}';
    }
}
