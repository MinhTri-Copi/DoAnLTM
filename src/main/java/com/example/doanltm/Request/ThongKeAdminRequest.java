package com.example.doanltm.Request;

import java.io.Serializable;
import java.time.LocalDate;

public class ThongKeAdminRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private LocalDate month;
    
    public ThongKeAdminRequest() {}
    
    public ThongKeAdminRequest(LocalDate month) {
        this.month = month;
    }
    
    public LocalDate getMonth() {
        return month;
    }
    
    public void setMonth(LocalDate month) {
        this.month = month;
    }
    
    @Override
    public String toString() {
        return "ThongKeAdminRequest{month=" + month + "}";
    }
}