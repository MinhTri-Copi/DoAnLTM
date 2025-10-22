package com.example.doanltm.Response;

import com.example.doanltm.Model.CaLam;
import java.io.Serializable;
import java.util.List;

public class GetAllCaLamAdminResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private String message;
    private List<CaLam> caLamList;
    private int totalRecords;
    
    public GetAllCaLamAdminResponse() {}
    
    public GetAllCaLamAdminResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public GetAllCaLamAdminResponse(boolean success, String message, List<CaLam> caLamList, int totalRecords) {
        this.success = success;
        this.message = message;
        this.caLamList = caLamList;
        this.totalRecords = totalRecords;
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
    
    public int getTotalRecords() {
        return totalRecords;
    }
    
    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }
    
    @Override
    public String toString() {
        return "GetAllCaLamAdminResponse{success=" + success + ", message='" + message + 
               "', caLamList=" + (caLamList != null ? caLamList.size() + " items" : "null") + 
               ", totalRecords=" + totalRecords + "}";
    }
}
