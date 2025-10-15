package com.example.doanltm.Model;

import java.io.Serializable;

public class LoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String email;
    private String matKhau;
    
    public LoginRequest() {}
    
    public LoginRequest(String email, String matKhau) {
        this.email = email;
        this.matKhau = matKhau;
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
    
    @Override
    public String toString() {
        return "LoginRequest{email='" + email + "'}";
    }
}
