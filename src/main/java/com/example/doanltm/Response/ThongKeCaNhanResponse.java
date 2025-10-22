package com.example.doanltm.Response;

import java.io.Serializable;

public class ThongKeCaNhanResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private int monthlyTotal;
    private int normalShiftsCount;
    private int brokenShiftsCount;

    public ThongKeCaNhanResponse() {}

    public ThongKeCaNhanResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ThongKeCaNhanResponse(boolean success, String message, int monthlyTotal,
                                int normalShiftsCount, int brokenShiftsCount) {
        this.success = success;
        this.message = message;
        this.monthlyTotal = monthlyTotal;
        this.normalShiftsCount = normalShiftsCount;
        this.brokenShiftsCount = brokenShiftsCount;
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

    public int getMonthlyTotal() {
        return monthlyTotal;
    }

    public void setMonthlyTotal(int monthlyTotal) {
        this.monthlyTotal = monthlyTotal;
    }

    public int getNormalShiftsCount() {
        return normalShiftsCount;
    }

    public void setNormalShiftsCount(int normalShiftsCount) {
        this.normalShiftsCount = normalShiftsCount;
    }

    public int getBrokenShiftsCount() {
        return brokenShiftsCount;
    }

    public void setBrokenShiftsCount(int brokenShiftsCount) {
        this.brokenShiftsCount = brokenShiftsCount;
    }

    @Override
    public String toString() {
        return "ThongKeCaNhanResponse{success=" + success + ", message='" + message +
                "', monthlyTotal=" + monthlyTotal + ", normalShifts=" + normalShiftsCount +
                ", brokenShifts=" + brokenShiftsCount + "}";
    }
}
