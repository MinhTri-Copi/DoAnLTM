package com.example.doanltm.Request;

import com.example.doanltm.Model.CaLam;
import java.io.Serializable;

public class CreateCaLamRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private CaLam caLam;
    
    public CreateCaLamRequest() {}
    
    public CreateCaLamRequest(CaLam caLam) {
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
        return "CreateCaLamRequest{caLam=" + caLam + "}";
    }
}
