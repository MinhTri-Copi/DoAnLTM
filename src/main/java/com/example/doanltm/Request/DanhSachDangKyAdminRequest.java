package com.example.doanltm.Request;

import java.io.Serializable;
import java.time.LocalDate;

public class DanhSachDangKyAdminRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer maCalam;
    private LocalDate ngayFilter;
    
    public DanhSachDangKyAdminRequest() {}
    
    public DanhSachDangKyAdminRequest(Integer maCalam, LocalDate ngayFilter) {
        this.maCalam = maCalam;
        this.ngayFilter = ngayFilter;
    }
    
    public Integer getMaCalam() {
        return maCalam;
    }
    
    public void setMaCalam(Integer maCalam) {
        this.maCalam = maCalam;
    }
    
    public LocalDate getNgayFilter() {
        return ngayFilter;
    }
    
    public void setNgayFilter(LocalDate ngayFilter) {
        this.ngayFilter = ngayFilter;
    }
    
    @Override
    public String toString() {
        return "DanhSachDangKyAdminRequest{maCalam=" + maCalam + ", ngayFilter=" + ngayFilter + "}";
    }
}