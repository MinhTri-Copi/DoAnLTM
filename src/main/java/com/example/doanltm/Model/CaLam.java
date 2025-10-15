package com.example.doanltm.Model;

import java.io.Serializable;
import java.sql.Time;

public class CaLam implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int maCalam;
    private Time gioBatdau;
    private Time gioKetthuc;
    private String moTa;
    private int soLuongToiDa;
    private int soLuongDaDangKy; // Để hiển thị số người đã đăng ký
    
    public CaLam() {}
    
    public CaLam(int maCalam, Time gioBatdau, Time gioKetthuc, String moTa, int soLuongToiDa) {
        this.maCalam = maCalam;
        this.gioBatdau = gioBatdau;
        this.gioKetthuc = gioKetthuc;
        this.moTa = moTa;
        this.soLuongToiDa = soLuongToiDa;
    }
    
    // Getters and Setters
    public int getMaCalam() {
        return maCalam;
    }
    
    public void setMaCalam(int maCalam) {
        this.maCalam = maCalam;
    }
    
    public Time getGioBatdau() {
        return gioBatdau;
    }
    
    public void setGioBatdau(Time gioBatdau) {
        this.gioBatdau = gioBatdau;
    }
    
    public Time getGioKetthuc() {
        return gioKetthuc;
    }
    
    public void setGioKetthuc(Time gioKetthuc) {
        this.gioKetthuc = gioKetthuc;
    }
    
    public String getMoTa() {
        return moTa;
    }
    
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    
    public int getSoLuongToiDa() {
        return soLuongToiDa;
    }
    
    public void setSoLuongToiDa(int soLuongToiDa) {
        this.soLuongToiDa = soLuongToiDa;
    }
    
    public int getSoLuongDaDangKy() {
        return soLuongDaDangKy;
    }
    
    public void setSoLuongDaDangKy(int soLuongDaDangKy) {
        this.soLuongDaDangKy = soLuongDaDangKy;
    }
    
    public boolean isFullSlot() {
        return soLuongDaDangKy >= soLuongToiDa;
    }
    
    public String getThoiGian() {
        return gioBatdau + " - " + gioKetthuc;
    }
    
    @Override
    public String toString() {
        return "CaLam{" +
                "maCalam=" + maCalam +
                ", gioBatdau=" + gioBatdau +
                ", gioKetthuc=" + gioKetthuc +
                ", moTa='" + moTa + '\'' +
                ", soLuongToiDa=" + soLuongToiDa +
                ", soLuongDaDangKy=" + soLuongDaDangKy +
                '}';
    }
}
