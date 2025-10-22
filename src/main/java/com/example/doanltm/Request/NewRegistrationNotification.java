package com.example.doanltm.Request;

import com.example.doanltm.Model.DangKy;
import java.io.Serializable;

/**
 * Thông báo broadcast khi có đăng ký mới
 * Server gửi cho tất cả admin clients
 */
public class NewRegistrationNotification implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private DangKy newRegistration;
    private String message;
    
    public NewRegistrationNotification(DangKy newRegistration, String message) {
        this.newRegistration = newRegistration;
        this.message = message;
    }
    
    public DangKy getNewRegistration() {
        return newRegistration;
    }
    
    public void setNewRegistration(DangKy newRegistration) {
        this.newRegistration = newRegistration;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "NewRegistrationNotification{" +
                "newRegistration=" + newRegistration +
                ", message='" + message + '\'' +
                '}';
    }
}
