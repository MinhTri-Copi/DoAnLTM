package com.example.doanltm.Request;

import java.io.Serializable;
import java.time.LocalDate;

public class ThongKeCaNhanRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate month;
    private int maNguoidung;

    public ThongKeCaNhanRequest() {}

    public ThongKeCaNhanRequest(LocalDate month, int maNguoidung) {
        this.month = month;
        this.maNguoidung = maNguoidung;

    }
    public int getMaNguoidung() {
        return maNguoidung;
    }

    public void setMaNguoidung(int maNguoidung) {
        this.maNguoidung = maNguoidung;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    @Override
    public String toString() {
        return "ThongKeCaNhanRequest{month=" + month +  ", maNguoidung=" + maNguoidung + "}";
    }
}
