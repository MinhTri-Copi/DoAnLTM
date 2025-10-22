package com.example.doanltm.Request;

import java.io.Serializable;

public class GetAllCaLamAdminRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String searchKeyword;  // Tìm kiếm theo mô tả
    private int page;
    private int pageSize;
    
    public GetAllCaLamAdminRequest() {
        this.page = 1;
        this.pageSize = 10;
    }
    
    public GetAllCaLamAdminRequest(String searchKeyword, int page, int pageSize) {
        this.searchKeyword = searchKeyword;
        this.page = page;
        this.pageSize = pageSize;
    }
    
    public String getSearchKeyword() {
        return searchKeyword;
    }
    
    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
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
    
    @Override
    public String toString() {
        return "GetAllCaLamAdminRequest{searchKeyword='" + searchKeyword + "', page=" + page + ", pageSize=" + pageSize + "}";
    }
}
