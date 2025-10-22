package com.example.doanltm.Model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int maNguoidung;
    private String email;
    private String matKhau;
    private String hoTen;
    private int maVaitro;
    private String tenVaitro; // Để hiển thị
    
    // Constructors
    public User() {}
    
    public User(String email, String matKhau) {
        this.email = email;
        this.matKhau = matKhau;
    }
    
    public User(int maNguoidung, String email, String hoTen, int maVaitro, String tenVaitro,  String matKhau) {
        this.maNguoidung = maNguoidung;
        this.email = email;
        this.hoTen = hoTen;
        this.maVaitro = maVaitro;
        this.tenVaitro = tenVaitro;
        this.matKhau = matKhau;
    }
    
    // Getters and Setters
    public int getMaNguoidung() {
        return maNguoidung;
    }
    
    public void setMaNguoidung(int maNguoidung) {
        this.maNguoidung = maNguoidung;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getMatKhau() {
        return matKhau;
    }
    
    public void setMatKhau(String matKhauUser) {
        this.matKhau = matKhauUser;
    }
    
    public String getHoTen() {
        return hoTen;
    }
    
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }
    
    public int getMaVaitro() {
        return maVaitro;
    }
    
    public void setMaVaitro(int maVaitro) {
        this.maVaitro = maVaitro;
    }
    
    public String getTenVaitro() {
        return tenVaitro;
    }
    
    public void setTenVaitro(String tenVaitro) {
        this.tenVaitro = tenVaitro;
    }
    
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(tenVaitro) || maVaitro == 1;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "maNguoidung=" + maNguoidung +
                ", email='" + email + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", tenVaitro='" + tenVaitro + '\'' +
                '}';
    }
}
