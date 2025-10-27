package com.example.doanltm.Request;

import java.io.Serializable;

public class GetUserStatsRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private int userId;

    public GetUserStatsRequest(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "GetUserStatsRequest{userId=" + userId + '}';
    }
}