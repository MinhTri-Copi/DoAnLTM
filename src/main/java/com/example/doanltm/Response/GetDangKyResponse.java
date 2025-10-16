package com.example.doanltm.Response;

import com.example.doanltm.Model.DangKy;

import java.io.Serializable;
import java.util.List;

public class GetDangKyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private List<DangKy> dangKyList;

    public GetDangKyResponse() {}

    public GetDangKyResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public GetDangKyResponse(boolean success, String message, List<DangKy> dangKyList) {
        this.success = success;
        this.message = message;
        this.dangKyList = dangKyList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DangKy> getDangKyList() {
        return dangKyList;
    }

    public void setDangKyList(List<DangKy> dangKyList) {
        this.dangKyList = dangKyList;
    }
}