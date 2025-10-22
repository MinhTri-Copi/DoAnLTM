package com.example.doanltm.Request;

import com.example.doanltm.Model.CaLam;
import java.io.Serializable;

public class UpdateCaLamRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private CaLam caLam;
    
    public UpdateCaLamRequest() {}
    
    public UpdateCaLamRequest(CaLam caLam) {
        this.caLam = caLam;
    }
    
    public CaLam getCaLam() {
        return caLam;
    }
    
    public void setCaLam(CaLam caLam) {
        this.caLam = caLam;
    }
    
    @Override
    public String toString() {
        return "UpdateCaLamRequest{caLam=" + caLam + "}";
    }
}
