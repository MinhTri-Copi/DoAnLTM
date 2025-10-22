package com.example.doanltm.Request;

import java.io.Serializable;

public class DoiMatKhauRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maNguoidung;
    private String matKhauMoi;

    public DoiMatKhauRequest() {}

    public DoiMatKhauRequest(int maNguoidung, String matKhauMoi) {
        this.maNguoidung = maNguoidung;
        this.matKhauMoi = matKhauMoi;
    }

    public int getMaNguoidung() {
        return maNguoidung;
    }

    public void setMaNguoidung(int maNguoidung) {
        this.maNguoidung = maNguoidung;
    }

    public String getMatKhauMoi() {
        return matKhauMoi;
    }

    public void setMatKhauMoi(String matKhauMoi) {
        this.matKhauMoi = matKhauMoi;
    }

    @Override
    public String toString() {
        return "DoiMatKhauRequest{" +
                "maNguoidung=" + maNguoidung +
                ", matKhauMoi='" + matKhauMoi + '\'' +
                '}';
    }
}
