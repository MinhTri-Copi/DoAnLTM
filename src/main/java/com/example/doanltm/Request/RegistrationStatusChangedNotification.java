package com.example.doanltm.Request;

import com.example.doanltm.Model.DangKy;

import java.io.Serializable;

/**
 * Lớp này đại diện cho một thông báo được gửi từ máy chủ đến máy khách
 * khi trạng thái của một đơn đăng ký ca làm việc thay đổi.
 */
public class RegistrationStatusChangedNotification implements Serializable {
    private static final long serialVersionUID = 1L;

    private final DangKy updatedDangKy;

    /**
     * Khởi tạo một thông báo mới.
     * @param updatedDangKy Đối tượng DangKy đã được cập nhật với trạng thái mới.
     */
    public RegistrationStatusChangedNotification(DangKy updatedDangKy) {
        this.updatedDangKy = updatedDangKy;
    }

    /**
     * Lấy đối tượng đăng ký đã được cập nhật.
     * @return Đối tượng DangKy.
     */
    public DangKy getUpdatedDangKy() {
        return updatedDangKy;
    }

    @Override
    public String toString() {
        return "RegistrationStatusChangedNotification{" +
                "updatedDangKy=" + updatedDangKy +
                '}';
    }
}