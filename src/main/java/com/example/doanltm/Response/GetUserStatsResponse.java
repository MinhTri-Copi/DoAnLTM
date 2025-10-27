package com.example.doanltm.Response;

import java.io.Serializable;
import java.util.Map;

public class GetUserStatsResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success;
    private String message;
    private Map<String, Integer> stats;

    public GetUserStatsResponse(boolean success, String message, Map<String, Integer> stats) {
        this.success = success;
        this.message = message;
        this.stats = stats;
    }

    public GetUserStatsResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Integer> getStats() {
        return stats;
    }

    @Override
    public String toString() {
        return "GetUserStatsResponse{" +
               "success=" + success +
               ", message='" + message + "'" +
               ", stats=" + stats +
               '}';
    }
}