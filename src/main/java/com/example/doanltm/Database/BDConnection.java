package com.example.doanltm.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BDConnection {
    // Cập nhật theo thông tin database của bạn
    private static final String URL = "jdbc:mysql://localhost:3306/qldkca"; // Tên database của bạn
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Thay password MySQL của bạn

    private static Connection connection = null;

    private BDConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Kết nối Database thành công!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Không tìm thấy MySQL Driver!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Lỗi kết nối Database: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Đóng kết nối Database thành công!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi đóng kết nối Database!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Connection conn = BDConnection.getConnection();
        if (conn != null) {
            System.out.println("✅ Test connection successful!");
            closeConnection();
        }
    }
}
