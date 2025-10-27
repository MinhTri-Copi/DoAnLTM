package com.example.doanltm.Request;

import java.io.Serializable;
import java.time.LocalDate;

public class DanhSachDangKyAdminRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer maCalam;
    private LocalDate ngayFilter;
    private String statusFilter; // New field for filtering by status
    private int page;      // Trang hiện tại (bắt đầu từ 1)
    private int pageSize;  // Số bản ghi mỗi trang
    
    public DanhSachDangKyAdminRequest() {
        this.page = 1;
        this.pageSize = 10;
        this.statusFilter = "chờ duyệt"; // Default to pending registrations
    }
    
    public DanhSachDangKyAdminRequest(Integer maCalam, LocalDate ngayFilter) {
        this.maCalam = maCalam;
        this.ngayFilter = ngayFilter;
        this.page = 1;
        this.pageSize = 10;
        this.statusFilter = "chờ duyệt"; // Default to pending registrations
    }
    
    public DanhSachDangKyAdminRequest(Integer maCalam, LocalDate ngayFilter, String statusFilter, int page, int pageSize) {
        this.maCalam = maCalam;
        this.ngayFilter = ngayFilter;
        this.statusFilter = statusFilter;
        this.page = page;
        this.pageSize = pageSize;
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
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getStatusFilter() {
        return statusFilter;
    }

    public void setStatusFilter(String statusFilter) {
        this.statusFilter = statusFilter;
    }

    @Override
    public String toString() {
        return "DanhSachDangKyAdminRequest{maCalam=" + maCalam + ", ngayFilter=" + ngayFilter + ", statusFilter='" + statusFilter + "'" +
               ", page=" + page + ", pageSize=" + pageSize + "}";
    }
}
