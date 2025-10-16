package com.example.doanltm.Request;

import com.example.doanltm.Model.DangKy;

import java.io.Serializable;

public class DangKyRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private DangKy dangKy;

    public DangKyRequest() {}

    public DangKyRequest(DangKy dangKy) {
        this.dangKy = dangKy;
    }

    public DangKy getDangKy() {
        return dangKy;
    }

    public void setDangKy(DangKy dangKy) {
        this.dangKy = dangKy;
    }

    @Override
    public String toString() {
        return "DangKyRequest{dangKy=" + dangKy + '}';
    }
}