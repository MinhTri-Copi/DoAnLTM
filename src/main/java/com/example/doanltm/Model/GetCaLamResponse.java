package com.example.doanltm.Model;

import java.io.Serializable;
import java.util.List;

public class GetCaLamResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private List<CaLam> caLamList;

    public GetCaLamResponse() {}

    public GetCaLamResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public GetCaLamResponse(boolean success, String message, List<CaLam> caLamList) {
        this.success = success;
        this.message = message;
        this.caLamList = caLamList;
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

    public List<CaLam> getCaLamList() {
        return caLamList;
    }

    public void setCaLamList(List<CaLam> caLamList) {
        this.caLamList = caLamList;
    }
}