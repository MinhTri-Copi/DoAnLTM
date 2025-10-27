package com.example.doanltm.Service;

import com.example.doanltm.Model.DangKy;
import com.example.doanltm.Request.NewRegistrationNotification;

/**
 * Một interface để lắng nghe các thông báo thay đổi trạng thái đăng ký từ server.
 */
public interface NotificationListener {
    /**
     * Được gọi khi nhận được thông báo về việc trạng thái đăng ký đã thay đổi.
     * @param updatedDangKy Đối tượng DangKy đã được cập nhật.
     */
    void onStatusChange(DangKy updatedDangKy);

    /**
     * Được gọi khi có thông báo về một lượt đăng ký mới (dành cho admin).
     * @param notification Thông báo đăng ký mới.
     */
    void onNewRegistration(NewRegistrationNotification notification);
}
