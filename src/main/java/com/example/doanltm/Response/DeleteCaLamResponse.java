package com.example.doanltm.Response;

import java.io.Serializable;

public class DeleteCaLamResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private String message;
    
    public DeleteCaLamResponse() {}
    
    public DeleteCaLamResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
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
    
    @Override
    public String toString() {
        return "DeleteCaLamResponse{success=" + success + ", message='" + message + "'}";
    }
}
