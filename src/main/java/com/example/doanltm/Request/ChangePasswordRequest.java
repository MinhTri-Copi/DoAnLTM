package com.example.doanltm.Request;

import java.io.Serializable;

public class ChangePasswordRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private int userId;
    private String oldPassword;
    private String newPassword;

    public ChangePasswordRequest(int userId, String oldPassword, String newPassword) {
        this.userId = userId;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public int getUserId() {
        return userId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    @Override
    public String toString() {
        return "ChangePasswordRequest{" +
               "userId=" + userId +
               ", oldPassword='*****'" +
               ", newPassword='*****'" +
               '}';
    }
}