
package com.example.doanltm.Response;

import com.example.doanltm.Model.DangKy;

import java.io.Serializable;

public class DangKyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private DangKy dangKy;

    public DangKyResponse() {}

    public DangKyResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public DangKyResponse(boolean success, String message, DangKy dangKy) {
        this.success = success;
        this.message = message;
        this.dangKy = dangKy;
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

    public DangKy getDangKy() {
        return dangKy;
    }

    public void setDangKy(DangKy dangKy) {
        this.dangKy = dangKy;
    }
}