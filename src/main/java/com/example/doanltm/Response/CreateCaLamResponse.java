package com.example.doanltm.Response;

import com.example.doanltm.Model.CaLam;
import java.io.Serializable;

public class CreateCaLamResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private String message;
    private CaLam caLam;
    
    public CreateCaLamResponse() {}
    
    public CreateCaLamResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public CreateCaLamResponse(boolean success, String message, CaLam caLam) {
        this.success = success;
        this.message = message;
        this.caLam = caLam;
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
    
    public CaLam getCaLam() {
        return caLam;
    }
    
    public void setCaLam(CaLam caLam) {
        this.caLam = caLam;
    }
    
    @Override
    public String toString() {
        return "CreateCaLamResponse{success=" + success + ", message='" + message + "', caLam=" + caLam + "}";
    }
}
