package com.example.doanltm.Test;

import com.example.doanltm.Model.*;
import com.example.doanltm.Request.GetCaLamRequest;
import com.example.doanltm.Request.LoginRequest;
import com.example.doanltm.Response.GetCaLamResponse;
import com.example.doanltm.Service.TCPClientService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Test utility để kiểm tra khả năng xử lý nhiều client đồng thời
 */
public class MultiClientTest {
    
    public static void main(String[] args) {
        System.out.println("🚀 Bắt đầu test nhiều client đồng thời...\n");
        
        // Test với 5 client cùng lúc
        testMultipleClients(5);
        
        System.out.println("\n✅ Hoàn thành test!");
    }
    
    /**
     * Test với nhiều client đồng thời
     */
    private static void testMultipleClients(int clientCount) {
        ExecutorService executor = Executors.newFixedThreadPool(clientCount);
        
        for (int i = 1; i <= clientCount; i++) {
            final int clientId = i;
            executor.submit(() -> {
                testSingleClient(clientId);
            });
        }
        
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
    
    /**
     * Test một client
     */
    private static void testSingleClient(int clientId) {
        TCPClientService client = new TCPClientService();
        
        try {
            System.out.println("🔗 Client " + clientId + " đang kết nối...");
            
            // 1. Kết nối
            if (!client.connect()) {
                System.err.println("❌ Client " + clientId + " không thể kết nối!");
                return;
            }
            System.out.println("✅ Client " + clientId + " kết nối thành công!");
            
            // 2. Test login (giả sử có user test)
            LoginRequest loginReq = new LoginRequest("user" + clientId + "@test.com", "123456");
            LoginResponse loginResp = client.login(loginReq);
            
            if (loginResp != null && loginResp.isSuccess()) {
                System.out.println("✅ Client " + clientId + " đăng nhập thành công!");
                
                // 3. Test lấy ca làm
                testGetCaLam(client, clientId);
                
            } else {
                System.out.println("⚠️ Client " + clientId + " đăng nhập thất bại (có thể do không có user test)");
            }
            
            // 4. Giữ kết nối một lúc
            Thread.sleep(2000 + (clientId * 500)); // Mỗi client sleep khác nhau
            
        } catch (Exception e) {
            System.err.println("❌ Client " + clientId + " lỗi: " + e.getMessage());
        } finally {
            // 5. Đóng kết nối
            client.disconnect();
            System.out.println("🔌 Client " + clientId + " đã ngắt kết nối");
        }
    }
    
    /**
     * Test lấy danh sách ca làm
     */
    private static void testGetCaLam(TCPClientService client, int clientId) {
        try {
            GetCaLamRequest req = new GetCaLamRequest(java.time.LocalDate.now());
            GetCaLamResponse resp = client.getCaLam(req);
            
            if (resp != null && resp.isSuccess()) {
                System.out.println("📋 Client " + clientId + " lấy được " + 
                    (resp.getCaLamList() != null ? resp.getCaLamList().size() : 0) + " ca làm");
            } else {
                System.out.println("⚠️ Client " + clientId + " không lấy được ca làm");
            }
        } catch (Exception e) {
            System.err.println("❌ Client " + clientId + " lỗi khi lấy ca làm: " + e.getMessage());
        }
    }
}