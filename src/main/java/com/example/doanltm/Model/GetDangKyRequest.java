package com.example.doanltm.Model;

import java.io.Serializable;

public class GetDangKyRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maNguoidung;

    public GetDangKyRequest() {}

    public GetDangKyRequest(int maNguoidung) {
        this.maNguoidung = maNguoidung;
    }

    public int getMaNguoidung() {
        return maNguoidung;
    }

    public void setMaNguoidung(int maNguoidung) {
        this.maNguoidung = maNguoidung;
    }

    @Override
    public String toString() {
        return "GetDangKyRequest{maNguoidung=" + maNguoidung + '}';
    }
}