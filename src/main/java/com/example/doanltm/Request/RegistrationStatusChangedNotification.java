package com.example.doanltm.Request;

import java.io.Serializable;

/**
 * Thông báo broadcast khi trạng thái đăng ký thay đổi
 * Server gửi cho tất cả admin clients khi admin thay đổi trạng thái
 */
public class RegistrationStatusChangedNotification implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int maDangky;
    private String newStatus;
    private String message;
    
    public RegistrationStatusChangedNotification(int maDangky, String newStatus, String message) {
        this.maDangky = maDangky;
        this.newStatus = newStatus;
        this.message = message;
    }
    
    public int getMaDangky() {
        return maDangky;
    }
    
    public void setMaDangky(int maDangky) {
        this.maDangky = maDangky;
    }
    
    public String getNewStatus() {
        return newStatus;
    }
    
    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "RegistrationStatusChangedNotification{" +
                "maDangky=" + maDangky +
                ", newStatus='" + newStatus + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
