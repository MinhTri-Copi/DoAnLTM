package com.example.doanltm.Response;

import com.example.doanltm.Model.DangKy;
import java.io.Serializable;
import java.util.List;

public class GetPendingRegistrationsResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private String message;
    private List<DangKy> registrations;
    private int totalRecords;
    
    public GetPendingRegistrationsResponse(boolean success, String message, List<DangKy> registrations, int totalRecords) {
        this.success = success;
        this.message = message;
        this.registrations = registrations;
        this.totalRecords = totalRecords;
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
    
    public int getTotalRecords() {
        return totalRecords;
    }
    
    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }
    
    @Override
    public String toString() {
        return "GetPendingRegistrationsResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", registrations=" + (registrations != null ? registrations.size() : 0) +
                ", totalRecords=" + totalRecords +
                '}';
    }
}
