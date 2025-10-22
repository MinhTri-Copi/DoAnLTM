package com.example.doanltm.Request;

import java.io.Serializable;

public class GetPendingRegistrationsRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int page;
    private int pageSize;
    
    public GetPendingRegistrationsRequest(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
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
        return "GetPendingRegistrationsRequest{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
