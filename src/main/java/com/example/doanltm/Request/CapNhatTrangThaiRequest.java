package com.example.doanltm.Request;

import com.example.doanltm.Model.DangKy;
import java.io.Serializable;

public class CapNhatTrangThaiRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int maDangky;
    private DangKy.TrangThai trangThai;
    
    public CapNhatTrangThaiRequest() {}
    
    public CapNhatTrangThaiRequest(int maDangky, DangKy.TrangThai trangThai) {
        this.maDangky = maDangky;
        this.trangThai = trangThai;
    }
    
    public int getMaDangky() {
        return maDangky;
    }
    
    public void setMaDangky(int maDangky) {
        this.maDangky = maDangky;
    }
    
    public DangKy.TrangThai getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(DangKy.TrangThai trangThai) {
        this.trangThai = trangThai;
    }
    
    @Override
    public String toString() {
        return "CapNhatTrangThaiRequest{maDangky=" + maDangky + ", trangThai=" + trangThai + "}";
    }
}