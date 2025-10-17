package com.example.doanltm.Request;

import java.io.Serializable;

public class RegisterRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String hoTen;
    private String email;
    private String matKhau;
    private int maVaitro;
    
    public RegisterRequest() {}
    
    public RegisterRequest(String hoTen, String email, String matKhau, int maVaitro) {
        this.hoTen = hoTen;
        this.email = email;
        this.matKhau = matKhau;
        this.maVaitro = maVaitro;
    }
    
    // Getters and Setters
    public String getHoTen() {
        return hoTen;
    }
    
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
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
    
    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
    
    public int getMaVaitro() {
        return maVaitro;
    }
    
    public void setMaVaitro(int maVaitro) {
        this.maVaitro = maVaitro;
    }
    
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "hoTen='" + hoTen + '\'' +
                ", email='" + email + '\'' +
                ", maVaitro=" + maVaitro +
                '}';
    }
}