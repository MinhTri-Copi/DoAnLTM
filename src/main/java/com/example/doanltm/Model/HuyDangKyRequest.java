package com.example.doanltm.Model;

import java.io.Serializable;

public class HuyDangKyRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maDangky;
    private int maNguoidung;

    public HuyDangKyRequest() {}

    public HuyDangKyRequest(int maDangky, int maNguoidung) {
        this.maDangky = maDangky;
        this.maNguoidung = maNguoidung;
    }

    public int getMaDangky() {
        return maDangky;
    }

    public void setMaDangky(int maDangky) {
        this.maDangky = maDangky;
    }

    public int getMaNguoidung() {
        return maNguoidung;
    }

    public void setMaNguoidung(int maNguoidung) {
        this.maNguoidung = maNguoidung;
    }

    @Override
    public String toString() {
        return "HuyDangKyRequest{maDangky=" + maDangky + ", maNguoidung=" + maNguoidung + '}';
    }
}