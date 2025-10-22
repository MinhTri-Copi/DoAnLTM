package com.example.doanltm.Request;

import java.io.Serializable;

public class DeleteCaLamRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int maCalam;
    
    public DeleteCaLamRequest() {}
    
    public DeleteCaLamRequest(int maCalam) {
        this.maCalam = maCalam;
    }
    
    public int getMaCalam() {
        return maCalam;
    }
    
    public void setMaCalam(int maCalam) {
        this.maCalam = maCalam;
    }
    
    @Override
    public String toString() {
        return "DeleteCaLamRequest{maCalam=" + maCalam + "}";
    }
}
