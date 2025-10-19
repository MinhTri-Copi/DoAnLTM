package com.example.doanltm.Response;

import com.example.doanltm.Model.DangKy;
import java.io.Serializable;
import java.util.List;

public class DanhSachDangKyAdminResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private String message;
    private List<DangKy> registrations;
    
    public DanhSachDangKyAdminResponse() {}
    
    public DanhSachDangKyAdminResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public DanhSachDangKyAdminResponse(boolean success, String message, List<DangKy> registrations) {
        this.success = success;
        this.message = message;
        this.registrations = registrations;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<DangKy> getRegistrations() {
        return registrations;
    }
    
    public void setRegistrations(List<DangKy> registrations) {
        this.registrations = registrations;
    }
    
    @Override
    public String toString() {
        return "DanhSachDangKyAdminResponse{success=" + success + ", message='" + message + 
               "', registrations=" + (registrations != null ? registrations.size() + " items" : "null") + "}";
    }
}