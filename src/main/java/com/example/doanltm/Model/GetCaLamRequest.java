
package com.example.doanltm.Model;

import java.io.Serializable;
import java.time.LocalDate;

public class GetCaLamRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate ngayLam;

    public GetCaLamRequest() {}

    public GetCaLamRequest(LocalDate ngayLam) {
        this.ngayLam = ngayLam;
    }

    public LocalDate getNgayLam() {
        return ngayLam;
    }

    public void setNgayLam(LocalDate ngayLam) {
        this.ngayLam = ngayLam;
    }

    @Override
    public String toString() {
        return "GetCaLamRequest{ngayLam=" + ngayLam + '}';
    }
}